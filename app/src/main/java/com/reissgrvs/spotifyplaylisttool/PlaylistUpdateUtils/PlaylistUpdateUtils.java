package com.reissgrvs.spotifyplaylisttool.PlaylistUpdateUtils;


import android.os.AsyncTask;
import android.util.Log;

import com.reissgrvs.spotifyplaylisttool.CustomTrack;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackToRemove;
import kaaes.spotify.webapi.android.models.TracksToRemove;
import retrofit.RetrofitError;

import static android.os.Debug.waitForDebugger;


public class PlaylistUpdateUtils {

    static void updateSpotifyPlaylist(String userID, String playlistID, ArrayList<PlaylistTrack> oldTrackArray, ArrayList<PlaylistTrack> newTrackArray){
        //Populate oldTrackSet and newTrackSet
        Set<CustomTrack> oldTrackSet = new HashSet<>();
        for (PlaylistTrack pTrack : oldTrackArray){
            oldTrackSet.add(new CustomTrack(pTrack.track));
        }

        Set<CustomTrack> newTrackSet = new HashSet<>();
        for (PlaylistTrack pTrack : newTrackArray){
            newTrackSet.add(new CustomTrack(pTrack.track));
        }

        //Find tracks unique to either set, keeping seperate
        //Tracks left in newTrackSet are to be added
        List<CustomTrack> removeTracks = new ArrayList<>();
        for (CustomTrack oldTrack : oldTrackSet){
            if(newTrackSet.contains(oldTrack)) {
                newTrackSet.remove(oldTrack);
            }
            else{
                removeTracks.add(oldTrack);
            }
        }

        removeTracks(userID, playlistID, removeTracks);
        addTracks(userID, playlistID, newTrackSet);

    }


    public static void addTracks(String userID, String playlistID, Collection<CustomTrack> newTracks){
        if (!newTracks.isEmpty()) {

            ArrayList<CustomTrack> tracks = new ArrayList<>(newTracks);
            int pageLength = 50;

            for(int i = 0; i < (tracks.size() / pageLength); i++){
                addTrackChunk(userID,playlistID, tracks.subList(pageLength*i, pageLength*(i+1)));
            }

            if(tracks.size() % pageLength > 0){
                addTrackChunk(userID, playlistID ,tracks.subList(tracks.size() - tracks.size() % pageLength , tracks.size()));
            }
        }
    }

    private static void addTrackChunk(String userID, String playlistID, Collection<CustomTrack> tracksChunk){
        List<String> trackList = new ArrayList<>();
        for (CustomTrack track : tracksChunk){
            if(track.mTrack.uri.startsWith("spotify:track:")){
                trackList.add(track.mTrack.uri);
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("uris", trackList);
        try {
            SpotifyAPIManager.getService().addTracksToPlaylist(userID, playlistID, null, body);
        }catch (RetrofitError error)
        {
            Log.e("retrofiterror" ,error.getResponse().getReason());
        }
    }
    private static void removeTracks(String userID, String playlistID, Collection<CustomTrack> oldTracks){
        if(!oldTracks.isEmpty()) {
            ArrayList<CustomTrack> tracks = new ArrayList<>(oldTracks);
            int pageLength = 50;

            for(int i = 0; i < (tracks.size() / pageLength); i++){
                TracksToRemove tracksToRemove = packageTracksToRemove(tracks.subList(i*pageLength, (i+1)*pageLength));
                SpotifyAPIManager.getService().removeTracksFromPlaylist(userID, playlistID, tracksToRemove);
            }

            if(tracks.size() % pageLength > 0){
                TracksToRemove tracksToRemove = packageTracksToRemove(tracks.subList(tracks.size() - tracks.size() % pageLength , tracks.size()));
                SpotifyAPIManager.getService().removeTracksFromPlaylist(userID, playlistID, tracksToRemove);
            }

        }
    }

    public static void removeTrackTask(final String userID, final String playlistID, final Track oldTrack){
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... unused) {

                if(oldTrack != null) {
                    CustomTrack remove = new CustomTrack(oldTrack);
                    ArrayList<CustomTrack> removeList = new ArrayList<>();
                    removeList.add(remove);
                    TracksToRemove tracksToRemove = packageTracksToRemove(removeList);
                    SpotifyAPIManager.getService().removeTracksFromPlaylist(userID, playlistID, tracksToRemove);
                }
                return null;
            }
        }.execute();

    }

    public static void addTrackTask(final String userID, final String playlistID, final Track track, final Integer position){
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... unused) {

                if (track != null) {

                    Map<String, Object> body = new HashMap<>();
                    Map<String, Object> query = new HashMap<>();

                    query.put("uris", track.uri);
                    query.put("position", position.toString());

                    SpotifyAPIManager.getService().addTracksToPlaylist(userID, playlistID, query, body);
                }
                return null;
            }
        }.execute();

    }

    private static TracksToRemove packageTracksToRemove(Collection<CustomTrack> tracksToRemove){
        TracksToRemove packagedTracks = new TracksToRemove();
        packagedTracks.tracks = new ArrayList<>();

        for(CustomTrack track : tracksToRemove){
            TrackToRemove tracktoRemove = new TrackToRemove();
            tracktoRemove.uri = track.mTrack.uri;
            packagedTracks.tracks.add(tracktoRemove);
        }

        return packagedTracks;
    }

    public static void moveTrackTask(final String userID, final String playlistID, final int start, final int end){
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... unused) {

                Map<String, Object> body = new HashMap<>();
                body.put("range_start", start);
                body.put("insert_before", end);

                SpotifyAPIManager.getService().reorderPlaylistTracks(userID, playlistID, body);

                return null;
            }
        }.execute();
    }
}
