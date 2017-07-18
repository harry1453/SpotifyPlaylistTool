package com.reissgrvs.spotifyplaylisttool.PlaylistSearch;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.reissgrvs.spotifyplaylisttool.Player.Player;
import com.reissgrvs.spotifyplaylisttool.Player.PlayerService;
import com.reissgrvs.spotifyplaylisttool.Util.MyPlaylistsStore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;

public class PlaylistSearchPresenter implements PlaylistSearch.ActionListener {

    private static final String TAG = PlaylistSearchPresenter.class.getSimpleName();
    public static final int PAGE_SIZE = 20;

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

        mUserPlaylists = MyPlaylistsStore.myPlaylists;

    }


    @Override
    public void search(@Nullable String searchQuery) {
        if (searchQuery != null && !searchQuery.isEmpty() && !searchQuery.equals(mCurrentQuery)) {
            logMessage("query text submit " + searchQuery);
            mCurrentQuery = searchQuery;
            mView.reset();
            List<PlaylistSimple> queriedUserPlaylists = new ArrayList<>();
            //TODO: Add a search for current users playlists and add to View
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
        //TODO: For if someone clicks on playlists
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
