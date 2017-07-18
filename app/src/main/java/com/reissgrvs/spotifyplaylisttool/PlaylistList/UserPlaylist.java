package com.reissgrvs.spotifyplaylisttool.PlaylistList;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class UserPlaylist {

    public interface View {
        void reset();

        void addData(List<PlaylistSimple> items);
    }

    public interface ActionListener {

        void init(String token);

        void refresh();

        void loadMoreResults();

        void selectPlaylist(PlaylistSimple item);

        void resume();

        void pause();

        void destroy();

    }
}
