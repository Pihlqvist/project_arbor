package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.media.SoundPool;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

public class MainUIActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    //make a private SoundHandler despite there is a method to get a copy of the reference
    private SoundHandler sh;
    private boolean snackbarSemaphore = false;
    private Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);

        //Setup a SoundHandler to load Sounds this is time critcal operation
        sh = new SoundHandler(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        // TODO: Add elements to the settings_main_settings layout
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(snackbarSemaphore) {
                   if (snackbar.isShown()) {
                       snackbar.dismiss();
                   }else {
                       snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
                       Snackbar.SnackbarLayout mSnacks = (Snackbar.SnackbarLayout) snackbar.getView();
                       mSnacks.addView(getLayoutInflater().inflate(R.layout.settings_main_settings, null));
                       snackbar.removeCallback(null);
                       snackbar.show();
                   }
               }else{
                   snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
                   Snackbar.SnackbarLayout mSnacks = (Snackbar.SnackbarLayout) snackbar.getView();
                   mSnacks.addView(getLayoutInflater().inflate(R.layout.settings_main_settings, null));
                   snackbar.show();
                   snackbarSemaphore = true;
               }
            }
        });

    }
    protected void onResume() {
        super.onResume();

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return new TreeTab();
                case 1:
                    return new StatsTab();
                case 2:
                    return new ShopTab();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "TREE";
                case 1:
                    return "STATS";
                case 2:
                    return "SHOP";
            }
            return null;
        }


    }
    public class SoundHandler{
        SoundPool pp;
        int shopWater;
        int shopSun;
        int soundVolume;
        SoundHandler(Context context) {
            shopSun = pp.load(context, R.raw.shopsun_3, 1);
            shopWater = pp.load(context, R.raw.shopwater_2, 1);
        }
        public SoundPool getSoundPoolRef(){
            return this.pp;
        }
        public void playShopWater(){
            pp.play(shopWater, soundVolume, soundVolume, 1, 0, 1);
        }
        public void playShopSun(){
            pp.play(shopSun, soundVolume, soundVolume, 1, 0, 1);
        }
        public void playShopWater(int vol){
            pp.play(shopWater, vol, vol, 1, 0, 1);
        }
        public void playShopSun(int vol){
            pp.play(shopSun, vol, vol, 1, 0, 1);
        }
        public void pause(){
            pp.autoPause();
        }
        public void resume(){
            pp.autoResume();
        }
        public void release (){
            pp.release();
        }
    }
    public SoundHandler getSoundHandler (){
        return sh;
    }
    @Override
    public void onBackPressed() {}


}
