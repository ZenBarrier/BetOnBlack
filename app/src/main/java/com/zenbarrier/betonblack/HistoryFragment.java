package com.zenbarrier.betonblack;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {

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
        return mView;
    }

    private void initValues(){
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recylerView_main_list);
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
                Toast.makeText(getActivity(), "hello", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
