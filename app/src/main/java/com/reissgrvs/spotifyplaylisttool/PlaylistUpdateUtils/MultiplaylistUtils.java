package com.reissgrvs.spotifyplaylisttool.PlaylistUpdateUtils;

import android.content.Context;
import android.media.session.MediaSession;
import android.os.AsyncTask;
import android.util.Log;

import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.AccessTokenInfo;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import com.reissgrvs.spotifyplaylisttool.Util.MultiPlaylistStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Result;
import retrofit.client.Response;

import static android.R.id.list;
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
        List<String> childIDs = MultiPlaylistStore.getMulti(playlistID);

        List<Playlist> childPlaylists = getPlaylistsFromId(childIDs);

        ArrayList<PlaylistTrack> childTracks = unpackPlaylists(childPlaylists);

        updateSpotify(userID,playlistID, childTracks);
    }


    private static void syncPlaylist(final String userID, final String playlistID, List<Playlist> childPlaylists){

        ArrayList<PlaylistTrack> childTracks = unpackPlaylists(childPlaylists);

        updateSpotify(userID,playlistID, childTracks);
    }

    private static void updateSpotify(final String userID, final String playlistID, final ArrayList<PlaylistTrack> newTrackArray){

        ArrayList<PlaylistTrack> oldTrackArray = new ArrayList<>(SpotifyAPIManager.getService().getPlaylist(userID,playlistID).tracks.items);
        PlaylistUpdateUtils.updateSpotifyPlaylist(userID,playlistID, oldTrackArray, newTrackArray);

    }

    private static List<Playlist> getPlaylistsFromId(List<String> childPlaylistIDs){
        List<Playlist> childPlaylists = new ArrayList<>();
        for (String childID : childPlaylistIDs) {
            childPlaylists.add(getPlaylistFromId(childID));
        }
        return childPlaylists;
    }

    private static Playlist getPlaylistFromId(String id) {
        String[] ids = id.split("-");
        Playlist playlist = SpotifyAPIManager.getService().getPlaylist(ids[1], ids[0]);
        return playlist;
    }

    private static void executeSyncPlaylistTask(final String userID, final String playlistID){
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... unused) {
                MultiplaylistUtils.syncPlaylist(userID, playlistID);
                return null;
            }
        }.execute();
    }

    public static void executeSyncPlaylistTask(final String userID, final String playlistID, final List<Playlist> childPlaylists){
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... unused) {
                MultiplaylistUtils.syncPlaylist(userID, playlistID, childPlaylists);
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
                MultiPlaylistStore.loadMultiPlaylistFile(context);
                Set<String> playlistIDs = MultiPlaylistStore.getPlaylistIDs();

                for(String curPlaylistID : playlistIDs ){
                    //TODO: Update each playlist
                    executeSyncPlaylistTask(userID, curPlaylistID);
                }

                return null;
            }
        }.execute();

    }
}
