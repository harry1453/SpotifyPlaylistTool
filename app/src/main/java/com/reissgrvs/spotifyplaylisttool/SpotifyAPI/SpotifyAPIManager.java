package com.reissgrvs.spotifyplaylisttool.SpotifyAPI;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;


public class SpotifyAPIManager {
    private static final SpotifyApi spotifyApi = new SpotifyApi();
    private static final SpotifyAuthApi spotifyAuthApi = new SpotifyAuthApi();

    public static SpotifyService getService(){
        return spotifyApi.getService();
    }

    public static SpotifyAuthService getAuthService(){
        return spotifyAuthApi.getService();
    }

    public static void setToken(String token){
        spotifyApi.setAccessToken(token);
    }


}
