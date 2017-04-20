package com.zenbarrier.betonblack;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private OnStartDragListener mDragListener;

    interface OnStartDragListener {

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    StrategyAdapter(Context context, List<Strategy> strategyList, OnStartDragListener dragListener){
        mStrategyList = strategyList;
        mContext = context;
        mDragListener = dragListener;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
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

        holder.view.findViewById(R.id.imageView_mainRecyclerlist_drag).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mDragListener.onStartDrag(holder);
                }
                return false;
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
        dbHelper.deleteStrategy(id);
    }

    void onItemMove(int fromPosition, int toPosition){
        Strategy prev = mStrategyList.remove(fromPosition);
        Log.d("Move", prev.name+" moved from "+fromPosition+" to "+toPosition);
        mStrategyList.add(toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    void updatePositions() {
        StrategyDbHelper dbHelper = new StrategyDbHelper(mContext);
        dbHelper.updatePositions(mStrategyList);
    }

    void addItem(int position, Strategy strategy) {
        mStrategyList.add(position, strategy);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());

        StrategyDbHelper dbHelper = new StrategyDbHelper(mContext);
        dbHelper.add(strategy);
    }

}
