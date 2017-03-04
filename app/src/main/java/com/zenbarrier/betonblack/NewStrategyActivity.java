package com.zenbarrier.betonblack;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewStrategyActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private EditText mStartingMoney;
    private EditText mMinBet, mMaxBet;
    private Spinner mStrategyChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_strategy);

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        mStartingMoney = (EditText) findViewById(R.id.editText_newStrategy_startingMoney);
        mMinBet = (EditText) findViewById(R.id.editText_newStrategy_minimumBet);
        mMaxBet = (EditText) findViewById(R.id.editText_newStrategy_maximumBet);
        mStrategyChoice = (Spinner) findViewById(R.id.spinner_newStrategy_strategyChoice);

        //TODO Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();
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
        SQLiteDatabase db = this.openOrCreateDatabase("strategies", MODE_PRIVATE, null);
        int startingMoney = Integer.parseInt(mStartingMoney.getText().toString());
        int minBet = Integer.parseInt(mMinBet.getText().toString());
        int maxBet = Integer.parseInt(mMaxBet.getText().toString());
        int strategyChoice = mStrategyChoice.getSelectedItemPosition();

        db.execSQL("CREATE TABLE IF NOT EXISTS strategies " +
                "(starting_money INT, " +
                "min_bet INT, " +
                "max_bet INT, " +
                "strategy_choice INT, " +
                "id INTEGER PRIMARY KEY)");

        ContentValues values = new ContentValues();
        values.put("starting_money", startingMoney);
        values.put("min_bet", minBet);
        values.put("max_bet", maxBet);
        values.put("strategy_choice", strategyChoice);
        db.insert("strategies", null, values);
    }
}
