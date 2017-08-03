package com.reissgrvs.spotifyplaylisttool.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.reissgrvs.spotifyplaylisttool.Dialogs.PlaylistAddDialog;
import com.reissgrvs.spotifyplaylisttool.PlaylistUpdateUtils.MultiplaylistUtils;
import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import com.reissgrvs.spotifyplaylisttool.PlaylistList.PlaylistPresenter;
import com.reissgrvs.spotifyplaylisttool.PlaylistList.PlaylistResultsAdapter;
import com.reissgrvs.spotifyplaylisttool.UpdateService.UpdateScheduler;
import com.reissgrvs.spotifyplaylisttool.Util.MultiPlaylistStore;
import com.reissgrvs.spotifyplaylisttool.Util.ResultListScrollListener;
import com.reissgrvs.spotifyplaylisttool.PlaylistList.UserPlaylist;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class MainActivity extends AppCompatActivity implements UserPlaylist.View, android.support.v7.widget.SearchView.OnQueryTextListener {

    private UserPlaylist.ActionListener mActionListener;

    private LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
    private ScrollListener mScrollListener = new ScrollListener(mLayoutManager);
    private PlaylistResultsAdapter mAdapter;



    private class ScrollListener extends ResultListScrollListener {

        private ScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        public void onLoadMore() {
            mActionListener.loadMoreResults();
        }
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(24);

        String token = TokenStore.getAuthToken(this);


        mActionListener = new PlaylistPresenter(this, this);
        mActionListener.init(token);

        Log.d("UserID", "UserID:" +TokenStore.getUserId(this));
        //TODO: Should make these files userID specific
        MultiPlaylistStore.loadMultiPlaylistFile(this);

        MultiplaylistUtils.updateAllMultiplaylists(this, TokenStore.getUserId(this));

        UpdateScheduler.scheduleMultiplaylistUpdate(this);

        // Setup search results list
        mAdapter = new PlaylistResultsAdapter(this, new PlaylistResultsAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, PlaylistSimple item) {
                mActionListener.selectPlaylist(item);
            }
        });

        //View setup
        RecyclerView resultsList = (RecyclerView) findViewById(R.id.search_results);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(mLayoutManager);
        resultsList.setAdapter(mAdapter);
        resultsList.addOnScrollListener(mScrollListener);
        mActionListener.refresh();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_playlist);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaylistAddDialog addDialog = new PlaylistAddDialog(MainActivity.this);
                addDialog.show();
            }
        });


    }

    @Override
    public void reset() {
        mScrollListener.reset();
    }

    @Override
    public void addData(List<PlaylistSimple> items) {
        mAdapter.addData(items);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActionListener.pause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {

            case R.id.action_log_out:
                TokenStore.clearToken(this);
                Intent returnToLogIn = new Intent(this, LoginActivity.class);
                startActivity(returnToLogIn);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActionListener.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist_list_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {
        mActionListener.destroy();
        MultiPlaylistStore.saveMultiPlaylistFile(this);
        super.onDestroy();
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.filterData(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.filterData(newText);
        return false;
    }
}
