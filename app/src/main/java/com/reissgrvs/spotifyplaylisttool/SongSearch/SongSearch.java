package com.reissgrvs.spotifyplaylisttool.SongSearch;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class SongSearch {

    public interface View {
        void reset();

        void addData(List<Track> items);
    }

    public interface ActionListener {

        void init(String token);

        String getCurrentQuery();

        void search(String searchQuery);

        void loadMoreResults();

        void selectTrack(Track item);

        void resume();

        void pause();

        void destroy();

    }
}
