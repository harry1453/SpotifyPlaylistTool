package com.reissgrvs.spotifyplaylisttool.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.SongSearch.SongSearch;
import com.reissgrvs.spotifyplaylisttool.SongSearch.SongSearchPresenter;
import com.reissgrvs.spotifyplaylisttool.SongSearch.SongSearchResultsAdapter;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import com.reissgrvs.spotifyplaylisttool.Util.ResultListScrollListener;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class SongSearchActivity extends AppCompatActivity implements SongSearch.View {


    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";

    private SongSearch.ActionListener mActionListener;
    private List<Track> mReservedTracks;
    private List<Track> mAddedTracks = new ArrayList<>();
    private LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
    private ScrollListener mScrollListener = new ScrollListener(mLayoutManager);
    private SongSearchResultsAdapter mAdapter;


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
        return new Intent(context, SongSearchActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.song_search_toolbar);
        setSupportActionBar(toolbar);
        mReservedTracks = getIntent().getParcelableArrayListExtra("playlistTracks");

        String token = TokenStore.getAuthToken(getBaseContext());
        Log.d("token", token);

        mActionListener = new SongSearchPresenter(this, this);
        mActionListener.init(token);

        // Setup search field
        final SearchView searchView = (SearchView) findViewById(R.id.song_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mActionListener.search(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        // Setup search results list
        mAdapter = new SongSearchResultsAdapter(this,
                new SongSearchResultsAdapter.ItemSelectedListener() {
                    @Override
                    public void onItemSelected(View itemView, Track item) {
                        mActionListener.selectTrack(item);
                    }
                },
                new SongSearchResultsAdapter.OnItemCheckListener() {
                    @Override
                    public void onItemCheck(Track item) {
                        mAddedTracks.add(item);
                        Log.d("TrackCheckedAdd", item.id);
                    }

                    @Override
                    public void onItemUncheck(Track item) {
                        mAddedTracks.remove(item);
                        Log.d("TrackCheckedRemove", item.id);
                    }

                    @Override
                    public boolean isItemReserved(Track item){

                        boolean value = false;

                        for (Track cur : mReservedTracks){
                            if (cur.external_ids.equals(item.external_ids)){
                                value = true;
                                Log.d("TrackChecked", item.name + " is in list of tracks. ID: " + item.id + "  Matched with: " + cur.id);
                            }
                        }

                        return value;
                    }
                    @Override
                    public boolean isItemChecked(Track item){
                        boolean value = false;
                        for (Track cur : mAddedTracks){
                            if (cur.external_ids.equals(item.external_ids)){
                                value = true;
                            }
                        }
                        return value;
                    }
                }
        );

        RecyclerView resultsList = (RecyclerView) findViewById(R.id.song_search_results);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(mLayoutManager);
        resultsList.setAdapter(mAdapter);
        resultsList.addOnScrollListener(mScrollListener);

        // If Activity was recreated wit active search restore it
        if (savedInstanceState != null) {
            String currentQuery = savedInstanceState.getString(KEY_CURRENT_QUERY);
            mActionListener.search(currentQuery);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {

            case R.id.action_apply:

                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra("result",(ArrayList<Track>) mAddedTracks);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_search_menu, menu);
        return true;
    }

    @Override
    public void reset() {
        mScrollListener.reset();
        mAdapter.clearData();
    }

    @Override
    public void addData(List<Track> items) {
        mAdapter.addData(items);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActionListener.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActionListener.resume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActionListener.getCurrentQuery() != null) {
            outState.putString(KEY_CURRENT_QUERY, mActionListener.getCurrentQuery());
        }
    }

    @Override
    protected void onDestroy() {
        mActionListener.destroy();
        super.onDestroy();
    }

}
