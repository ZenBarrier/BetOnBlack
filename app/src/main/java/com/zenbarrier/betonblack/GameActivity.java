package com.zenbarrier.betonblack;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

public class GameActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private Strategy mStrategy;
    private ViewAnimator mAnimator;
    private TextView mTextCash, mTextMin, mTextMax, mTextName, mTextOdds, mTextRounds, mTextStrategyMode;
    private NumberPicker mPickerBet;
    private int mCash, mMin, mMax, mRounds, mBet;
    private double mOdds;
    private String mName, mStrategyMode;

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
        }else{
            mCash = Integer.parseInt(cashString);
            initializeInfo();
            setInfo();
            mAnimator.showNext();
        }
    }

    private void initializeInfo() {
        mTextCash = (TextView) findViewById(R.id.textView_game_cash);
        mTextMin = (TextView) findViewById(R.id.textView_game_min);
        mTextMax = (TextView) findViewById(R.id.textView_game_max);
        mTextName = (TextView) findViewById(R.id.textView_game_name);
        mTextOdds = (TextView) findViewById(R.id.textView_game_odds);
        mTextRounds = (TextView) findViewById(R.id.textView_game_rounds);
        mTextStrategyMode = (TextView) findViewById(R.id.textView_game_strategyMode);
        mPickerBet = (NumberPicker) findViewById(R.id.numberPicker_game_bettingAmount);

        mMin = mStrategy.minBet;
        mMax = mStrategy.maxBet;
        String[] modes = getResources().getStringArray(R.array.strategy_array);
        mStrategyMode = modes[mStrategy.strategyChoice];
        mName = mStrategy.name;

        mTextName.setText(mName);
        mTextMin.setText(String.valueOf(mMin));
        mTextMax.setText(String.valueOf(mMax));
        mTextStrategyMode.setText(mStrategyMode);

        mPickerBet.setMinValue(mMin);
    }

    private void setInfo() {
        mTextCash.setText(String.valueOf(mCash));
        mTextOdds.setText(String.valueOf(mOdds));
        mTextRounds.setText(String.valueOf(mRounds));

        mPickerBet.setMaxValue(Math.min(mCash, mMax));
        mPickerBet.setValue(Math.min(mBet, mMax));
    }

    public void wonBet(View view){

    }

    public void lostBet(View view){

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
