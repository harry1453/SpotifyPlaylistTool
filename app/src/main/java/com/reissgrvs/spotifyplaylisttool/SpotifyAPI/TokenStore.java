package com.reissgrvs.spotifyplaylisttool.SpotifyAPI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.reissgrvs.spotifyplaylisttool.Activities.MainActivity;
import com.spotify.sdk.android.authentication.AuthenticationClient;

import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TokenStore
{

    private static final String TAG = TokenStore.class.getSimpleName();
    private static final String TOKEN_NAME = "webapi.credentials";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String EXPIRES_AT = "expires_at";
    private static final String USER_ID = "user_id";
    public static Boolean mStart = false;

    public static void setToken(final Context context, AccessTokenInfo accessTokenInfo) {
        final Context appContext = context.getApplicationContext();

        long now = System.currentTimeMillis();
        long expiresAt = now + TimeUnit.SECONDS.toMillis(Integer.parseInt(accessTokenInfo.expires_in));

        SharedPreferences sharedPref = getSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, accessTokenInfo.access_token);
        editor.putLong(EXPIRES_AT, expiresAt);

        if(accessTokenInfo.refresh_token != null){
            editor.putString(REFRESH_TOKEN, accessTokenInfo.refresh_token);
        }

        editor.apply();

        SpotifyAPIManager.setToken(accessTokenInfo.access_token);

        fetchUserId(context);

    }

    public static void fetchAuthRefreshTokens(final Context appContext, String code){

        SpotifyAPIManager.getAuthService().requestAccessTokens("authorization_code", code, "testschema://callback", new Callback<AccessTokenInfo>() {
            @Override
            public void success(AccessTokenInfo accessTokenInfo, Response response) {
                TokenStore.setToken(appContext, accessTokenInfo);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("fetchAuthRefreshTokens", "Failed to fetch tokens");
                Log.e("fetchAuthRefreshTokens", error.toString());
            }
        });
    }

    public static void refreshAuthToken(final Context appContext){

        SpotifyAPIManager.getAuthService().refreshAccessToken("refresh_token", getRefreshToken(appContext), new Callback<AccessTokenInfo>() {
            @Override
            public void success(AccessTokenInfo accessTokenInfo, Response response) {
                TokenStore.setToken(appContext, accessTokenInfo);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("fetchAuthRefreshTokens", "Failed to fetch tokens");
                Log.e("fetchAuthRefreshTokens", error.toString());
            }
        });

    }

    public static void fetchUserId(final Context appContext){
        final SharedPreferences  sharedPref = getSharedPreferences(appContext);
        String userID = sharedPref.getString(USER_ID, null);

        if(userID == null){
            SpotifyAPIManager.getService().getMe(new SpotifyCallback<UserPrivate>() {
                @Override
                public void success(UserPrivate userPrivate, Response response) {

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(USER_ID, userPrivate.id);
                    editor.apply();
                    Log.d("UserID", userPrivate.id);
                    checkStart(appContext);
                }

                @Override
                public void failure(SpotifyError error) {
                    Log.e("TokenStore", "Failure to retrieve user id");
                }
            });
        }
    }

    private static void checkStart(Context appContext) {
        if(mStart) {
            Intent intent = MainActivity.createIntent(appContext);
            appContext.startActivity(intent);
        }
    }

    public static void clearToken(Context context) {
        Context appContext = context.getApplicationContext();

        SharedPreferences sharedPref = getSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, null);
        editor.putString(REFRESH_TOKEN, null);
        editor.putLong(EXPIRES_AT, 0L);
        editor.putString(USER_ID, null);
        editor.apply();

        AuthenticationClient.clearCookies(context);
    }

    private static SharedPreferences getSharedPreferences(Context appContext) {
        return appContext.getSharedPreferences(TOKEN_NAME, Context.MODE_PRIVATE);
    }

    public static String getAuthToken(Context context) {
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);

        String token = sharedPref.getString(ACCESS_TOKEN, null);
        long expiresAt = sharedPref.getLong(EXPIRES_AT, 0L);
        if(System.currentTimeMillis() > expiresAt){
            return null;
        }

        return token;
    }

    public static String getRefreshToken(Context context) {
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);

        return sharedPref.getString(REFRESH_TOKEN, null);
    }

    public static String getUserId(Context context) {

        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);
        String userId = sharedPref.getString(USER_ID, null);

        return userId;
    }

}
