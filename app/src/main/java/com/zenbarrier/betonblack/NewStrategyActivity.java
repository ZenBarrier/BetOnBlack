package com.zenbarrier.betonblack;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Spinner;

public class NewStrategyActivity extends AppCompatActivity {

    private EditText mStrategyName;
    private EditText mMinBet, mMaxBet;
    private Spinner mStrategyChoice;
    private StrategyDbHelper mDbHelper;
    private boolean mIsEditing = false;
    private long mEditId;
    private int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_strategy);
        setFinishOnTouchOutside(false);

        mStrategyName = (EditText) findViewById(R.id.editText_newStrategy_strategyName);
        mMinBet = (EditText) findViewById(R.id.editText_newStrategy_minimumBet);
        mMaxBet = (EditText) findViewById(R.id.editText_newStrategy_maximumBet);
        mStrategyChoice = (Spinner) findViewById(R.id.spinner_newStrategy_strategyChoice);
        mDbHelper = new StrategyDbHelper(this);

        Intent intent = getIntent();
        if(intent.hasExtra(MainActivity.KEY_STRATEGY)){
            Strategy strategy = (Strategy) intent.getSerializableExtra(MainActivity.KEY_STRATEGY);
            mPosition = intent.getIntExtra(MainActivity.KEY_POSITION, 0);
            mStrategyName.setText(strategy.name);
            mMinBet.setText(String.valueOf(strategy.minBet));
            mMaxBet.setText(String.valueOf(strategy.maxBet));
            mStrategyChoice.setSelection(strategy.strategyChoice);
            mIsEditing = true;
            mEditId = strategy._id;
        }
    }


    public void saveStrategy(View view) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            String strategyName = mStrategyName.getText().toString();
            int minBet = Integer.parseInt(mMinBet.getText().toString());
            int maxBet = Integer.parseInt(mMaxBet.getText().toString());
            int strategyChoice = mStrategyChoice.getSelectedItemPosition();

            ContentValues values = new ContentValues();
            values.put(StrategyContract.StrategyEntry.COLUMN_MIN_BET, minBet);
            values.put(StrategyContract.StrategyEntry.COLUMN_MAX_BET, maxBet);
            values.put(StrategyContract.StrategyEntry.COLUMN_STRATEGY_CHOICE, strategyChoice);
            values.put(StrategyContract.StrategyEntry.COLUMN_STRATEGY_NAME, strategyName);

            Strategy strategy = new Strategy(0, strategyName, minBet, maxBet, strategyChoice);
            if(mIsEditing){
                String selection = StrategyContract.StrategyEntry._ID+"=?";
                String[] selectionArgs = {String.valueOf(mEditId)};
                db.update(StrategyContract.StrategyEntry.TABLE_NAME, values, selection, selectionArgs);
                strategy._id = mEditId;
            }
            else {
                strategy._id = db.insert(StrategyContract.StrategyEntry.TABLE_NAME, null, values);
            }
            db.close();
            Intent data = new Intent();
            data.putExtra(MainActivity.KEY_STRATEGY ,strategy);
            data.putExtra(MainActivity.KEY_POSITION ,mPosition);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
        catch (NumberFormatException e){
            findViewById(R.id.constraintLayout_newStrategy_root).startAnimation(AnimationUtils.loadAnimation(this, R.anim.vibrate));
        }
    }

    public void cancelStrategy(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
