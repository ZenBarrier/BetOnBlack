package com.zenbarrier.betonblack;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainTabbedActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private final int STRATEGY_LIST_POSITION = 0;
    private final int HISTORY_POSITION = 1;
    private AdView mAdView;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /*
      The {@link ViewPager} that will host the section contents.
     */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(positionOffset>0.0) {
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int width = displayMetrics.widthPixels - mFab.getWidth();
                    int margin = ((ViewGroup.MarginLayoutParams)mFab.getLayoutParams()).rightMargin<<1;
                    mFab.setTranslationX(-positionOffset*(width-margin)*0.5f);
                    mFab.setRotation(-360*positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case STRATEGY_LIST_POSITION:
                        ((StrategyListFragment)mSectionsPagerAdapter.getFragment(STRATEGY_LIST_POSITION)).connectFab(mFab);
                        break;
                    case HISTORY_POSITION:
                        ((HistoryFragment)mSectionsPagerAdapter.getFragment(HISTORY_POSITION)).connectFab(mFab);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Load an ad into the AdMob banner view.
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mAdView.loadAd(adRequest);

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
        getMenuInflater().inflate(R.menu.menu_main_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SparseArrayCompat<Fragment> mPageReferenceMap;
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mPageReferenceMap = new SparseArrayCompat<>();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment;
            if(position == STRATEGY_LIST_POSITION){
                myFragment = StrategyListFragment.newInstance();
                mPageReferenceMap.put(position, myFragment);
                ((StrategyListFragment)myFragment).connectFab(mFab);
                return myFragment;
            }else{
                myFragment = HistoryFragment.newInstance();
                mPageReferenceMap.put(position, myFragment);
                return myFragment;
            }
        }

        Fragment getFragment(int key){
            if(mPageReferenceMap.get(key) == null){
                return getItem(key);
            }else return mPageReferenceMap.get(key);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case STRATEGY_LIST_POSITION:
                    return "SECTION 1";
                case HISTORY_POSITION:
                    return "SECTION 2";
            }
            return null;
        }
    }
}
