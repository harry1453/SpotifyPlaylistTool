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

import com.reissgrvs.spotifyplaylisttool.MultiplaylistList.MultiplaylistListFragment;
import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;
import com.reissgrvs.spotifyplaylisttool.Util.MultiPlaylistStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

public class MultiplaylistActivity extends AppCompatActivity  {


    private MultiplaylistListFragment fragment = new MultiplaylistListFragment();
    private FloatingActionButton fab;
    private String playlistID = "NO ID";
    private String userID = "NO ID";
    private ArrayList<String> childPlaylists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_multiplaylist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_multiplaylist);
        setSupportActionBar(toolbar);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("childPlaylists")) {
            childPlaylists = intentThatStartedThisActivity.getStringArrayListExtra("childPlaylists");
        }
        Log.d("Multi", "Child playlists of selected multi: " + childPlaylists.toString());
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String playlistTitle = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            getSupportActionBar().setTitle(playlistTitle);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_SUBJECT) && intentThatStartedThisActivity.hasExtra(Intent.EXTRA_USER)){
            playlistID = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_SUBJECT);
            userID = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_USER);
        }

        Bundle args = new Bundle();
        args.putString("playlistID", playlistID);
        args.putString("userID", userID);
        args.putStringArrayList("childPlaylists", childPlaylists);
        if (savedInstanceState == null) {
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_multiplaylist, fragment)
                    .commit();
        }

        fab = (FloatingActionButton) findViewById(R.id.fab_add_playlist);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = PlaylistSearchActivity.createIntent(getBaseContext());
                intent.putParcelableArrayListExtra("playlists", fragment.getPlaylists());
                startActivityForResult(intent, 1);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                ArrayList<PlaylistSimple> result = data.getParcelableArrayListExtra("result");

                for (PlaylistSimple playlistSimple : result){
                    if (!childPlaylists.contains(playlistSimple.id)){
                        childPlaylists.add(playlistSimple.id+"-"+playlistSimple.owner.id);
                    }
                }

                MultiPlaylistStore.addToMulti(playlistID, childPlaylists, this);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}
