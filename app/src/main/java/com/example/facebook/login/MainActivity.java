package com.example.facebook.login;

import android.content.Intent;
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
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private CallbackManager callbackManager = null;
    private Profile currentProfile = null;
    private TextView profileInfo = null;
    private LoginButton loginButton = null;
    private ImageView profileAvatar = null;
    private ProfileTracker profileTracker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        profileInfo = this.findViewById(R.id.profileInfo);
        loginButton = this.findViewById(R.id.login_button);
        profileAvatar = this.findViewById(R.id.profileAvatar);

        setFacebookPermission();
        initializeLoginButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setFacebookPermission() {
        loginButton.setReadPermissions("email");
    }

    private void initializeLoginButton() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                if (Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                            Log.v("facebook - profile", newProfile.getFirstName());
                            currentProfile = newProfile;
                            showUserInfo(loginResult);
                        }
                    };
                } else {
                    currentProfile = Profile.getCurrentProfile();
                    showUserInfo(loginResult);
                }
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

    private void showUserInfo(final LoginResult loginResult) {
        String profileInfoText = "";
        profileInfoText = profileInfoText.concat("First name: ").concat(currentProfile.getFirstName().concat("\n"));
        profileInfoText = profileInfoText.concat("Last name: ").concat(currentProfile.getLastName().concat("\n"));
        profileInfoText = profileInfoText.concat("ID: ").concat(currentProfile.getId().concat("\n"));
        profileInfoText = profileInfoText.concat("Access token: ").concat(loginResult.getAccessToken().getToken().concat("\n"));
        profileInfo.setText(profileInfoText);

        loadAvatar(currentProfile.getId());
    }

    private void loadAvatar(String userId) {
        String imageUrl = "https://graph.facebook.com/" + userId + "/picture?type=normal";
        Picasso.with(MainActivity.this).load(imageUrl).into(profileAvatar);
    }
}
