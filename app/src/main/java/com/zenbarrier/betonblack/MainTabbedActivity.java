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
    private int mCurrentPage = 0;
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
                mCurrentPage = position;
                mSectionsPagerAdapter.setFabListener(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if(getIntent().hasExtra(GameActivity.KEY_SAVED)){
            mViewPager.setCurrentItem(HISTORY_POSITION);
        }

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
                return myFragment;
            }else{
                myFragment = HistoryFragment.newInstance();
                mPageReferenceMap.put(position, myFragment);
                return myFragment;
            }
        }

        void setFabListener(int position){
            Fragment myFragment = mPageReferenceMap.get(position);
            if(myFragment == null) return;
            if(position == STRATEGY_LIST_POSITION){
                ((StrategyListFragment)myFragment).connectFab(mFab);
            }if(position == HISTORY_POSITION){
                ((HistoryFragment)myFragment).connectFab(mFab);
            }
        }

        /*Fragment getFragment(int key){
            if(mPageReferenceMap.get(key) == null){
                return getItem(key);
            }else return mPageReferenceMap.get(key);
        }*/

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mPageReferenceMap.put(position, fragment);
            if(position == mCurrentPage)setFabListener(position);
            return fragment;
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
                    return "Strategies";
                case HISTORY_POSITION:
                    return "History";
            }
            return null;
        }
    }
}
