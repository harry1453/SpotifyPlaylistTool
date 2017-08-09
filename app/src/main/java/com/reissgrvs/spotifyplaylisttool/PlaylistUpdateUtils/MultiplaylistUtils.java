package com.reissgrvs.spotifyplaylisttool.PlaylistUpdateUtils;

import android.content.Context;
import android.os.AsyncTask;


import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.AccessTokenInfo;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import com.reissgrvs.spotifyplaylisttool.Util.MultiplaylistStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;

import static com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore.getRefreshToken;


public class MultiplaylistUtils {

    private static ArrayList<PlaylistTrack> unpackPlaylists(List<Playlist> playlists){
        ArrayList<PlaylistTrack> tracks = new ArrayList<>();

        for (Playlist playlist : playlists){
            tracks.addAll(playlist.tracks.items);
        }
        return tracks;
    }

    private static void syncPlaylist(final String userID, final String playlistID){
        List<String> childIDs = MultiplaylistStore.getMulti(playlistID);

        ArrayList<PlaylistTrack> childTracks = getPlaylistsFromId(childIDs);

        //ArrayList<PlaylistTrack> childTracks = unpackPlaylists(childPlaylists);

        updateSpotify(userID,playlistID, childTracks);
    }



    private static void updateSpotify(final String userID, final String playlistID, final ArrayList<PlaylistTrack> newTrackArray){
        if(!userID.equals("NO ID") && !playlistID.equals("NO ID")) {

            ArrayList<PlaylistTrack> oldTrackArray = getPlaylistTracks(userID, playlistID);
            PlaylistUpdateUtils.updateSpotifyPlaylist(userID, playlistID, oldTrackArray, newTrackArray);
        }
    }

    private static ArrayList<PlaylistTrack> getPlaylistTracks(String userID, String playlistID){
        int i = 0;
        int pageSize = 100;
        boolean endOfPlaylist = false;

        ArrayList<PlaylistTrack> trackArray = new ArrayList<>();
        while (!endOfPlaylist){
            Map<String, Object> query = new HashMap<>();
            query.put("offset", i);
            ArrayList<PlaylistTrack> fetchedTrackArray = new ArrayList<>(SpotifyAPIManager.getService().getPlaylistTracks(userID, playlistID, query).items);
            int fetchedSize = fetchedTrackArray.size();
            if (!fetchedTrackArray.isEmpty() && fetchedSize == pageSize) {

                trackArray.addAll(fetchedTrackArray);
                i += pageSize;
            }
            else {
                if (fetchedSize > 0){
                    trackArray.addAll(fetchedTrackArray);
                }
                endOfPlaylist = true;
            }
        }

        return trackArray;
    }
    private static ArrayList<PlaylistTrack> getPlaylistsFromId(List<String> childPlaylistIDs){
        ArrayList<PlaylistTrack> childPlaylists = new ArrayList<>();
        for (String childID : childPlaylistIDs) {
            childPlaylists.addAll(getPlaylistFromId(childID));
        }
        return childPlaylists;
    }

    private static ArrayList<PlaylistTrack> getPlaylistFromId(String id) {
        String[] ids = id.split("-");
        return getPlaylistTracks(ids[1], ids[0]);
    }

    public static void executeSyncPlaylistTask(final String userID, final String playlistID){
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... unused) {
                MultiplaylistUtils.syncPlaylist(userID, playlistID);
                return null;
            }
        }.execute();
    }


    public static void updateAllMultiplaylists(final Context context, final String userID){
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... unused) {

                AccessTokenInfo accessTokenInfo = SpotifyAPIManager.getAuthService().refreshAccessToken("refresh_token", getRefreshToken(context));
                TokenStore.setToken(context, accessTokenInfo);
                //Get multistore
                MultiplaylistStore.loadMultiPlaylistFile(context);
                Set<String> playlistIDs = MultiplaylistStore.getPlaylistIDs();

                for(String curPlaylistID : playlistIDs ){
                    executeSyncPlaylistTask(userID, curPlaylistID);
                }

                return null;
            }
        }.execute();

    }
}
