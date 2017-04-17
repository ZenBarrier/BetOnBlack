package com.zenbarrier.betonblack;

import android.app.Activity;
import android.content.Intent;
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
        if(intent.hasExtra(StrategyListFragment.KEY_STRATEGY)){
            Strategy strategy = (Strategy) intent.getSerializableExtra(StrategyListFragment.KEY_STRATEGY);
            mPosition = intent.getIntExtra(StrategyListFragment.KEY_POSITION, 0);
            mStrategyName.setText(strategy.name);
            mMinBet.setText(String.valueOf(strategy.minBet));
            mMaxBet.setText(String.valueOf(strategy.maxBet));
            mStrategyChoice.setSelection(strategy.strategyChoice);
            mIsEditing = true;
            mEditId = strategy._id;
        }
    }


    public void saveStrategy(View view) {
        try {
            String strategyName = mStrategyName.getText().toString();
            int minBet = Integer.parseInt(mMinBet.getText().toString());
            int maxBet = Integer.parseInt(mMaxBet.getText().toString());
            int strategyChoice = mStrategyChoice.getSelectedItemPosition();

            Strategy strategy = new Strategy(0, strategyName, minBet, maxBet, strategyChoice);
            if(mIsEditing){
                strategy._id = mEditId;
                mDbHelper.update(strategy);
            }
            else {
                strategy._id = mDbHelper.add(strategy);
            }
            Intent data = new Intent();
            data.putExtra(StrategyListFragment.KEY_STRATEGY ,strategy);
            data.putExtra(StrategyListFragment.KEY_POSITION ,mPosition);
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
