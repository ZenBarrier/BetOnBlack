package com.zenbarrier.betonblack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anthony on 3/6/2017.
 * This file is the fragment that holds all the preferences
 */

public class StrategyDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Strategy.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + StrategyContract.StrategyEntry.TABLE_NAME + " (" +
                    StrategyContract.StrategyEntry._ID + " INTEGER PRIMARY KEY," +
                    StrategyContract.StrategyEntry.COLUMN_STRATEGY_NAME + " TEXT," +
                    StrategyContract.StrategyEntry.COLUMN_MIN_BET + " INT," +
                    StrategyContract.StrategyEntry.COLUMN_MAX_BET + " INT," +
                    StrategyContract.StrategyEntry.COLUMN_STRATEGY_CHOICE + " INT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StrategyContract.StrategyEntry.TABLE_NAME;

    public StrategyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
