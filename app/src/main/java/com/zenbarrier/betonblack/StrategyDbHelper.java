package com.zenbarrier.betonblack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anthony on 3/6/2017.
 * This file is the fragment that holds all the preferences
 */

class StrategyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Strategy.db";

    private static final String SQL_CREATE_STRATEGY_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + StrategyContract.StrategyEntry.TABLE_NAME + " (" +
                    StrategyContract.StrategyEntry._ID + " INTEGER PRIMARY KEY," +
                    StrategyContract.StrategyEntry.COLUMN_NAME + " TEXT," +
                    StrategyContract.StrategyEntry.COLUMN_MIN_BET + " INT," +
                    StrategyContract.StrategyEntry.COLUMN_MAX_BET + " INT," +
                    StrategyContract.StrategyEntry.COLUMN_STRATEGY_CHOICE + " INT)";

    private static final String SQL_CREATE_HISTORY_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + StrategyContract.HistoryEntry.TABLE_NAME + " (" +
                    StrategyContract.HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                    StrategyContract.HistoryEntry.COLUMN_NAME + " TEXT," +
                    StrategyContract.HistoryEntry.COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    StrategyContract.HistoryEntry.COLUMN_START_CASH + " INT," +
                    StrategyContract.HistoryEntry.COLUMN_END_CASH + " INT," +
                    StrategyContract.HistoryEntry.COLUMN_MANUAL + " INT DEFAULT 0)";

    private static final String SQL_DELETE_STRATEGY_ENTRIES =
            "DROP TABLE IF EXISTS " + StrategyContract.StrategyEntry.TABLE_NAME;
    private static final String SQL_DELETE_HISTORY_ENTRIES =
            "DROP TABLE IF EXISTS " + StrategyContract.HistoryEntry.TABLE_NAME;

    StrategyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STRATEGY_ENTRIES);
        db.execSQL(SQL_CREATE_HISTORY_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_STRATEGY_ENTRIES);
        db.execSQL(SQL_DELETE_HISTORY_ENTRIES);
        onCreate(db);
    }

    Cursor getAllStrategies(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                StrategyContract.StrategyEntry._ID,
                StrategyContract.StrategyEntry.COLUMN_NAME,
                StrategyContract.StrategyEntry.COLUMN_MIN_BET,
                StrategyContract.StrategyEntry.COLUMN_MAX_BET,
                StrategyContract.StrategyEntry.COLUMN_STRATEGY_CHOICE,
        };
        String sortOrder = StrategyContract.StrategyEntry.COLUMN_NAME + " ASC";
        return db.query(
                StrategyContract.StrategyEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    Cursor getAllHistory(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                StrategyContract.HistoryEntry._ID,
                StrategyContract.HistoryEntry.COLUMN_NAME,
                StrategyContract.HistoryEntry.COLUMN_START_CASH,
                StrategyContract.HistoryEntry.COLUMN_END_CASH,
                StrategyContract.HistoryEntry.COLUMN_DATE,
                StrategyContract.HistoryEntry.COLUMN_MANUAL,
        };
        String sortOrder = StrategyContract.HistoryEntry.COLUMN_DATE + " ASC";
        return db.query(
                StrategyContract.HistoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    long add(History history){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(history._id > 0) {values.put(StrategyContract.HistoryEntry._ID, history._id);}
        values.put(StrategyContract.HistoryEntry.COLUMN_NAME, history.name);
        values.put(StrategyContract.HistoryEntry.COLUMN_START_CASH, history.startCash);
        values.put(StrategyContract.HistoryEntry.COLUMN_END_CASH, history.endCash);
        values.put(StrategyContract.HistoryEntry.COLUMN_MANUAL, history.isManual);
        long id = db.insert(StrategyContract.HistoryEntry.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    void update(History history){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StrategyContract.HistoryEntry.COLUMN_NAME, history.name);
        values.put(StrategyContract.HistoryEntry.COLUMN_START_CASH, history.startCash);
        values.put(StrategyContract.HistoryEntry.COLUMN_END_CASH, history.endCash);
        String selection = StrategyContract.HistoryEntry._ID+"=?";
        String[] selectionArgs = {String.valueOf(history._id)};
        db.update(StrategyContract.HistoryEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    long add(Strategy strategy){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(strategy._id > 0) {values.put(StrategyContract.StrategyEntry._ID, strategy._id);}
        values.put(StrategyContract.StrategyEntry.COLUMN_MIN_BET, strategy.minBet);
        values.put(StrategyContract.StrategyEntry.COLUMN_MAX_BET, strategy.maxBet);
        values.put(StrategyContract.StrategyEntry.COLUMN_STRATEGY_CHOICE, strategy.strategyChoice);
        values.put(StrategyContract.StrategyEntry.COLUMN_NAME, strategy.name);
        long id = db.insert(StrategyContract.StrategyEntry.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    void update(Strategy strategy){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StrategyContract.StrategyEntry.COLUMN_MIN_BET, strategy.minBet);
        values.put(StrategyContract.StrategyEntry.COLUMN_MAX_BET, strategy.maxBet);
        values.put(StrategyContract.StrategyEntry.COLUMN_STRATEGY_CHOICE, strategy.strategyChoice);
        values.put(StrategyContract.StrategyEntry.COLUMN_NAME, strategy.name);
        String selection = StrategyContract.StrategyEntry._ID+"=?";
        String[] selectionArgs = {String.valueOf(strategy._id)};
        db.update(StrategyContract.StrategyEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    void deleteStrategy(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = StrategyContract.StrategyEntry._ID+"=?";
        String[] selectArgs = {String.valueOf(id)};
        db.delete(StrategyContract.StrategyEntry.TABLE_NAME, selection, selectArgs);
        db.close();
    }

    void deleteHistory(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = StrategyContract.HistoryEntry._ID+"=?";
        String[] selectArgs = {String.valueOf(id)};
        db.delete(StrategyContract.HistoryEntry.TABLE_NAME, selection, selectArgs);
        db.close();
    }
}
