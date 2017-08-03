package com.reissgrvs.spotifyplaylisttool.MultiplaylistSearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistsPager;
import retrofit.client.Response;

class PlaylistSearchPager {

    private final SpotifyService mSpotifyApi;
    private int mCurrentOffset;
    private int mPageSize;
    private String mCurrentQuery;

    interface CompleteListener {
        void onComplete(List<PlaylistSimple> items);
        void onError(Throwable error);
    }

    PlaylistSearchPager(SpotifyService spotifyApi) {
        mSpotifyApi = spotifyApi;
    }

    void getFirstPage(String query, int pageSize, CompleteListener listener) {
        mCurrentOffset = 0;
        mPageSize = pageSize;
        mCurrentQuery = query;
        getData(query, 0, pageSize, listener);
    }

    void getNextPage(CompleteListener listener) {
        mCurrentOffset += mPageSize;
        getData(mCurrentQuery, mCurrentOffset, mPageSize, listener);
    }

    private void getData(String query, int offset, final int limit, final CompleteListener listener) {

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);

        mSpotifyApi.searchPlaylists(query, options, new SpotifyCallback<PlaylistsPager>() {
            @Override
            public void success(PlaylistsPager playlistsPager, Response response) {
                listener.onComplete(playlistsPager.playlists.items);
            }

            @Override
            public void failure(SpotifyError error) {
                listener.onError(error);
            }
        });
    }
}
