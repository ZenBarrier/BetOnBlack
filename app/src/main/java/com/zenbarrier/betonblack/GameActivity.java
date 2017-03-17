package com.zenbarrier.betonblack;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private Strategy mStrategy;
    private ViewAnimator mAnimator;
    private Button mButtonStartBet, mButtonLost, mButtonWon, mButtonChangeBet;
    private TextView mTextCash, mTextMin, mTextMax, mTextName, mTextOdds, mTextRounds, mTextStrategyMode, mTextBettingAmount;
    private NumberPicker mPickerBet;
    private int mCash, mMin, mMax, mRounds, mBet, mStrategyChoice, mStartingBet, mStartingCash;
    private double mOdds;
    private String mName, mStrategyMode;
    private List<Integer> mBetSequence;
    private boolean mJustStarted = true;
    private static final double DOUBLE_ZERO_ODDS = 0.47368421052;//18 black 38 numbers
    private static final double SINGLE_ZERO_ODDS = 0.48648648648;//18 black 37 numbers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        this.setSupportActionBar((Toolbar) findViewById(R.id.toolbar_game));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();

        Intent intent = getIntent();
        mStrategy = (Strategy) intent.getSerializableExtra(MainActivity.KEY_STRATEGY);

        mAnimator = (ViewAnimator) findViewById(R.id.viewAnimator_game_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    public void setCash(View view){
        EditText cashText = (EditText) findViewById(R.id.editText_game0_cash);
        String cashString = cashText.getText().toString();
        if(cashString.length() == 0){
            cashText.setError("Enter amount");
        }else if(Integer.parseInt(cashString)<mStrategy.minBet){
            cashText.setError("You need at least $"+mStrategy.minBet+" to make a bet.");
        }else{
            mCash = Integer.parseInt(cashString);
            mStartingCash = mCash;
            initializeInfo();
            calculateOdds(mMin);
            setInfo();
            mAnimator.showNext();
        }
    }

    private void initializeInfo() {
        mButtonLost = (Button) findViewById(R.id.button_game_lose);
        mButtonWon = (Button) findViewById(R.id.button_game_win);
        mButtonStartBet = (Button) findViewById(R.id.button_game_startingBet);
        mButtonChangeBet = (Button) findViewById(R.id.button_game_changeBet);

        mTextCash = (TextView) findViewById(R.id.textView_game_cash);
        mTextMin = (TextView) findViewById(R.id.textView_game_min);
        mTextMax = (TextView) findViewById(R.id.textView_game_max);
        mTextName = (TextView) findViewById(R.id.textView_game_name);
        mTextOdds = (TextView) findViewById(R.id.textView_game_odds);
        mTextRounds = (TextView) findViewById(R.id.textView_game_rounds);
        mTextStrategyMode = (TextView) findViewById(R.id.textView_game_strategyMode);
        mTextBettingAmount = (TextView) findViewById(R.id.textView_game_bettingAmount);

        mPickerBet = (NumberPicker) findViewById(R.id.numberPicker_game_bettingAmount);

        mMin = mStrategy.minBet;
        mMax = mStrategy.maxBet;
        String[] modes = getResources().getStringArray(R.array.strategy_array);
        mStrategyMode = modes[mStrategy.strategyChoice];
        mStrategyChoice = mStrategy.strategyChoice;
        mName = mStrategy.name;

        mTextName.setText(mName);
        mTextMin.setText(String.valueOf(mMin));
        mTextMax.setText(String.valueOf(mMax));
        mTextStrategyMode.setText(mStrategyMode);

        mPickerBet.setMinValue(mMin);
        mBet = mMin;
        mBetSequence = new ArrayList<>();
        mPickerBet.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                calculateOdds(newVal);
                setInfo();
            }
        });
    }

    private void setInfo() {
        mTextCash.setText(String.valueOf(mCash));
        mTextOdds.setText(String.valueOf(mOdds));
        mTextRounds.setText(String.valueOf(mRounds));
        mTextBettingAmount.setText(String.valueOf(mBet));

        mPickerBet.setMaxValue(Math.min(mCash, mMax));
        //mPickerBet.setValue(Math.min(mBet, mMax));
    }

    public void wonBet(View view){
        mJustStarted = false;
        mCash+=mBet;
        mBet = nextBet(false);
        calculateOdds(mBet);
        setInfo();
    }

    public void lostBet(View view){
        mJustStarted = false;
        mCash-=mBet;
        mBet = nextBet(true);
        calculateOdds(mBet);
        setInfo();
    }

    public void setBet(View view){
        mPickerBet.setEnabled(false);
        mPickerBet.setVisibility(View.INVISIBLE);
        mButtonStartBet.setVisibility(View.INVISIBLE);

        mButtonLost.setVisibility(View.VISIBLE);
        mButtonWon.setVisibility(View.VISIBLE);
        mButtonChangeBet.setVisibility(View.VISIBLE);
        mTextBettingAmount.setVisibility(View.VISIBLE);
        mTextBettingAmount.setText(String.valueOf(mPickerBet.getValue()));
        if(mJustStarted) mStartingBet = mPickerBet.getValue();
        mBet = mPickerBet.getValue();
    }
    public void changeBet(View view){
        mPickerBet.setEnabled(true);
        mTextBettingAmount.setVisibility(View.INVISIBLE);
        mButtonLost.setVisibility(View.INVISIBLE);
        mButtonWon.setVisibility(View.INVISIBLE);
        mButtonChangeBet.setVisibility(View.INVISIBLE);

        mButtonStartBet.setVisibility(View.VISIBLE);
        if(!mJustStarted) mButtonStartBet.setText("Set Betting Amount");
        mPickerBet.setVisibility(View.VISIBLE);
    }

    private int nextBet(boolean justLost) {
        int bet;
        switch (mStrategyChoice){
            case 0://Martingale
                bet = justLost ? Math.min(mBet << 1, mMax) : mStartingBet;
                break;
            case 1://Paroli
            case 2://Fibonaci
            default:
                bet = mStartingBet;
        }
        if(mCash < bet){
            return mCash < mMin ? 0 : mCash;
        }
        else return bet;
    }

    void calculateOdds(int betting){
        int cash = mCash;
        int bet = betting;
        int rounds = 0;
        while(cash >= bet && bet <= mMax && bet != 0){
            rounds++;
            cash-=bet;
            switch (mStrategyChoice){
                case 0://Martingale
                    bet <<= 1;
                    break;
                case 1://Paroli
                case 2://Fibonaci
                    fibonacci(mStartingBet, rounds);
                default:
            }
        }
        mRounds = rounds;
        mOdds = Math.pow(1.0-DOUBLE_ZERO_ODDS, rounds);
    }

    public static int fibonacci(int startingBet, int rounds) {
        if(rounds <= 2) return startingBet;
        return fibonacci(startingBet, rounds-2) + fibonacci(startingBet, rounds-1);
    }

    @Override
    public void onBackPressed() {
        int index = mAnimator.getDisplayedChild();
        if(index == 0){
            super.onBackPressed();
        }else {
            mAnimator.showPrevious();
        }
    }
}
