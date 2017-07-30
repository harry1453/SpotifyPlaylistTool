package com.reissgrvs.spotifyplaylisttool.MultiplaylistList;

import android.content.Context;
import android.media.session.MediaSession;
import android.os.AsyncTask;
import android.util.Log;

import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import com.reissgrvs.spotifyplaylisttool.Util.MultiPlaylistStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Result;
import retrofit.client.Response;

import static android.R.id.list;

/**
 * Created by Reiss on 16/07/2017.
 */

public class MultiplaylistUpdater {

    public static ArrayList<PlaylistTrack> unpackPlaylists(List<Playlist> playlists){
        ArrayList<PlaylistTrack> tracks = new ArrayList<>();

        for (Playlist playlist : playlists){
            tracks.addAll(playlist.tracks.items);
        }

        return tracks;
    }


    public static void updateSpotifyPlaylist(String userID, String playlistID, ArrayList<PlaylistTrack> trackArray){

        //TODO: Make more robust call replace for first 50 tracks, then repeatedly each 50 add to the playlist
        String trackList = "";
        for (PlaylistTrack playlistTrack : trackArray){
            trackList += playlistTrack.track.uri + ",";
        }

        SpotifyAPIManager.getService().replaceTracksInPlaylist(userID, playlistID, trackList.substring(0,trackList.length()-1), new HashMap<>() ,new SpotifyCallback<Result>() {
            @Override
            public void success(Result result, Response response) {
                Log.d("Track Add", "Successfully added tracks to playlist");
            }
            @Override
            public void failure(SpotifyError error){
                Log.e("Track Add", "Failed to add tracks or something");
            }

        });

    }

    public static void updateAllMultiplaylists(Context context){
        MultiPlaylistStore.loadMultiPlaylistFile(context);
        HashMap<String, ArrayList<String>> multiPlaylists = MultiPlaylistStore.getMultiplaylistStore();
        String userID = TokenStore.getUserId(context);
        Log.d("UpdateTesting", "updateAllMultiplaylists");
        for (String multiplaylistID : multiPlaylists.keySet()){

            final ArrayList<String> childPlaylists = multiPlaylists.get(multiplaylistID);
            final ArrayList<PlaylistTrack> trackList = new ArrayList<>();

            //Get all tracks of all child playlists
            AsyncTask fetchMultiTracksTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] a) {
                    for (String childPlaylist : childPlaylists){
                        trackList.addAll(MultiplaylistUpdater.getPlaylistTracksFromId(childPlaylist));
                    }
                    return null;
                }
            };

            try {
                fetchMultiTracksTask.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            updateSpotifyPlaylist(userID, multiplaylistID, trackList);
        }
    }

    public static List<PlaylistTrack> getPlaylistTracksFromId(String id) {
        String[] ids = id.split("-");
        Playlist playlist = SpotifyAPIManager.getService().getPlaylist(ids[1], ids[0]);
        return playlist.tracks.items;
    }
}
