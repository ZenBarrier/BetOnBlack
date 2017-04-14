package com.zenbarrier.betonblack;

/**
 * Created by Anthony on 4/14/2017.
 * This file is the fragment that holds all the preferences
 */

public class History {
    long _id;
    String name;
    int startCash;
    int endCash;
    boolean isManual;
    String date;
    
    public History(long _id, String name, int startCash, int endCash, boolean isManual, String date){
        this._id = _id;
        this.name = name;
        this.startCash = startCash;
        this.endCash = endCash;
        this.isManual = isManual;
        this.date = date;
    }
}
