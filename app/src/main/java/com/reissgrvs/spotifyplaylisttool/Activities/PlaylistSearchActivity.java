package com.reissgrvs.spotifyplaylisttool.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.reissgrvs.spotifyplaylisttool.MultiplaylistSearch.PlaylistSearch;
import com.reissgrvs.spotifyplaylisttool.MultiplaylistSearch.PlaylistSearchPresenter;
import com.reissgrvs.spotifyplaylisttool.MultiplaylistSearch.PlaylistSearchResultsAdapter;
import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import com.reissgrvs.spotifyplaylisttool.Util.ResultListScrollListener;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class PlaylistSearchActivity extends AppCompatActivity implements PlaylistSearch.View {


    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";

    private PlaylistSearch.ActionListener mActionListener;
    private List<String> mReservedPlaylists = new ArrayList<>();
    private List<PlaylistSimple> mAddedPlaylists = new ArrayList<>();

    private LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
    private ScrollListener mScrollListener = new ScrollListener(mLayoutManager);
    private PlaylistSearchResultsAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_search);
        Intent intent = getIntent();
        intent.toString();
        if(getIntent().hasExtra("playlists")) {
            Log.d("PlaylistSearchActivity", "Has extra");
            mReservedPlaylists = getIntent().getStringArrayListExtra("playlists");
        }
        else{
            Log.d("PlaylistSearchActivity", "Doesnt have extra");
        }
        String token = TokenStore.getAuthToken(getBaseContext());
        Log.d("token", token);

        mActionListener = new PlaylistSearchPresenter(this, this);
        mActionListener.init(token);

        // Setup search field
        final SearchView searchView = (SearchView) findViewById(R.id.playlist_search_view);
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
        mAdapter = new PlaylistSearchResultsAdapter(this,
                new PlaylistSearchResultsAdapter.ItemSelectedListener() {
                    @Override
                    public void onItemSelected(View itemView, PlaylistSimple item) {
                        mActionListener.selectPlaylist(item);
                    }
                },
                new PlaylistSearchResultsAdapter.OnItemCheckListener() {
                    @Override
                    public void onItemCheck(PlaylistSimple item) {
                        mAddedPlaylists.add(item);
                        Log.d("TrackCheckedAdd", item.id);
                    }

                    @Override
                    public void onItemUncheck(PlaylistSimple item) {
                        mAddedPlaylists.remove(item);
                        Log.d("TrackCheckedRemove", item.id);
                    }

                    @Override
                    public boolean isItemReserved(PlaylistSimple item){

                        boolean value = false;
                        if (item != null && !mReservedPlaylists.isEmpty()) {
                            for (String cur : mReservedPlaylists) {
                                if (cur.equals(item.id)) {
                                    value = true;
                                }
                            }
                        }
                        return value;
                    }
                    @Override
                    public boolean isItemChecked(PlaylistSimple item){
                        boolean value = false;
                        for (PlaylistSimple cur : mAddedPlaylists){
                            if (cur.id.equals(item.id)){
                                value = true;
                            }
                        }
                        return value;
                    }
                }
        );

        RecyclerView resultsList = (RecyclerView) findViewById(R.id.playlist_search_results);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(mLayoutManager);
        resultsList.setAdapter(mAdapter);
        resultsList.addOnScrollListener(mScrollListener);

        // If Activity was recreated with active search restore it
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
                returnIntent.putParcelableArrayListExtra("result",(ArrayList<PlaylistSimple>) mAddedPlaylists);
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
    public void addData(List<PlaylistSimple> items) {
        mAdapter.addData(items);
    }


    private class ScrollListener extends ResultListScrollListener {

        public ScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        public void onLoadMore() {
            mActionListener.loadMoreResults();
        }
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, PlaylistSearchActivity.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActionListener.getCurrentQuery() != null) {
            outState.putString(KEY_CURRENT_QUERY, mActionListener.getCurrentQuery());
        }
    }


}
