package com.reissgrvs.spotifyplaylisttool.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.reissgrvs.spotifyplaylisttool.CustomTrack;
import com.reissgrvs.spotifyplaylisttool.PlaylistUpdateUtils.PlaylistUpdateUtils;
import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.SongList.SongListFragment;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;


public class PlaylistActivity extends AppCompatActivity  {


    private SongListFragment fragment = new SongListFragment();
    private String playlistID = "NO ID";
    private String userID = "NO ID";
    public static final String PLAYLIST_ID =  "playlistID";
    public static final String USER_ID =  "userID";

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
            //noinspection ConstantConditions
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
        args.putString(PLAYLIST_ID, playlistID);
        args.putString(USER_ID, userID);

        if (savedInstanceState == null) {
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_playlist, fragment)
                    .commit();
        }

        //Set up FAB to launch the Add to playlist activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_songs);
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
                final ArrayList<CustomTrack> customTrackArrayList = new ArrayList<>();
                for(Track cur : result){
                    customTrackArrayList.add(new CustomTrack(cur));
                }

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        PlaylistUpdateUtils.addTracks(userID, playlistID, customTrackArrayList);
                        return null;
                    }
                }.execute();


            }
        }
    }


}
