
package com.reissgrvs.spotifyplaylisttool.SongList;

import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reissgrvs.spotifyplaylisttool.Helper.ItemTouchHelperAdapter;
import com.reissgrvs.spotifyplaylisttool.Helper.ItemTouchHelperViewHolder;
import com.reissgrvs.spotifyplaylisttool.Helper.OnStartDragListener;
import com.reissgrvs.spotifyplaylisttool.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;


public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<PlaylistTrack> mItems = new ArrayList<>();
    private final OnStartDragListener mDragStartListener;
    private final SongListFragment songListFragment;

    SongListAdapter(SongListFragment fragment, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
        songListFragment = fragment;
    }

    void addTracks(List<PlaylistTrack> newTracks) {
        mItems.addAll(newTracks);
        notifyDataSetChanged();
    }

    void addTrack(PlaylistTrack track, int position) {
        mItems.add(position,track);
        notifyDataSetChanged();
    }

    List<PlaylistTrack> getTracks(){
        return mItems;
    }



    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        Track track = mItems.get(position).track;

        holder.titleView.setText(track.name);
        holder.artistView.setText(track.artists.get(0).name);

        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        PlaylistTrack removedTrack = mItems.get(position);
        mItems.remove(position);
        notifyItemRemoved(position);
        songListFragment.onItemDismissed(removedTrack, position);

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder{

        final TextView titleView;
        final TextView artistView;
        final ImageView handleView;

        ItemViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.song_title);
            artistView = (TextView) itemView.findViewById(R.id.song_artist);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.DKGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }


    }
}
