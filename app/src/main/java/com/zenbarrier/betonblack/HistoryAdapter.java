package com.zenbarrier.betonblack;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        final History history = mHistoryList.get(position);
        int profit = history.endCash - history.startCash;
        ((TextView)holder.view.findViewById(R.id.textView_history_name)).setText(history.name);
        ((TextView)holder.view.findViewById(R.id.textView_history_startCash)).setText(String.valueOf(history.startCash));
        ((TextView)holder.view.findViewById(R.id.textView_history_endCash)).setText(String.valueOf(history.endCash));
        ((TextView)holder.view.findViewById(R.id.textView_history_date)).setText(String.valueOf(history.date));

        TextView textViewProfit = ((TextView)holder.view.findViewById(R.id.textView_history_profit));
        if(profit >= 0){
            textViewProfit.setText(String.valueOf(profit));
            textViewProfit.setTextColor(ContextCompat.getColor(mContext, R.color.colorProfit));
        }else{
            textViewProfit.setText(String.valueOf(Math.abs(profit)));
            textViewProfit.setTextColor(ContextCompat.getColor(mContext, R.color.colorLoss));
            ((TextView)holder.view.findViewById(R.id.textView_history_profitLabel)).setText(R.string.loss_label);
        }
        ImageView imageViewManual = ((ImageView) holder.view.findViewById(R.id.imageView_history_manual));
        if(history.isManual){
            imageViewManual.setImageResource(R.drawable.ic_casino);
        }
        else{
            imageViewManual.setImageResource(R.drawable.ic_roulette);
        }
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    void removeItem(int position){
        long id = mHistoryList.remove(position)._id;
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

        StrategyDbHelper dbHelper = new StrategyDbHelper(mContext);
        dbHelper.deleteHistory(id);
    }

    void addItem(int position, History history) {
        mHistoryList.add(position, history);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());

        StrategyDbHelper dbHelper = new StrategyDbHelper(mContext);
        dbHelper.add(history);
    }
}
