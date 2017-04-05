package com.zenbarrier.betonblack;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
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

class StrategyAdapter extends RecyclerView.Adapter<StrategyAdapter.ViewHolder> {
    private List<Strategy> mStrategyList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    StrategyAdapter(Context context, List<Strategy> strategyList){
        mStrategyList = strategyList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_mainrecyclerlist_strategy, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Strategy strategy = mStrategyList.get(position);
        ((TextView)holder.view.findViewById(R.id.textView_mainRecyclerlist_name)).setText(String.valueOf(strategy.name));
        ((TextView)holder.view.findViewById(R.id.textView_mainRecyclerlist_minBet)).setText(String.valueOf(strategy.minBet));
        ((TextView)holder.view.findViewById(R.id.textView_mainRecyclerlist_maxBet)).setText(String.valueOf(strategy.maxBet));
        Resources res = mContext.getResources();
        String[] strategies = res.getStringArray(R.array.strategy_array);
        ((TextView)holder.view.findViewById(R.id.textView_mainRecyclerlist_strategyChoice)).setText(strategies[strategy.strategyChoice]);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GameActivity.class);
                intent.putExtra(StrategyListFragment.KEY_STRATEGY, strategy);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStrategyList.size();
    }

    void removeItem(int position){
        long id = mStrategyList.remove(position)._id;
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

        StrategyDbHelper dbHelper = new StrategyDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = StrategyContract.StrategyEntry._ID+"=?";
        String[] selectArgs = {String.valueOf(id)};
        db.delete(StrategyContract.StrategyEntry.TABLE_NAME, selection, selectArgs);
        db.close();
    }


    void addItem(int position, Strategy strategy) {
        mStrategyList.add(position, strategy);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());

        StrategyDbHelper dbHelper = new StrategyDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StrategyContract.StrategyEntry._ID, strategy._id);
        values.put(StrategyContract.StrategyEntry.COLUMN_MIN_BET, strategy.minBet);
        values.put(StrategyContract.StrategyEntry.COLUMN_MAX_BET, strategy.maxBet);
        values.put(StrategyContract.StrategyEntry.COLUMN_STRATEGY_CHOICE, strategy.strategyChoice);
        values.put(StrategyContract.StrategyEntry.COLUMN_NAME, strategy.name);
        db.insert(StrategyContract.StrategyEntry.TABLE_NAME, null, values);
        db.close();
    }

}
