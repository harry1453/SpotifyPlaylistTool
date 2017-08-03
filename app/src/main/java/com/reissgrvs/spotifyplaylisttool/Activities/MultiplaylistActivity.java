package com.reissgrvs.spotifyplaylisttool.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.reissgrvs.spotifyplaylisttool.MultiplaylistList.MultiplaylistListFragment;
import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.Util.MultiPlaylistStore;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;


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


        Intent ownerIntent = getIntent();

        if (ownerIntent.hasExtra(Intent.EXTRA_TEXT)) {

            String playlistTitle = ownerIntent.getStringExtra(Intent.EXTRA_TEXT);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(playlistTitle);
        }

        if (ownerIntent.hasExtra(Intent.EXTRA_SUBJECT) && ownerIntent.hasExtra(Intent.EXTRA_USER))
        {
            playlistID = ownerIntent.getStringExtra(Intent.EXTRA_SUBJECT);
            userID = ownerIntent.getStringExtra(Intent.EXTRA_USER);
        }
        if (ownerIntent.hasExtra("childPlaylists")) {
            childPlaylists = ownerIntent.getStringArrayListExtra("childPlaylists");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = PlaylistSearchActivity.createIntent(getBaseContext());
                ArrayList<String> playlistIDs = new ArrayList<>();
                for (Playlist playlist : fragment.getPlaylists())
                {
                    playlistIDs.add(playlist.id);
                }
                intent.putStringArrayListExtra("playlists", playlistIDs);
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
                recreate();
            }

        }
    }

    public void turnLoadingOff(){
        ProgressBar loading = (ProgressBar)findViewById(R.id.progress_bar_multiplaylist);
        loading.setVisibility(View.GONE);
        fab.show();
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.lin_layout_multiplaylist);
        linearLayout.setVisibility(View.VISIBLE);
    }

}
