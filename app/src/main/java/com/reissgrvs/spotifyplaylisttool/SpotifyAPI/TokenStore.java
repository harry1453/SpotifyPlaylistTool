package com.reissgrvs.spotifyplaylisttool.SpotifyAPI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.spotify.sdk.android.authentication.AuthenticationClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.UserPrivate;

public class TokenStore
{

    private static final String TOKEN_NAME = "webapi.credentials";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String EXPIRES_AT = "expires_at";
    private static final String USER_ID = "user_id";

    public static void setToken(final Context context, AccessTokenInfo accessTokenInfo) {
        final Context appContext = context.getApplicationContext();

        long now = System.currentTimeMillis();
        long expiresAt = now + TimeUnit.SECONDS.toMillis(Integer.parseInt(accessTokenInfo.getExpiry()));

        SharedPreferences sharedPref = getSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, accessTokenInfo.getAccessToken());
        editor.putLong(EXPIRES_AT, expiresAt);

        if(accessTokenInfo.getRefreshToken() != null){
            editor.putString(REFRESH_TOKEN, accessTokenInfo.getRefreshToken());
        }

        editor.apply();

        SpotifyAPIManager.setToken(accessTokenInfo.getAccessToken());

    }

    public static void fetchAuthRefreshTokens(final Context appContext,final String code){


        AsyncTask<Void, Void, Void> fetchTokensTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                AccessTokenInfo accessTokenInfo = SpotifyAPIManager.getAuthService().requestAccessTokens("authorization_code", code, "testschema://callback");
                TokenStore.setToken(appContext, accessTokenInfo);
                return null;
            }
        };

        try {
            fetchTokensTask.execute().get();
        }
        catch (InterruptedException | ExecutionException ignored) {}
    }

    public static void refreshAuthToken(final Context appContext){

        AsyncTask<Void,Void,Void> refreshAuthTokenTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                AccessTokenInfo accessTokenInfo = SpotifyAPIManager.getAuthService().refreshAccessToken("refresh_token", getRefreshToken(appContext));
                TokenStore.setToken(appContext, accessTokenInfo);
                return null;
            }
        };

        try {
            refreshAuthTokenTask.execute().get();
        }
        catch (ExecutionException | InterruptedException ignored) {}

    }

    public static void fetchUserId(final Context appContext)  {
        final SharedPreferences  sharedPref = getSharedPreferences(appContext);
        String userID = sharedPref.getString(USER_ID, null);
        long expiresAt = sharedPref.getLong(EXPIRES_AT, 0L);

        if(userID == null || System.currentTimeMillis() > expiresAt){

            AsyncTask<Void,Void,Void> getMeTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    UserPrivate userPrivate = SpotifyAPIManager.getService().getMe();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(USER_ID, userPrivate.id);
                    editor.apply();
                    return null;
                }
            };

            try {
                getMeTask.execute().get();
            } catch (ExecutionException | InterruptedException ignored) {}
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
            refreshAuthToken(context);
            return sharedPref.getString(ACCESS_TOKEN, null);
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

        return sharedPref.getString(USER_ID, null);
    }

}
