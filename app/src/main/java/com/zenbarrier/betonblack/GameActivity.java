package com.zenbarrier.betonblack;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    public final static String KEY_HISTORY = "KEY_HISTORY";
    public final static String KEY_SAVED = "KEY_SAVED";

    private Strategy mStrategy;
    private ViewAnimator mAnimator;
    private Button mButtonStartBet, mButtonLost, mButtonWon, mButtonChangeBet;
    private TextView mTextCash, mTextWinProfit, mTextTotalProfit, mTextMin, mTextMax;
    private TextView mTextOdds, mTextRounds, mTextStrategyMode, mTextBettingAmount, mTextOddsLabel;
    private NumberPicker mPickerBet;
    private int mCash, mMin, mMax, mRounds, mBet, mStrategyChoice, mStartingBet, mStartingCash;
    private int mFibStreak = 1;
    private double mOdds;
    private AdView mAdView;
    private String mStrategyMode;
    private boolean mJustStarted = true;
    private Menu mMenu;
    private static final double DOUBLE_ZERO_ODDS = 0.47368421052;//18 black 38 numbers
    //private static final double SINGLE_ZERO_ODDS = 0.48648648648;//18 black 37 numbers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        this.setSupportActionBar((Toolbar) findViewById(R.id.toolbar_game));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load an ad into the AdMob banner view.
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        mStrategy = (Strategy) intent.getSerializableExtra(StrategyListFragment.KEY_STRATEGY);
        setTitle(mStrategy.name);

        mAnimator = (ViewAnimator) findViewById(R.id.viewAnimator_game_view);
        findViewById(R.id.editText_game0_cash).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    setCash(v);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    protected void onDestroy() {
        mAdView.setAdListener(null);
        ((ViewGroup)mAdView.getParent()).removeView(mAdView);
        mAdView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        mMenu = menu;
        return true;
    }


    public void setCash(View view){
        Utility.hideKeyboard(this);
        EditText cashText = (EditText) findViewById(R.id.editText_game0_cash);
        String cashString = cashText.getText().toString();
        if(cashString.length() == 0){
            cashText.setError(getString(R.string.error_empty_number));
        }else if(Integer.parseInt(cashString)<mStrategy.minBet){
            cashText.setError(String.format(Locale.getDefault(), getString(R.string.error_min_bet),mStrategy.minBet));
        }else{
            mCash = Integer.parseInt(cashString);
            initializeInfo();
            mStartingCash = mCash;
            mStartingBet = mMin;
            calculateOdds(mMin);
            setInfo();
            changeBet(null);
            mAnimator.showNext();
            mMenu.findItem(R.id.action_save).setVisible(true);
        }
    }

    private void initializeInfo() {
        mButtonLost = (Button) findViewById(R.id.button_game_lose);
        mButtonWon = (Button) findViewById(R.id.button_game_win);
        mButtonStartBet = (Button) findViewById(R.id.button_game_startingBet);
        mButtonChangeBet = (Button) findViewById(R.id.button_game_changeBet);

        mTextCash = (TextView) findViewById(R.id.textView_game_cash);
        mTextWinProfit = (TextView) findViewById(R.id.textView_game_profit);
        mTextTotalProfit = (TextView) findViewById(R.id.textView_game_totalProfit);
        mTextMin = (TextView) findViewById(R.id.textView_game_min);
        mTextMax = (TextView) findViewById(R.id.textView_game_max);
        mTextOdds = (TextView) findViewById(R.id.textView_game_odds);
        mTextOddsLabel = (TextView) findViewById(R.id.textView_game_oddsLabel);
        mTextRounds = (TextView) findViewById(R.id.textView_game_rounds);
        mTextStrategyMode = (TextView) findViewById(R.id.textView_game_strategyMode);
        mTextBettingAmount = (TextView) findViewById(R.id.textView_game_bettingAmount);

        mPickerBet = (NumberPicker) findViewById(R.id.numberPicker_game_bettingAmount);

        mMin = mStrategy.minBet;
        mMax = mStrategy.maxBet;
        String[] modes = getResources().getStringArray(R.array.strategy_array);
        mStrategyMode = modes[mStrategy.strategyChoice];
        mStrategyChoice = mStrategy.strategyChoice;

        mTextMin.setText(String.valueOf(mMin));
        mTextMax.setText(String.valueOf(mMax));
        mTextStrategyMode.setText(mStrategyMode);

        mPickerBet.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mPickerBet.setMinValue(mMin);
        mBet = mMin;
        mPickerBet.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mStartingBet = newVal;
                mBet = newVal;
                calculateOdds(newVal);
                setInfo();
            }
        });
    }

    private void setInfo() {
        mTextCash.setText(String.format(Locale.getDefault(),getString(R.string.dollar_value), mCash));

        int profit = mCash - mStartingCash;
        mTextTotalProfit.setText(String.format(Locale.getDefault(),getString(R.string.dollar_value), profit));
        if(profit>=0){
            mTextTotalProfit.setTextColor(ContextCompat.getColor(this, R.color.colorProfit));
        }else{
            mTextTotalProfit.setTextColor(ContextCompat.getColor(this, R.color.colorLoss));
        }

        mTextOdds.setText(String.valueOf(String.format(Locale.getDefault(),getString(R.string.percent_value), mOdds * 100.0)));
        mTextOddsLabel.setText(String.format(Locale.getDefault(), getResources().getString(R.string.game_odds_label), mRounds));
        mTextRounds.setText(String.valueOf(mRounds));
        mTextBettingAmount.setText(String.format(Locale.getDefault(),getString(R.string.dollar_value), mBet));

        mTextWinProfit.setText(String.format(Locale.getDefault(),getString(R.string.dollar_value), mCash - mStartingCash + mBet));


        mPickerBet.setMaxValue(Math.min(mCash, mMax));
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
        mButtonChangeBet.setVisibility(View.INVISIBLE);
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
        mTextBettingAmount.setText(String.format(Locale.getDefault(),getString(R.string.dollar_value), mPickerBet.getValue()));
        mStartingBet = mPickerBet.getValue();
        mBet = mPickerBet.getValue();
    }
    public void changeBet(View view){
        mPickerBet.setEnabled(true);
        mTextBettingAmount.setVisibility(View.INVISIBLE);
        mButtonLost.setVisibility(View.INVISIBLE);
        mButtonWon.setVisibility(View.INVISIBLE);
        mButtonChangeBet.setVisibility(View.INVISIBLE);

        mButtonStartBet.setVisibility(View.VISIBLE);
        if(!mJustStarted) mButtonStartBet.setText(R.string.game_set_betting_amount);
        mPickerBet.setVisibility(View.VISIBLE);
    }

    private int nextBet(boolean justLost) {
        int bet;
        switch (mStrategyChoice){
            case 0://Martingale
                if(justLost){
                    bet = Math.min(mBet << 1, mMax);
                }else{
                    bet = mStartingBet;
                    mButtonChangeBet.setVisibility(View.VISIBLE);
                }
                break;
            case 1://D'Alembert
                if(justLost){
                    bet = Math.min(mBet + 1, mMax);
                }else{
                    bet = Math.max(mBet - 1, mMin);
                }
                break;
            case 2://Fibonaci
                mFibStreak = justLost ? mFibStreak + 1 : Math.max(1, mFibStreak-2);
                bet = fibonacci(mStartingBet, mFibStreak);
                if(mFibStreak == 1) mButtonChangeBet.setVisibility(View.VISIBLE);
                break;
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
        int fibStreak = mFibStreak;
        int rounds = 0;
        while(cash >= bet && bet <= mMax && bet != 0){
            rounds++;
            cash-=bet;
            switch (mStrategyChoice){
                case 0://Martingale
                    bet <<= 1;
                    break;
                case 1://D'Alembert
                    bet++;
                    break;
                case 2://Fibonaci
                    fibStreak++;
                    bet = fibonacci(mStartingBet, fibStreak);
                    break;
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
            if(mJustStarted){
                mAnimator.showPrevious();
                mMenu.findItem(R.id.action_save).setVisible(false);
            }
            else super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Intent intent = new Intent(this, MainTabbedActivity.class);
            intent.putExtra(KEY_SAVED, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void saveData(MenuItem item) {

        History history = new History(0, mStrategy.name, mStartingCash, mCash, false, null);
        Intent intent = new Intent(this, NewHistoryActivity.class);
        intent.putExtra(KEY_HISTORY, history);
        startActivityForResult(intent, 1);
    }
}
