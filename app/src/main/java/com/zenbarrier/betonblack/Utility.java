package com.zenbarrier.betonblack;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Anthony on 4/18/2017.
 * This file is the fragment that holds all the preferences
 */

public class Utility {
    static void hideKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
