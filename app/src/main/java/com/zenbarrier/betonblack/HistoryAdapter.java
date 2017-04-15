package com.zenbarrier.betonblack;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Anthony on 4/14/2017.
 * This file is the fragment that holds all the preferences
 */

class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private List<History> mHistoryList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    HistoryAdapter(Context context, List<History> historyList){
        mHistoryList = historyList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_mainrecyclerlist_history, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
