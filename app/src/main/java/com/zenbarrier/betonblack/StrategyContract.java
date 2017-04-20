package com.zenbarrier.betonblack;

import android.provider.BaseColumns;

/**
 * Created by Anthony on 3/6/2017.
 * This file is the fragment that holds all the preferences
 */

class StrategyContract {
    private StrategyContract(){}

    static class StrategyEntry implements BaseColumns{
        static final String TABLE_NAME = "strategies";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_MIN_BET = "minBet";
        static final String COLUMN_MAX_BET = "maxBet";
        static final String COLUMN_STRATEGY_CHOICE = "choice";
        static final String COLUMN_POSITION = "position";

    }

    static class HistoryEntry implements BaseColumns{
        static final String TABLE_NAME = "history";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_DATE = "date";
        static final String COLUMN_MANUAL = "manual";
        static final String COLUMN_START_CASH = "startCash";
        static final String COLUMN_END_CASH = "endCash";
    }
}
