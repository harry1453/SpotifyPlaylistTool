package com.reissgrvs.spotifyplaylisttool.SongSearch;

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

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class SongSearchResultsAdapter extends RecyclerView.Adapter<SongSearchResultsAdapter.ViewHolder> {

    private final List<Track> mItems = new ArrayList<>();
    private final Context mContext;
    private final ItemSelectedListener mListener;
    private final OnItemCheckListener mCheckListener;

    public interface OnItemCheckListener {
        void onItemCheck(Track item);
        void onItemUncheck(Track item);
        boolean isItemChecked(Track item);
        boolean isItemReserved(Track item);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView title;
        final TextView subtitle;
        final ImageView image;
        final CheckBox checkBox;

        ViewHolder(View itemView) {
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
        void onItemSelected(View itemView, Track item);
    }

    public SongSearchResultsAdapter(Context context, ItemSelectedListener listener, OnItemCheckListener checkListener) {
        mContext = context;
        mListener = listener;
        mCheckListener = checkListener;
    }

    public void clearData() {
        mItems.clear();
    }

    public void addData(List<Track> items) {
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
        final Track item = mItems.get(position);

        holder.checkBox.setChecked(false);
        holder.title.setText(item.name);
        holder.subtitle.setText(item.artists.get(0).name);

        Image image = item.album.images.get(0);
        if (image != null) {
            Picasso.with(mContext).load(image.url).into(holder.image);
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
