package com.example.facebook.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    @BindView(R.id.login_button)
    LoginButton loginButton;

    @BindView(R.id.profileInfo)
    TextView profileInfo;

    @BindView(R.id.profileAvatar)
    ImageView profileAvatar;

    private CallbackManager callbackManager = null;

    private ProfileTracker profileTracker = null;
    private Profile currentProfile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();
        initializeLoginButton();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeLoginButton() {
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                            Log.v("facebook - profile", currentProfile.getFirstName());
                            currentProfile = newProfile;
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                } else {
                    currentProfile = Profile.getCurrentProfile();

                }
                showUserInfo(loginResult);
                profileTracker.stopTracking();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(MainActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OnCancle");
            }


            @Override
            public void onError(FacebookException exception) {
                showError(exception);
            }
        });
    }

    private void showError(FacebookException exception) {
        Toast.makeText(this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, exception.getLocalizedMessage());
    }

    private void showUserInfo(LoginResult loginResult) {
        String profileInfoText = "";
        profileInfoText = profileInfoText.concat("First name: ").concat(currentProfile.getFirstName());
        profileInfoText = profileInfoText.concat("Last name: ").concat(currentProfile.getLastName());
        profileInfoText = profileInfoText.concat("ID").concat(currentProfile.getId());
        profileInfoText = profileInfoText.concat("Access token").concat(loginResult.getAccessToken().getToken());
        profileInfo.setText(profileInfoText);

        loadAvatar(currentProfile.getId());
    }

    private void loadAvatar(String userId) {
        URL imageURL = null;
        try {
            imageURL = new URL("https://graph.facebook.com/" + userId + "/picture?type=large");
            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            profileAvatar.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "failed to load avatar, ".concat(e.getMessage()));
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "failed to load avatar, ".concat(e.getMessage()));
        }

    }
}
