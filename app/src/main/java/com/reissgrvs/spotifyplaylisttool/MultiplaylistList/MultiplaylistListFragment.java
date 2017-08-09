
package com.reissgrvs.spotifyplaylisttool.MultiplaylistList;


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

import com.reissgrvs.spotifyplaylisttool.Activities.MultiplaylistActivity;
import com.reissgrvs.spotifyplaylisttool.Helper.OnStartDragListener;
import com.reissgrvs.spotifyplaylisttool.Helper.SimpleItemTouchHelperCallback;
import com.reissgrvs.spotifyplaylisttool.PlaylistUpdateUtils.MultiplaylistUtils;
import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;
import com.reissgrvs.spotifyplaylisttool.Util.MultiplaylistStore;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Playlist;
import retrofit.client.Response;


public class MultiplaylistListFragment extends Fragment implements OnStartDragListener {

    private ItemTouchHelper mItemTouchHelper;
    private String mPlaylistID;
    private String mUserID;
    private ArrayList<String> mChildPlaylists;
    final MultiplaylistListAdapter adapter;
    private RecyclerView recyclerView;

    public MultiplaylistListFragment() {
        adapter = new MultiplaylistListAdapter(this, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPlaylistID = getArguments().getString("playlistID");
        mUserID = getArguments().getString("userID");
        mChildPlaylists = getArguments().getStringArrayList("childPlaylists");
        return new RecyclerView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Recyclerview setup
        recyclerView = (RecyclerView) view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setVisibility(View.GONE);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        if(!mChildPlaylists.isEmpty()) {
            String lastItem = mChildPlaylists.get(mChildPlaylists.size() - 1);
            for (String childId : mChildPlaylists) {
                addPlaylistFromId(childId, lastItem.equals(childId));
            }
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            ((MultiplaylistActivity)getActivity()).turnLoadingOff();
        }

    }

    private void addPlaylistFromId(String id, final Boolean last){
        String[] ids = id.split("-");
        //TODO: Maybe just get relevant info/ title user and possibly image if I want
        SpotifyAPIManager.getService().getPlaylist(ids[1], ids[0], new SpotifyCallback<Playlist>() {
            @Override
            public void failure(SpotifyError spotifyError) {
            }

            @Override
            public void success(Playlist playlist, Response response) {

                adapter.addPlaylist(playlist);
                if (last){
                    MultiplaylistUtils.executeSyncPlaylistTask(mUserID, mPlaylistID);
                    recyclerView.setVisibility(View.VISIBLE);
                    ((MultiplaylistActivity)getActivity()).turnLoadingOff();
                }
            }
        });
    }





    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


    public void onItemDismissed(Playlist removedPlaylist, int position){
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.content_multiplaylist), "Playlist removed" , Snackbar.LENGTH_SHORT );
        snackbar.setAction("UNDO", new UndoListener(removedPlaylist,position));
        snackbar.show();
        MultiplaylistStore.removeFromMulti(mPlaylistID, removedPlaylist.id + "-" + removedPlaylist.owner.id, getContext());
    }



    private class UndoListener implements View.OnClickListener{

        Playlist removedPlaylist;
        int position;

        UndoListener(Playlist removedPlaylist, int position) {
            this.position = position;
            this.removedPlaylist = removedPlaylist;
        }

        @Override
        public void onClick(View v) {
            adapter.addPlaylist(removedPlaylist,position);
            ArrayList<String> tracks = new ArrayList<>();
            tracks.add( removedPlaylist.id + "-" + removedPlaylist.owner.id);
            MultiplaylistStore.addToMulti(mPlaylistID, tracks , getContext());
        }
    }


    public ArrayList<Playlist> getPlaylists(){
        return new ArrayList<>(adapter.getPlaylists());
    }



}
