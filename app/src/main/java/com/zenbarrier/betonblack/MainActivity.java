package com.zenbarrier.betonblack;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    public static final int REQUEST_NEW_STRATEGY = 1;
    public static final int REQUEST_EDIT_STRATEGY = 2;
    public static final String KEY_STRATEGY = "strategy";
    public static final String KEY_POSITION = "position";

    RecyclerView mRecyclerView;
    MyMainAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    List<Strategy> mStrategyList;
    StrategyDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getApplicationContext(),NewStrategyActivity.class);
                startActivityForResult(intent, REQUEST_NEW_STRATEGY);
            }
        });

        initValues();
        initSwipe();

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_NEW_STRATEGY:
                if(resultCode == Activity.RESULT_OK){
                    Strategy strategy = (Strategy) data.getSerializableExtra(KEY_STRATEGY);
                    mStrategyList.add(strategy);
                }
                break;
            case REQUEST_EDIT_STRATEGY:
                if(resultCode == Activity.RESULT_OK){
                    int position = data.getIntExtra(KEY_POSITION,0);
                    Strategy strategy = (Strategy) data.getSerializableExtra(KEY_STRATEGY);
                    mStrategyList.set(position, strategy);
                }
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    private class DatabaseLoader extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = {
                    StrategyContract.StrategyEntry._ID,
                    StrategyContract.StrategyEntry.COLUMN_STRATEGY_NAME,
                    StrategyContract.StrategyEntry.COLUMN_MIN_BET,
                    StrategyContract.StrategyEntry.COLUMN_MAX_BET,
                    StrategyContract.StrategyEntry.COLUMN_STRATEGY_CHOICE,
            };
            String sortOrder = StrategyContract.StrategyEntry.COLUMN_STRATEGY_NAME + " DESC";
            Cursor cursor = db.query(
                    StrategyContract.StrategyEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
                    );

            while(cursor.moveToNext()){
                long itemId = cursor.getLong(cursor.getColumnIndex(StrategyContract.StrategyEntry._ID));
                String itemName = cursor.getString(cursor.getColumnIndex(StrategyContract.StrategyEntry.COLUMN_STRATEGY_NAME));
                int itemMin = cursor.getInt(cursor.getColumnIndex(StrategyContract.StrategyEntry.COLUMN_MIN_BET));
                int itemMax = cursor.getInt(cursor.getColumnIndex(StrategyContract.StrategyEntry.COLUMN_MAX_BET));
                int itemChoice = cursor.getInt(cursor.getColumnIndex(StrategyContract.StrategyEntry.COLUMN_STRATEGY_CHOICE));
                Strategy strategy = new Strategy(itemId, itemName, itemMin, itemMax, itemChoice);
                mStrategyList.add(strategy);
            }
            cursor.close();
            db.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void initValues(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recylerView_main_list);
        mStrategyList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyMainAdapter(mStrategyList);
        mRecyclerView.setAdapter(mAdapter);
        mDbHelper = new StrategyDbHelper(this);

        DatabaseLoader databaseLoader = new DatabaseLoader();
        databaseLoader.execute();
    }

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    deleteStrategy(position);
                } else {
                    editStrategy(position);
                    //removeView();
                    //edit_position = position;
                    //alertDialog.setTitle("Edit Country");
                    //et_country.setText(countries.get(position));
                    //alertDialog.show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    Paint p = new Paint();
                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else if (dX < 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else{
                        c.drawColor(Color.TRANSPARENT);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void editStrategy(int position) {
        Intent intent = new Intent( getApplicationContext(),NewStrategyActivity.class);
        Strategy strategy = mStrategyList.get(position);
        intent.putExtra(KEY_STRATEGY, strategy);
        intent.putExtra(KEY_POSITION, position);
        startActivityForResult(intent, REQUEST_EDIT_STRATEGY);
    }

    private void deleteStrategy(int position) {
        long id = mStrategyList.get(position)._id;
        mAdapter.removeItem(position);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = StrategyContract.StrategyEntry._ID+"=?";
        String[] selectArgs = {String.valueOf(id)};
        db.delete(StrategyContract.StrategyEntry.TABLE_NAME, selection, selectArgs);
        Snackbar.make(findViewById(R.id.recylerView_main_list), "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        db.close();
    }

}
