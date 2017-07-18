package com.reissgrvs.spotifyplaylisttool.SongSearch;

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

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

public class SongSearchPresenter implements SongSearch.ActionListener {

    private static final String TAG = SongSearchPresenter.class.getSimpleName();
    public static final int PAGE_SIZE = 20;

    private final Context mContext;
    private final SongSearch.View mView;
    private String mCurrentQuery;

    private SongSearchPager mSongSearchPager;
    private SongSearchPager.CompleteListener mSearchListener;

    private Player mPlayer;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayer = ((PlayerService.PlayerBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer = null;
        }
    };

    public SongSearchPresenter(Context context, SongSearch.View view) {
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

        mSongSearchPager = new SongSearchPager(spotifyApi.getService());

        mContext.bindService(PlayerService.getIntent(mContext), mServiceConnection, Activity.BIND_AUTO_CREATE);
    }


    @Override
    public void search(@Nullable String searchQuery) {
        if (searchQuery != null && !searchQuery.isEmpty() && !searchQuery.equals(mCurrentQuery)) {
            logMessage("query text submit " + searchQuery);
            mCurrentQuery = searchQuery;
            mView.reset();
            mSearchListener = new SongSearchPager.CompleteListener() {
                @Override
                public void onComplete(List<Track> items) {
                    mView.addData(items);
                }

                @Override
                public void onError(Throwable error) {
                    logError(error.getMessage());
                }
            };
            mSongSearchPager.getFirstPage(searchQuery, PAGE_SIZE, mSearchListener);
        }
    }


    @Override
    public void destroy() {
        mContext.unbindService(mServiceConnection);
    }

    @Override
    @Nullable
    public String getCurrentQuery() {
        return mCurrentQuery;
    }

    @Override
    public void resume() {
        mContext.stopService(PlayerService.getIntent(mContext));
    }

    @Override
    public void pause() {
        mContext.startService(PlayerService.getIntent(mContext));
    }

    @Override
    public void loadMoreResults() {
        Log.d(TAG, "Load more...");
        mSongSearchPager.getNextPage(mSearchListener);
    }

    @Override
    public void selectTrack(Track item) {
        String previewUrl = item.preview_url;

        if (previewUrl == null) {
            logMessage("Track doesn't have a preview");
            return;
        }

        if (mPlayer == null) return;

        String currentTrackUrl = mPlayer.getCurrentTrack();

        if (currentTrackUrl == null || !currentTrackUrl.equals(previewUrl)) {
            mPlayer.play(previewUrl);
        } else if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.resume();
        }
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
