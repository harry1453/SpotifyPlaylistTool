package com.reissgrvs.spotifyplaylisttool.PlaylistList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.reissgrvs.spotifyplaylisttool.Activities.MultiplaylistActivity;
import com.reissgrvs.spotifyplaylisttool.Player.PlayerService;
import com.reissgrvs.spotifyplaylisttool.Activities.PlaylistActivity;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;
import com.reissgrvs.spotifyplaylisttool.Util.MultiplaylistStore;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class PlaylistPresenter implements UserPlaylist.ActionListener {

    private static final String TAG = PlaylistPresenter.class.getSimpleName();
    private static final int PAGE_SIZE = 50;

    private final Context mContext;
    private final UserPlaylist.View mView;

    private PlaylistPager mPlaylistPager;
    private PlaylistPager.CompleteListener mPlaylistListener;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ((PlayerService.PlayerBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public PlaylistPresenter(Context context, UserPlaylist.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void init(String accessToken) {
        SpotifyAPIManager.setToken(accessToken);
        mPlaylistPager = new PlaylistPager(mContext);
        mContext.bindService(PlayerService.getIntent(mContext), mServiceConnection, Activity.BIND_AUTO_CREATE);
    }


    public void refresh() {
            mView.reset();
            mPlaylistListener = new PlaylistPager.CompleteListener() {
                @Override
                public void onComplete(List<PlaylistSimple> items) {
                    mView.addData(items);
                }

                @Override
                public void onError(Throwable error) {
                    logError(error.getMessage());
                }
            };

            mPlaylistPager.getFirstPage( PAGE_SIZE, mPlaylistListener);
    }


    @Override
    public void destroy() {
        mContext.unbindService(mServiceConnection);
    }

    @Override
    public void resume() {
        mContext.stopService(PlayerService.getIntent(mContext));
    }

    @Override
    public void pause() {
        mContext.startService(PlayerService.getIntent(mContext));
    }

    public void loadMoreResults() {
        Log.d(TAG, "Load more...");
        mPlaylistPager.getNextPage(mPlaylistListener);
    }

    public void selectPlaylist(PlaylistSimple item) {

        if( MultiplaylistStore.isMulti(item.id))
        {
            Intent startMultiplaylistActivity = new Intent(mContext, MultiplaylistActivity.class);
            startMultiplaylistActivity.putExtra(Intent.EXTRA_SUBJECT, item.id);
            startMultiplaylistActivity.putExtra(Intent.EXTRA_TEXT, item.name);
            startMultiplaylistActivity.putExtra(Intent.EXTRA_USER, item.owner.id);
            startMultiplaylistActivity.putStringArrayListExtra("childPlaylists", MultiplaylistStore.getMulti(item.id) );
            mContext.startActivity(startMultiplaylistActivity);
        }
        else
        {
            Intent startPlaylistActivity = new Intent(mContext, PlaylistActivity.class);
            startPlaylistActivity.putExtra(Intent.EXTRA_SUBJECT, item.id);
            startPlaylistActivity.putExtra(Intent.EXTRA_TEXT, item.name);
            startPlaylistActivity.putExtra(Intent.EXTRA_USER, item.owner.id);
            mContext.startActivity(startPlaylistActivity);
        }

    }

    private void logError(String msg) {
        Toast.makeText(mContext, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

}
