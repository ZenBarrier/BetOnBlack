package com.zenbarrier.betonblack;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.zenbarrier.betonblack.StrategyListFragment.KEY_STRATEGY;


public class HistoryFragment extends Fragment {

    private static final int REQUEST_NEW_HISTORY = 1;
    private static final int REQUEST_EDIT_HISTORY = 2;
    private static final String KEY_POSITION = "KEY_POSITION";
    private static final String KEY_HISTORY = "KEY_HISTORY";
    List<History> mHistoryList;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    HistoryAdapter mAdapter;
    View mView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_history, container, false);

        initValues();

        return mView;
    }

    private void initValues(){
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recylerView_history_list);
        mHistoryList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HistoryAdapter(getActivity(), mHistoryList);
        mRecyclerView.setAdapter(mAdapter);

        DatabaseLoader databaseLoader = new DatabaseLoader();
        databaseLoader.execute();
    }

    private class DatabaseLoader extends AsyncTask<Void, Void, Void> {
        private StrategyDbHelper mDbHelper;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDbHelper = new StrategyDbHelper(getActivity());
        }

        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = mDbHelper.getAllHistory();

            while(cursor.moveToNext()){
                long id = cursor.getLong(cursor.getColumnIndex(StrategyContract.HistoryEntry._ID));
                String name = cursor.getString(cursor.getColumnIndex(StrategyContract.HistoryEntry.COLUMN_NAME));
                int startCash = cursor.getInt(cursor.getColumnIndex(StrategyContract.HistoryEntry.COLUMN_START_CASH));
                int endCash = cursor.getInt(cursor.getColumnIndex(StrategyContract.HistoryEntry.COLUMN_END_CASH));
                boolean isManual = cursor.getInt(cursor.getColumnIndex(StrategyContract.HistoryEntry.COLUMN_MANUAL)) != 0;
                String date = cursor.getString(cursor.getColumnIndex(StrategyContract.HistoryEntry.COLUMN_DATE));
                History history = new History(id, name, startCash, endCash, isManual, date);
                mHistoryList.add(history);
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
                Intent intent = new Intent( getActivity(),NewHistoryActivity.class);
                HistoryFragment.this.startActivityForResult(intent, REQUEST_NEW_HISTORY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_NEW_HISTORY:
                if(resultCode == Activity.RESULT_OK){
                    History history = (History) data.getSerializableExtra(KEY_HISTORY);
                    mHistoryList.add(history);
                }
                break;
            case REQUEST_EDIT_HISTORY:
                if(resultCode == Activity.RESULT_OK){
                    int position = data.getIntExtra(KEY_POSITION,0);
                    History history = (History) data.getSerializableExtra(KEY_STRATEGY);
                    mHistoryList.set(position, history);
                }
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

}
