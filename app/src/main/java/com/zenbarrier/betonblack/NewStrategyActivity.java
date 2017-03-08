package com.zenbarrier.betonblack;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Spinner;

public class NewStrategyActivity extends AppCompatActivity {

    private EditText mStrategyName;
    private EditText mMinBet, mMaxBet;
    private Spinner mStrategyChoice;
    private StrategyDbHelper mDbHelper;

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_strategy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            Long id = db.insert(StrategyContract.StrategyEntry.TABLE_NAME, null, values);

            Strategy strategy = new Strategy(id, strategyName, minBet, maxBet, strategyChoice);
            Intent data = new Intent();
            data.putExtra(MainActivity.KEY_STRATEGY ,strategy);
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
