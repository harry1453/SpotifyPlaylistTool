package com.reissgrvs.spotifyplaylisttool.PlaylistList;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

/**
 * Created by Reiss on 06/06/2017.
 */

public class PlaylistFilter extends Filter {
    private List<PlaylistSimple> playlistList;
    private List<PlaylistSimple> filteredPlaylistList;
    private PlaylistResultsAdapter adapter;

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredPlaylistList.clear();
        final FilterResults results = new FilterResults();
        String lowerConstraint = ((String) constraint).toLowerCase();
        for (final PlaylistSimple item : playlistList) {
            if (item.name.toLowerCase().trim().contains(lowerConstraint)) {
                filteredPlaylistList.add(item);
            }
        }

        results.values = filteredPlaylistList;
        results.count = filteredPlaylistList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setFilteredData(filteredPlaylistList);
    }

    public PlaylistFilter(List<PlaylistSimple> playlistList, PlaylistResultsAdapter adapter) {
        super();
        this.playlistList = playlistList;
        this.adapter = adapter;
        this.filteredPlaylistList = new ArrayList<>();
    }
}
