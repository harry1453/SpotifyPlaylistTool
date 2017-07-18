package com.reissgrvs.spotifyplaylisttool.PlaylistSearch;

import java.util.List;

import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;

public class PlaylistSearch {

    public interface View {
        void reset();

        void addData(List<PlaylistSimple> items);
    }

    public interface ActionListener {

        void init(String token);

        String getCurrentQuery();

        void search(String searchQuery);

        void loadMoreResults();

        void selectPlaylist(PlaylistSimple item);


    }
}
