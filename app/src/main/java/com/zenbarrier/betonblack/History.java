package com.zenbarrier.betonblack;

import java.io.Serializable;

/**
 * Created by Anthony on 4/14/2017.
 * This file is the fragment that holds all the preferences
 */

class History implements Serializable{
    long _id;
    String name;
    int startCash;
    int endCash;
    boolean isManual;
    String date;


    History(String name, int startCash, int endCash, boolean isManual){
        this(0, name, startCash, endCash, isManual);
    }

    private History(long _id, String name, int startCash, int endCash, boolean isManual){
        this(_id, name, startCash, endCash, isManual, null);
    }
    
    History(long _id, String name, int startCash, int endCash, boolean isManual, String date){
        this._id = _id;
        this.name = name;
        this.startCash = startCash;
        this.endCash = endCash;
        this.isManual = isManual;
        this.date = date;
    }
}
