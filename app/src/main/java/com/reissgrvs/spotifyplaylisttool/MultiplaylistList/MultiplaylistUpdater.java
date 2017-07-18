package com.reissgrvs.spotifyplaylisttool.MultiplaylistList;

import android.util.Log;

import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Result;
import retrofit.client.Response;

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

        Map<String, Object> body = new HashMap<>();

        //TODO: Make more robust call replace for first 50 tracks, then repeatedly each 50 add to the playlist
        String trackList = "";
        for (PlaylistTrack playlistTrack : trackArray){
            trackList += playlistTrack.track.uri + ",";
        }

        SpotifyAPIManager.getService().replaceTracksInPlaylist(userID, playlistID, trackList.substring(0,trackList.length()-1), body ,new SpotifyCallback<Result>() {
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


}
