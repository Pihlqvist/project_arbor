package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
    private boolean snackbarSemaphore = false;
    private Snackbar snackbar;

    // This receiver used by all fragments
    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            // DecimalFormat twoDForm = new DecimalFormat("#.0");
            Log.d("ARBOR", "onReceive()");

            // Msgs from MainService:tree data

            if (intent.getAction().equals(MainService.TREE_DATA)) {
                Log.d("HEALTH","HEALTH");
                statsTab.getDistanceView().setText(String.format("Distance: %.2f", (extras.getDouble("TOTALKM")/1000)));
                statsTab.getStepsView().setText("" + extras.getInt("TOTALSTEPS") + " steps");
                if (extras.getInt("HP") < 1) {
                    statsTab.getHealthView().setText("DEAD");
                } else
                    statsTab.getHealthView().setText("" + extras.getInt("HP") + "hp");
                statsTab.getPhaseView().setText(extras.getString("PHASE"));

                // TODO: Implement AGE when functionality is ready
                // Updates buffers
                statsTab.getWaterAnim().setLevel(extras.getInt("WATER") * 10);
                statsTab.getSunAnim().setLevel(extras.getInt("SUN") * 10);
            } else if (intent.getAction().equals("WEATHER_DATA")) {
                // Build new weatherLayout depending on weather
                treeTab.setWeather((Environment.Weather) extras.get("WEATHER"));
                RelativeLayout layout = (RelativeLayout) treeTab.getTabView();
                layout.removeView(treeTab.getWeatherLayout());
                treeTab.setWeatherLayout();
                layout.addView(treeTab.getWeatherLayout());
                treeTab.setTabView(layout);

            // Msgs from Pedometer

            } else if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
                treeTab.setDistance(extras.getDouble("DISTANCE"));
                treeTab.setSteps(extras.getInt("STEPCOUNT"));
                treeTab.getDistanceView().setText(String.format("Distance: %.2f",extras.getDouble("DISTANCE")));
            } else if (intent.getAction().equals(Pedometer.STORE_BROADCAST)) {
                int money = shopTab.addMoney(intent.getIntExtra("MONEY", 0));
                shopTab.getTextMoney().setText("Curreny: " + money);

                sharedPreferences.edit().putInt("STORE_MONEY", money).apply();
            }
        }
    }

    StatsTab statsTab;
    TreeTab treeTab;
    ShopTab shopTab;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        // TODO: Trigger animations in StatsTab and TreeTab with the help of addOnPageChangeListener

        // Adds action when switching to certain tab. May be used to trigger animations.
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        sharedPreferences = getSharedPreferences("se.kth.projectarbor.project_arbor", Context.MODE_PRIVATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Pedometer.DISTANCE_BROADCAST);
        filter.addAction(Pedometer.STORE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        registerReceiver(this.new Receiver(), filter);

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
                    return MainUIActivity.this.treeTab = new TreeTab();
                case 1:
                    return MainUIActivity.this.statsTab = new StatsTab();
                case 2:
                    return MainUIActivity.this.shopTab = new ShopTab();
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

    @Override
    public void onBackPressed() {}


}
