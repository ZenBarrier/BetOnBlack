package com.zenbarrier.betonblack;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Anthony on 4/14/2017.
 * This file is the fragment that holds all the preferences
 */

class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private List<History> mHistoryList;
    private Context mContext;
    private Fragment mFragment;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    HistoryAdapter(Context context, List<History> historyList, Fragment fragment){
        mHistoryList = historyList;
        mContext = context;
        mFragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_mainrecyclerlist_history, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final History history = mHistoryList.get(position);
        int profit = history.endCash - history.startCash;
        ((TextView)holder.view.findViewById(R.id.textView_history_name)).setText(history.name);
        ((TextView)holder.view.findViewById(R.id.textView_history_startCash)).setText(String.valueOf(history.startCash));
        ((TextView)holder.view.findViewById(R.id.textView_history_endCash)).setText(String.valueOf(history.endCash));

        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        dt.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = dt.parse(history.date);
            SimpleDateFormat dt1 = new SimpleDateFormat("MM/dd/yy", Locale.US);
            dt1.setTimeZone(TimeZone.getDefault());
            ((TextView)holder.view.findViewById(R.id.textView_history_date)).setText(dt1.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete);
                builder.setMessage(String.format(Locale.getDefault(), mContext.getString(R.string.history_adapter_delete), history.name));
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeItem(holder.getAdapterPosition());
                    }
                });
                builder.setNegativeButton(R.string.newstrategy_cancel, null);
                builder.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    private void removeItem(int position){
        long id = mHistoryList.remove(position)._id;
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

        StrategyDbHelper dbHelper = new StrategyDbHelper(mContext);
        dbHelper.deleteHistory(id);
        ((HistoryFragment)mFragment).setProfitDisplay();
    }

    /*void addItem(int position, History history) {
        mHistoryList.add(position, history);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());

        StrategyDbHelper dbHelper = new StrategyDbHelper(mContext);
        dbHelper.add(history);
    }*/
}
