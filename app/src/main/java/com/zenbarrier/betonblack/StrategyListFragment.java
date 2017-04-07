package com.zenbarrier.betonblack;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StrategyListFragment extends Fragment {

    public static final int REQUEST_NEW_STRATEGY = 1;
    public static final int REQUEST_EDIT_STRATEGY = 2;
    public static final String KEY_STRATEGY = "strategy";
    public static final String KEY_POSITION = "position";

    RecyclerView mRecyclerView;
    StrategyAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    List<Strategy> mStrategyList;
    StrategyDbHelper mDbHelper;
    View mView;

    public static Fragment newInstance(){
        return new StrategyListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_strategy_list, container, false);

        initValues();
        initSwipe();
        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_NEW_STRATEGY:
                if(resultCode == Activity.RESULT_OK){
                    Strategy strategy = (Strategy) data.getSerializableExtra(KEY_STRATEGY);
                    mStrategyList.add(strategy);
                }
                break;
            case REQUEST_EDIT_STRATEGY:
                if(resultCode == Activity.RESULT_OK){
                    int position = data.getIntExtra(KEY_POSITION,0);
                    Strategy strategy = (Strategy) data.getSerializableExtra(KEY_STRATEGY);
                    mStrategyList.set(position, strategy);
                }
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    private class DatabaseLoader extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = mDbHelper.getAll();

            while(cursor.moveToNext()){
                long itemId = cursor.getLong(cursor.getColumnIndex(StrategyContract.StrategyEntry._ID));
                String itemName = cursor.getString(cursor.getColumnIndex(StrategyContract.StrategyEntry.COLUMN_NAME));
                int itemMin = cursor.getInt(cursor.getColumnIndex(StrategyContract.StrategyEntry.COLUMN_MIN_BET));
                int itemMax = cursor.getInt(cursor.getColumnIndex(StrategyContract.StrategyEntry.COLUMN_MAX_BET));
                int itemChoice = cursor.getInt(cursor.getColumnIndex(StrategyContract.StrategyEntry.COLUMN_STRATEGY_CHOICE));
                Strategy strategy = new Strategy(itemId, itemName, itemMin, itemMax, itemChoice);
                mStrategyList.add(strategy);
            }
            cursor.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDbHelper.close();
        }
    }

    public void connectFab(FloatingActionButton fab){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getActivity(),NewStrategyActivity.class);
                StrategyListFragment.this.startActivityForResult(intent, REQUEST_NEW_STRATEGY);
            }
        });
    }

    private void initValues(){
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recylerView_main_list);
        mStrategyList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StrategyAdapter(getActivity(), mStrategyList);
        mRecyclerView.setAdapter(mAdapter);
        mDbHelper = new StrategyDbHelper(getActivity());

        DatabaseLoader databaseLoader = new DatabaseLoader();
        databaseLoader.execute();
    }

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    deleteStrategy(position);
                } else {
                    editStrategy(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    Paint p = new Paint();
                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else if (dX < 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else{
                        c.drawColor(Color.TRANSPARENT);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void editStrategy(int position) {
        Intent intent = new Intent( getActivity(),NewStrategyActivity.class);
        Strategy strategy = mStrategyList.get(position);
        intent.putExtra(KEY_STRATEGY, strategy);
        intent.putExtra(KEY_POSITION, position);
        startActivityForResult(intent, REQUEST_EDIT_STRATEGY);
    }

    private void deleteStrategy(int position) {
        Strategy strategy = mStrategyList.get(position);
        mAdapter.removeItem(position);
        String snackText = String.format(Locale.getDefault(), "Deleted %s", strategy.name);
        Snackbar snackbar = Snackbar.make(mView.findViewById(R.id.recylerView_main_list), snackText, Snackbar.LENGTH_INDEFINITE)
                .setAction("Undo", new UndoListener(position, strategy));
        snackbar.show();
    }
    private class UndoListener implements View.OnClickListener{
        private Strategy mStrategy;
        private int mPosition;
        UndoListener(int position, Strategy strategy){
            mPosition = position;
            mStrategy = strategy;
        }
        @Override
        public void onClick(View v) {
            mAdapter.addItem(mPosition, mStrategy);
        }
    }

}
