

package com.reissgrvs.spotifyplaylisttool.SongList;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reissgrvs.spotifyplaylisttool.Helper.OnStartDragListener;
import com.reissgrvs.spotifyplaylisttool.Helper.SimpleItemTouchHelperCallback;
import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;


public class SongListFragment extends Fragment implements OnStartDragListener {

    private ItemTouchHelper mItemTouchHelper;
    private String mPlaylistID;
    private String mUserID;
    final SongListAdapter adapter;

    private final String PLAYLIST_ID =  "playlistID";
    private final String USER_ID =  "userID";

    public SongListFragment() {
        adapter = new SongListAdapter(this, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPlaylistID = getArguments().getString(PLAYLIST_ID);
        mUserID = getArguments().getString(USER_ID);
        RecyclerView recyclerView = new RecyclerView(container.getContext());
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        SpotifyAPIManager.getService().getPlaylistTracks(mUserID, mPlaylistID, new SpotifyCallback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                adapter.addTracks(playlistTrackPager.items);
            }
            @Override
            public void failure(SpotifyError spotifyError) {}
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public void onItemDismissed(PlaylistTrack removedTrack, int position){
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.content_playlist), "Track removed" , Snackbar.LENGTH_SHORT );
        snackbar.setAction("UNDO", new UndoListener(removedTrack,position));
        snackbar.show();
    }

    private class UndoListener implements View.OnClickListener{

        PlaylistTrack removedTrack;
        int position;

        UndoListener(PlaylistTrack removedTrack, int position) {
            this.position = position;
            this.removedTrack = removedTrack;
        }

        @Override
        public void onClick(View v) {
            adapter.addTrack(removedTrack,position);
        }
    }

    public ArrayList<Track> getTracks(){
        ArrayList<Track> tracksList = new ArrayList<>();
        List<PlaylistTrack> playlistTrackList = adapter.getTracks();
        for (PlaylistTrack cur : playlistTrackList){
            tracksList.add(cur.track);
        }
        return tracksList;
    }


}
