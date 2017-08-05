package com.reissgrvs.spotifyplaylisttool.MultiplaylistSearch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.reissgrvs.spotifyplaylisttool.Util.MyPlaylistsStore;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class PlaylistSearchPresenter implements PlaylistSearch.ActionListener {

    private static final String TAG = PlaylistSearchPresenter.class.getSimpleName();
    private static final int PAGE_SIZE = 20;

    private final Context mContext;
    private final PlaylistSearch.View mView;
    private String mCurrentQuery;
    private List<PlaylistSimple> mUserPlaylists = new ArrayList<>();

    private PlaylistSearchPager mPlaylistSearchPager;
    private PlaylistSearchPager.CompleteListener mSearchListener;

    public PlaylistSearchPresenter(Context context, PlaylistSearch.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void init(String accessToken) {
        SpotifyApi spotifyApi = new SpotifyApi();

        if (accessToken != null) {
            spotifyApi.setAccessToken(accessToken);
        } else {
            logError("No valid access token");
        }

        mPlaylistSearchPager = new PlaylistSearchPager(spotifyApi.getService());

        mUserPlaylists = MyPlaylistsStore.getMyPlaylists();
        mView.addData(mUserPlaylists);

    }


    @Override
    public void search(@Nullable String searchQuery) {
        if (searchQuery != null && !searchQuery.isEmpty() && !searchQuery.equals(mCurrentQuery)) {
            mCurrentQuery = searchQuery;
            mView.reset();
            List<PlaylistSimple> queriedUserPlaylists = new ArrayList<>();

            for (PlaylistSimple playlist : mUserPlaylists)
            {
                if (playlist.name.toLowerCase().contains(mCurrentQuery.toLowerCase())){
                    queriedUserPlaylists.add(playlist);
                }
            }

            mView.addData(queriedUserPlaylists);

            mSearchListener = new PlaylistSearchPager.CompleteListener() {
                @Override
                public void onComplete(List<PlaylistSimple> items) {
                    mView.addData(items);
                }

                @Override
                public void onError(Throwable error) {
                    logError(error.getMessage());
                }
            };
            mPlaylistSearchPager.getFirstPage(searchQuery, PAGE_SIZE, mSearchListener);
        }
        if (searchQuery.isEmpty()){
            mView.reset();
            mView.addData(mUserPlaylists);
        }
    }



    @Override
    @Nullable
    public String getCurrentQuery() {
        return mCurrentQuery;
    }


    @Override
    public void loadMoreResults() {
        Log.d(TAG, "Load more...");
        mPlaylistSearchPager.getNextPage(mSearchListener);
    }

    @Override
    public void selectPlaylist(PlaylistSimple item) {

    }

    private void logError(String msg) {
        Toast.makeText(mContext, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }
}
