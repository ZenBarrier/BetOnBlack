package com.zenbarrier.betonblack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewHistoryActivity extends AppCompatActivity {
    EditText mTextName;
    EditText mTextStartCash;
    EditText mTextEndCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_history);
        setFinishOnTouchOutside(false);

        mTextName = (EditText) findViewById(R.id.editText_newHistory_name);
        mTextStartCash = (EditText) findViewById(R.id.editText_newHistory_startCash);
        mTextEndCash = (EditText) findViewById(R.id.editText_newHistory_endCash);
    }

    public void saveHistory(View view) {
        String name = mTextName.getText().toString();
        int startCash, endCash;
        try {
            startCash = Integer.parseInt(mTextStartCash.getText().toString());
            endCash = Integer.parseInt(mTextEndCash.getText().toString());
        } catch(NumberFormatException e){
            if(mTextStartCash.getText().length() == 0){
                mTextStartCash.setError(getString(R.string.error_empty_number));
            }if(mTextEndCash.getText().length() == 0){
                mTextEndCash.setError(getString(R.string.error_empty_number));
            }
            findViewById(R.id.constraintLayout_newHistory_root).startAnimation(AnimationUtils.loadAnimation(this, R.anim.vibrate));
            return;
        }

        History history = new History(name, startCash, endCash, true);

        StrategyDbHelper dbHelper = new StrategyDbHelper(this);
        history._id = dbHelper.add(history);
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        dt.setTimeZone(TimeZone.getTimeZone("UTC"));
        history.date = dt.format(new Date());

        Intent data = new Intent();
        data.putExtra(HistoryFragment.KEY_HISTORY ,history);
        setResult(AppCompatActivity.RESULT_OK, data);
        finish();

    }

    public void cancelHistory(View view) {
        setResult(AppCompatActivity.RESULT_CANCELED);
        finish();
    }
}
