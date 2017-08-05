package com.reissgrvs.spotifyplaylisttool.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final String CLIENT_ID = "3c0e3597d0304162a89f4c2c6002e2a7";
    private static final String REDIRECT_URI = "testschema://callback";
    private static final int REQUEST_CODE = 6969;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String refreshToken = TokenStore.getRefreshToken(this);
        if (refreshToken == null) {
            setContentView(R.layout.activity_login);
        } else {
            startMainActivity();
        }
    }

    public void onLoginButtonClicked(View view) {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.CODE, REDIRECT_URI)
                .setScopes(new String[]{"playlist-read","playlist-read-private","playlist-read-collaborative","playlist-modify-public", "playlist-modify-private","user-library-read","user-top-read"})
                .setShowDialog(true)
                .build();
        Log.d(TAG, "Login Button Clicked");
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case CODE:
                    logMessage("Got code: " + response.getCode());

                    TokenStore.fetchAuthRefreshTokens(this, response.getCode());
                    TokenStore.fetchUserId(this);
                    Intent mainActivity = MainActivity.createIntent(this);
                    startActivity(mainActivity);
                    break;

                // Auth flow returned an error
                case ERROR:
                    logError("Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    logError("Auth result: " + response.getType());
            }
        }
    }


    private void startMainActivity() {
        TokenStore.refreshAuthToken(this);


        Intent intent = MainActivity.createIntent(this);
        startActivity(intent);
        finish();
    }

    private void logError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }
}
