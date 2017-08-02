package com.reissgrvs.spotifyplaylisttool.SpotifyAPI;

import android.util.Base64;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;



/**
 * Creates and configures a REST adapter for Spotify Web API.
 *
 * Basic usage:
 * SpotifyApi wrapper = new SpotifyApi();
 *
 * Setting access token is optional for certain endpoints
 * so if you know you'll only use the ones that don't require authorisation
 * you can skip this step:
 * wrapper.setAccessToken(authenticationResponse.getAccessToken());
 */
public class SpotifyAuthApi {

    /**
     * Main Spotify Web API endpoint
     */

    public static final String SPOTIFY_AUTH_API_ENDPOINT = "https://accounts.spotify.com";
    private static String mClientID = "3c0e3597d0304162a89f4c2c6002e2a7";
    private static String mClientSecret = "7192054f745a4f799a6b1d6c3a5f2de6";

    /**
     * The request interceptor that will add the header with OAuth
     * token to every request made with the wrapper.
     */
    private class WebApiAuthenticator implements RequestInterceptor {
        @Override
        public void intercept(RequestFacade request) {
            String clientComposition =  mClientID + ":" + mClientSecret;
            byte[] encodedBytes = Base64.encode(clientComposition.getBytes(), Base64.NO_WRAP);
            request.addHeader("Authorization", "Basic " + new String(encodedBytes));
            request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        }
    }

    private final SpotifyAuthService mSpotifyService;




    private SpotifyAuthService init(Executor httpExecutor, Executor callbackExecutor) {

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(httpExecutor, callbackExecutor)
                .setEndpoint(SPOTIFY_AUTH_API_ENDPOINT)
                .setRequestInterceptor(new WebApiAuthenticator())
                .build();

        return restAdapter.create(SpotifyAuthService.class);
    }


    public SpotifyAuthApi() {
        Executor httpExecutor = Executors.newSingleThreadExecutor();
        MainThreadExecutor callbackExecutor = new MainThreadExecutor();
        mSpotifyService = init(httpExecutor, callbackExecutor);
    }


    public SpotifyAuthService getService() {
        return mSpotifyService;
    }
}


