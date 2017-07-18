package com.reissgrvs.spotifyplaylisttool.PlaylistList;

import android.content.Context;
import android.media.session.MediaSession;
import android.util.Log;

import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import com.reissgrvs.spotifyplaylisttool.Util.MyPlaylistsStore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import retrofit.client.Response;

public class PlaylistPager {

    private final SpotifyService mSpotifyService;
    private int mCurrentOffset;
    private int mPageSize;
    private Context mContext;

    public interface CompleteListener {
        void onComplete(List<PlaylistSimple> items);
        void onError(Throwable error);
    }

    public PlaylistPager(Context context) {
        mContext = context;
        mSpotifyService = SpotifyAPIManager.getService();
    }

    public void getFirstPage( int pageSize, CompleteListener listener) {
        mCurrentOffset = 0;
        mPageSize = pageSize;
        getData( 0, pageSize, listener);
    }

    public void getNextPage(CompleteListener listener) {
        mCurrentOffset += mPageSize;
        getData( mCurrentOffset, mPageSize, listener);
    }

    private void getData(int offset, final int limit, final CompleteListener listener) {

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);

        mSpotifyService.getMyPlaylists( options, new SpotifyCallback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistPager, Response response) {
                List<PlaylistSimple> items = playlistPager.items;
                List<PlaylistSimple> userItems = new ArrayList<>();
                //TODO: Maybe save these to file or something so that can be accessed in playlist search

                MyPlaylistsStore.myPlaylists = items;

                String userID = TokenStore.getUserId(mContext);
                for (PlaylistSimple item : items){
                    if (item.owner.id.equals(userID) || item.collaborative){
                        userItems.add(item);
                    }
                }
                listener.onComplete(userItems);
            }

            @Override
            public void failure(SpotifyError error) {
                listener.onError(error);
            }
        });
    }
}
