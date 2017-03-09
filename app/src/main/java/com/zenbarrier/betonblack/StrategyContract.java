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
        static final String COLUMN_STRATEGY_NAME = "name";
        static final String COLUMN_MIN_BET = "minBet";
        static final String COLUMN_MAX_BET = "maxBet";
        static final String COLUMN_STRATEGY_CHOICE = "choice";

    }
}
