package com.zenbarrier.betonblack;

import android.content.Context;
import android.content.res.Resources;
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

class MyMainAdapter extends RecyclerView.Adapter<MyMainAdapter.ViewHolder> {
    private List<Strategy> mStrategyList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    MyMainAdapter(Context context, List<Strategy> strategyList){
        mStrategyList = strategyList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_mainrecyclerlist_strategy, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Strategy strategy = mStrategyList.get(position);
        ((TextView)holder.view.findViewById(R.id.textView_mainRecyclerlist_name)).setText(String.valueOf(strategy.name));
        ((TextView)holder.view.findViewById(R.id.textView_mainRecyclerlist_minBet)).setText(String.valueOf(strategy.minBet));
        ((TextView)holder.view.findViewById(R.id.textView_mainRecyclerlist_maxBet)).setText(String.valueOf(strategy.maxBet));
        Resources res = mContext.getResources();
        String[] strategies = res.getStringArray(R.array.strategy_array);
        ((TextView)holder.view.findViewById(R.id.textView_mainRecyclerlist_strategyChoice)).setText(strategies[strategy.strategyChoice]);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().rotationBy(360f).setDuration(200).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStrategyList.size();
    }

    void removeItem(int position){
        mStrategyList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }
}
