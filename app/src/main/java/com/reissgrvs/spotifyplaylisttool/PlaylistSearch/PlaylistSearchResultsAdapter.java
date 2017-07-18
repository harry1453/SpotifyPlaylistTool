package com.reissgrvs.spotifyplaylisttool.PlaylistSearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.reissgrvs.spotifyplaylisttool.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;

public class PlaylistSearchResultsAdapter extends RecyclerView.Adapter<PlaylistSearchResultsAdapter.ViewHolder> {

    private final List<PlaylistSimple> mItems = new ArrayList<>();
    private final Context mContext;
    private final ItemSelectedListener mListener;
    private final OnItemCheckListener mCheckListener;

    public interface OnItemCheckListener {
        void onItemCheck(PlaylistSimple item);
        void onItemUncheck(PlaylistSimple item);
        boolean isItemChecked(PlaylistSimple item);
        boolean isItemReserved(PlaylistSimple item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView title;
        public final TextView subtitle;
        public final ImageView image;
        public final CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.song_search_entity_title);
            subtitle = (TextView) itemView.findViewById(R.id.song_search_entity_subtitle);
            image = (ImageView) itemView.findViewById(R.id.song_search_entity_image);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box_selected);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(getLayoutPosition());
            mListener.onItemSelected(v, mItems.get(getAdapterPosition()));
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, PlaylistSimple item);
    }

    public PlaylistSearchResultsAdapter(Context context, ItemSelectedListener listener, OnItemCheckListener checkListener) {
        mContext = context;
        mListener = listener;
        mCheckListener = checkListener;
    }

    public void clearData() {
        mItems.clear();
    }

    public void addData(List<PlaylistSimple> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_search_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PlaylistSimple item = mItems.get(position);

        holder.checkBox.setChecked(false);
        holder.title.setText(item.name);
        holder.subtitle.setText(item.owner.id);
        try {
            Image image = item.images.get(0);
            if (image != null) {
                Picasso.with(mContext).load(image.url).into(holder.image);
            }
        }
        catch (IndexOutOfBoundsException e){

        }


        holder.checkBox.setChecked(mCheckListener.isItemChecked(item) || mCheckListener.isItemReserved(item));
        holder.checkBox.setEnabled(!mCheckListener.isItemReserved(item));
        holder.checkBox.setClickable(!mCheckListener.isItemReserved(item));
        if(!mCheckListener.isItemReserved(item)) {
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //holder.checkBox.setChecked(!holder.checkBox.isChecked());

                    if (holder.checkBox.isChecked()) {
                        mCheckListener.onItemCheck(item);
                    } else {
                        mCheckListener.onItemUncheck(item);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
