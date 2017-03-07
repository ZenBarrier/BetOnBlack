package com.zenbarrier.betonblack;

import android.provider.BaseColumns;

/**
 * Created by Anthony on 3/6/2017.
 * This file is the fragment that holds all the preferences
 */

public class StrategyContract {
    private StrategyContract(){}

    public static class StrategyEntry implements BaseColumns{
        public static final String TABLE_NAME = "strategies";
        public static final String COLUMN_STRATEGY_NAME = "name";
        public static final String COLUMN_MIN_BET = "minBet";
        public static final String COLUMN_MAX_BET = "maxBet";
        public static final String COLUMN_STRATEGY_CHOICE = "choice";

    }
}
