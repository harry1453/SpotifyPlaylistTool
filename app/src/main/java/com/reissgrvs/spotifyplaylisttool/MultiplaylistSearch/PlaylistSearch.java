package com.reissgrvs.spotifyplaylisttool.MultiplaylistSearch;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

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
