package com.zenbarrier.betonblack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anthony on 3/6/2017.
 * This file is the fragment that holds all the preferences
 */

public class MyMainAdapter extends RecyclerView.Adapter<MyMainAdapter.ViewHolder> {
    List<Strategy> mStrategyList;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    public MyMainAdapter(List<Strategy> strategyList){
        mStrategyList = strategyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_main_strategy, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Strategy strategy = mStrategyList.get(position);
        ((TextView)holder.view.findViewById(R.id.textView)).setText(String.valueOf(strategy.maxBet));
        ((TextView)holder.view.findViewById(R.id.textView2)).setText(String.valueOf(strategy.minBet));
        ((TextView)holder.view.findViewById(R.id.textView3)).setText(String.valueOf(strategy.strategyChoice));
    }

    @Override
    public int getItemCount() {
        return mStrategyList.size();
    }
}
