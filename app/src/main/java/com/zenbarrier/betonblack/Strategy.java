package com.zenbarrier.betonblack;

import java.io.Serializable;

/**
 * Created by Anthony on 3/6/2017.
 * This file is the fragment that holds all the preferences
 */

class Strategy implements Serializable{
    String name;
    long _id;
    int minBet;
    int maxBet;
    int strategyChoice;

    Strategy(long id, String strategyName, int min, int max, int choice){
        name = strategyName;
        minBet = min;
        maxBet = max;
        strategyChoice = choice;
        _id = id;
    }
}
