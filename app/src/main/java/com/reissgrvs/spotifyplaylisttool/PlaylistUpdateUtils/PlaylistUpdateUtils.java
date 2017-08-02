package com.reissgrvs.spotifyplaylisttool.PlaylistUpdateUtils;

import android.util.Log;

import com.reissgrvs.spotifyplaylisttool.CustomTrack;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.SnapshotId;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackToRemove;
import kaaes.spotify.webapi.android.models.TracksToRemove;
import retrofit.client.Response;

/**
 * Created by reissgrvs on 01/08/17.
 */

public class PlaylistUpdateUtils {

    public static void updateSpotifyPlaylist(String userID, String playlistID, ArrayList<PlaylistTrack> oldTrackArray, ArrayList<PlaylistTrack> newTrackArray){

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

            Map<String, Object> body = new HashMap<>();
            Map<String, Object> query = new HashMap<>();

            String trackList = "";
            for (CustomTrack track : newTracks) {
                trackList += track.mTrack.uri + ",";
            }
            query.put("uris", trackList.substring(0, trackList.length() - 1));

            SpotifyAPIManager.getService().addTracksToPlaylist(userID, playlistID, query, body);
        }
    }

    public static void removeTracks(String userID, String playlistID, Collection<CustomTrack> oldTracks){

        if(!oldTracks.isEmpty()) {
            TracksToRemove tracksToRemove = packageTracksToRemove(oldTracks);
            SpotifyAPIManager.getService().removeTracksFromPlaylist(userID, playlistID, tracksToRemove);
        }
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
}
