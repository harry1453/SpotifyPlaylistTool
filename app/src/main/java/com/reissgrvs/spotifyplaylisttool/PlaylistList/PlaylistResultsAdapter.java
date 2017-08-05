package com.reissgrvs.spotifyplaylisttool.PlaylistList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reissgrvs.spotifyplaylisttool.R;
import com.reissgrvs.spotifyplaylisttool.Util.MultiplaylistStore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPublic;

public class PlaylistResultsAdapter extends RecyclerView.Adapter<PlaylistResultsAdapter.ViewHolder> {

    private final List<PlaylistSimple> mItems = new ArrayList<>();
    private List<PlaylistSimple> mFilteredItems = new ArrayList<>();
    private final Context mContext;
    private final ItemSelectedListener mListener;
    private PlaylistFilter mFilter;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView title;
        final TextView subtitle;
        final TextView multi;
        final ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.entity_title);
            subtitle = (TextView) itemView.findViewById(R.id.entity_subtitle);
            image = (ImageView) itemView.findViewById(R.id.entity_image);
            multi = (TextView) itemView.findViewById(R.id.entity_multi);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(getLayoutPosition());
            mListener.onItemSelected(v, mFilteredItems.get(getAdapterPosition()));
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, PlaylistSimple item);
    }

    public PlaylistResultsAdapter(Context context, ItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
        mFilter = new PlaylistFilter(mItems,this);
    }

    public void filterData(String constraint) {
        mFilter.filter(constraint);
    }

    void setFilteredData(List<PlaylistSimple> filteredData) {
        mFilteredItems = filteredData;
        notifyDataSetChanged();
    }
    public void addData(List<PlaylistSimple> items) {
        mItems.addAll(items);
        mFilter = new PlaylistFilter(mItems,this);
        filterData("");
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaylistSimple item = mFilteredItems.get(position);

        holder.title.setText(item.name);
        UserPublic holdersub = item.owner;
        if(MultiplaylistStore.isMulti(item.id)){
            holder.multi.setText(R.string.multi_indicator);
            Log.d("PlaylistResultsAdapter", item.name + ": Multi " );
        }
        else
        {
            holder.multi.setText("");
            Log.d("PlaylistResultsAdapter", item.name + ": Not Multi " );
        }
        holder.subtitle.setText("username");//TODO:holdersub.id);
        Image image = null;
        try {
            image = item.images.get(0); //album.images.get(0);
        }
        catch (Exception e)
        {
            Log.e("FUCK", e.toString());
        }

        if (image != null) {
            Picasso.with(mContext).load(image.url).into(holder.image);
            Log.d("image url", image.url);
        }

    }

    @Override
    public int getItemCount() {
        return mFilteredItems.size();
    }
}
