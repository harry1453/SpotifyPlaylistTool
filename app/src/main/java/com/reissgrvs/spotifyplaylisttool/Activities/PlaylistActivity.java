package com.reissgrvs.spotifyplaylisttool.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.SongList.SongListFragment;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.SnapshotId;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

public class PlaylistActivity extends AppCompatActivity  {


    private SongListFragment fragment = new SongListFragment();
    private FloatingActionButton fab;
    private String playlistID = "NO ID";
    private String userID = "NO ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Layout set up
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_playlist);
        setSupportActionBar(toolbar);
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String playlistTitle = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            getSupportActionBar().setTitle(playlistTitle);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Get passed IDs from Main Activity
        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_SUBJECT) && intentThatStartedThisActivity.hasExtra(Intent.EXTRA_USER)){
            playlistID = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_SUBJECT);
            userID = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_USER);
        }

        //Set up Song List fragment
        Bundle args = new Bundle();
        args.putString("playlistID", playlistID);
        args.putString("userID", userID);

        if (savedInstanceState == null) {
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_playlist, fragment)
                    .commit();
        }

        //Set up FAB to launch the Add to playlist activity
        fab = (FloatingActionButton) findViewById(R.id.fab_add_songs);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = SongSearchActivity.createIntent(getBaseContext());
                intent.putParcelableArrayListExtra("playlistTracks", fragment.getTracks());
                startActivityForResult(intent, 1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){

                //Set up arguments
                ArrayList<Track> result = data.getParcelableArrayListExtra("result");

                Map<String, Object> body = new HashMap<>();
                Map<String, Object> query = new HashMap<>();

                String trackList = "";
                for (Track track : result){
                    trackList += track.uri + ",";
                }
                query.put( "uris", trackList.substring(0,trackList.length()-1) );


                //TODO: Make sure to handle this properly when there is more than 50
                SpotifyAPIManager.getService().addTracksToPlaylist(userID,playlistID, query, body ,new SpotifyCallback<Pager<PlaylistTrack>>() {
                    @Override
                    public void success(Pager<PlaylistTrack> tracks, Response response) {
                        recreate();
                    }

                    @Override
                    public void failure(SpotifyError error){
                        Log.e("Track Add", "Failed to add tracks or something");
                    }
                });
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write some code if there's no result
            }
        }
    }


}
