package com.reissgrvs.spotifyplaylisttool.Util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class ResultListScrollListener extends RecyclerView.OnScrollListener {

    private final LinearLayoutManager mLayoutManager;

    private static final int SCROLL_BUFFER = 3;
    private int mCurrentItemCount = 0;

    private boolean mAwaitingItems = true;

    public ResultListScrollListener(LinearLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    public void reset() {
        mCurrentItemCount = 0;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int itemCount = mLayoutManager.getItemCount();
        int itemPosition = mLayoutManager.findLastVisibleItemPosition();
        if (mAwaitingItems && itemCount > mCurrentItemCount) {
            mCurrentItemCount = itemCount;
            mAwaitingItems = false;
        }

        if (!mAwaitingItems && itemPosition + 1 >= itemCount - SCROLL_BUFFER) {
            mAwaitingItems = true;
            onLoadMore();
        }
    }

    public abstract void onLoadMore();
}
