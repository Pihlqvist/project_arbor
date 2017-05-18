package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Environment;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.TextView;

import se.kth.projectarbor.project_arbor.tutorial.TutorialArbor;
import se.kth.projectarbor.project_arbor.view_objects.LoadingView;

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
    public Tree.Phase phase;
    private boolean hasAnimated;

    // Should be the golden pollen shown in game  //TODO: Fix this implementation (Fredrik)
    static int goldenPollen;

    // This receiver used by all fragments
    private class Receiver extends BroadcastReceiver {
        private ImageView loadScreen;
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            // DecimalFormat twoDForm = new DecimalFormat("#.0");

            // Msgs from MainService:tree data

            if (intent.getAction().equals(MainService.TREE_DATA)) {

                treeTab.newPhase = ((Tree.Phase) extras.get("PHASE")).getPhaseNumber();
                if (treeTab.newPhase != treeTab.currentPhase) {
                    if(treeTab.newPhase == 1){
                        treeTab.getAnimTree().startAnimation();
                    }
                    treeTab.getAnimTree().animatePhase(treeTab.newPhase, false);
                    treeTab.currentPhase = treeTab.newPhase;

                }

                if (extras.getInt("HP") < 1) {
                    statsTab.getHealthView().setText("DEAD");
                } else
                    statsTab.getHealthView().setText("" + extras.getInt("HP") + "hp");
                statsTab.getPhaseView().setText(((Tree.Phase) extras.get("PHASE")).getPhaseName());
                // TODO: Implement AGE when functionality is ready
                // Updates buffers
                statsTab.getWaterAnim().setLevel(extras.getInt("WATER") * 10);
                statsTab.getSunAnim().setLevel(extras.getInt("SUN") * 10);
                statsTab.getDistanceView().setText(String.format("%.2f", (extras.getDouble("TOTALKM")/1000)));
                totalDistance = extras.getDouble("TOTALKM");
                statsTab.getStepsView().setText(String.format("%d", (totalStepCount = extras.getInt("TOTALSTEPS"))));
                statsTab.getAgeView().setText("" + (ageMillis = extras.getLong("AGE", 0))/1000/60/60/24);

                // LAZAR OCH PATRIK ANVÄNDER DESSA
                statsTab.mDistance = extras.getDouble("TOTALKM")/1000;  // 1000 is BUFFER_CONSTANT in Pedometer
                statsTab.mStepCount = extras.getInt("TOTALSTEPS");
                statsTab.currentPhaseNumber = ((Tree.Phase) extras.get("PHASE")).getPhaseNumber();
                statsTab.statusImgUpd(statsTab.phaseIcon);
                // SLUT PÅ DERAS


            } else if (intent.getAction().equals(MainService.WEATHER_DATA)) {

                // Build new weatherLayout depending on weather
                // TODO: Make sure it dose not build new, if its the same weather
                se.kth.projectarbor.project_arbor.weather.Environment.Weather newWeather;
                newWeather = (se.kth.projectarbor.project_arbor.weather.Environment.Weather) extras.get("WEATHER");
                if (!treeTab.getWeather().equals(newWeather)) {
                    treeTab.setWeather(newWeather);
                    RelativeLayout layout = (RelativeLayout) treeTab.getTabView();
                    layout.removeView(treeTab.getWeatherLayout());
                    treeTab.setWeatherLayout();
                    layout.addView(treeTab.getWeatherLayout());
                    treeTab.setTabView(layout);
                }
                Double newTemp = extras.getDouble("TEMP");
                if (newTemp.equals(Double.NaN)) {
                    treeTab.getTempView().setText("N/A ");
                } else {
                    treeTab.getTempView().setText(String.format("%.1f °C", newTemp));
                }


            // Msgs from Pedometer

            } else if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
                treeTab.getStepView().setText(String.format("%d", extras.getInt("STEPCOUNT")));
                treeTab.getDistanceView().setText(String.format("%.2f km",extras.getDouble("DISTANCE")/1000));

                statsTab.getDistanceView().setText(String.format("%.2f", (extras.getDouble("TOTALDISTANCE")/1000)));
                totalDistance = extras.getDouble("TOTALDISTANCE");
                statsTab.getStepsView().setText(String.format("%d", (totalStepCount =extras.getInt("TOTALSTEPCOUNT"))));


            } else if (intent.getAction().equals(Pedometer.STORE_BROADCAST)) {
                goldenPollen = shopTab.addMoney(intent.getIntExtra("MONEY", 0));
                shopTab.getGoldenPollenView().setText(goldenPollen + " gp");
                treeTab.getTvPollen().setText("" + goldenPollen);
                sharedPreferences.edit().putInt("STORE_MONEY", goldenPollen).apply();

            } else if (intent.getAction().equals(MainService.TREE_DEAD)) {
                setDeathView();
            }
        }
    }

    StatsTab statsTab;
    public TreeTab treeTab;
    ShopTab shopTab;
    SharedPreferences sharedPreferences;

    private int totalStepCount;
    private double totalDistance;
    private long ageMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
        hasAnimated = false;


        // Used for handling golden pollens and boolean "alive"
        sharedPreferences = getSharedPreferences("se.kth.projectarbor.project_arbor", Context.MODE_PRIVATE);

        Button btnContinue = (Button) findViewById(R.id.btn_continue_new);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putBoolean("FIRST_TREE", false).putBoolean("TREE_ALIVE", true).apply();
                Intent intent = new Intent(MainUIActivity.this, NewTreeActivity.class);
                MainUIActivity.this.startActivity(intent);
                MainUIActivity.this.finish();
            }
        });

        boolean alive = sharedPreferences.getBoolean("TREE_ALIVE", true);
        // For TESTING
        // alive = false;

        if (!alive) {
            totalDistance = getIntent().getDoubleExtra("TOTALKM", 0);
            totalStepCount = getIntent().getIntExtra("TOTALSTEPS", 0);
            ageMillis = getIntent().getLongExtra("AGE", 0);
            setDeathView();
        } else {

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
                    switch (position) {
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

            // If money has been stored earlier, read from sharedPreferences
            if (sharedPreferences.contains("STORE_MONEY")) {
                goldenPollen = sharedPreferences.getInt("STORE_MONEY", 0);
            } else {
                goldenPollen = 10;
                sharedPreferences.edit().putInt("STORE_MONEY", goldenPollen).apply();
            }


            /// HANDLES GOLDEN POLLEN END

            IntentFilter filter = new IntentFilter();
            filter.addAction(Pedometer.DISTANCE_BROADCAST);
            filter.addAction(Pedometer.STORE_BROADCAST);
            filter.addAction(MainService.TREE_DATA);
            filter.addAction(MainService.WEATHER_DATA);
            filter.addAction(MainService.TREE_DEAD);
            registerReceiver(this.new Receiver(), filter);

            // TODO: Add elements to the settings_main_settings layout
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (snackbarSemaphore) {
                        if (snackbar.isShown()) {
                            snackbar.dismiss();
                        } else {
                            snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
                            Snackbar.SnackbarLayout mSnacks = (Snackbar.SnackbarLayout) snackbar.getView();
                            mSnacks.addView(getLayoutInflater().inflate(R.layout.settings_main_settings, null));
                            snackbar.removeCallback(null);
                            snackbar.show();
                        }
                    } else {
                        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
                        Snackbar.SnackbarLayout mSnacks = (Snackbar.SnackbarLayout) snackbar.getView();
                        mSnacks.addView(getLayoutInflater().inflate(R.layout.settings_main_settings, null));
                        snackbar.show();
                        snackbarSemaphore = true;
                    }
                }
            });
        }
    }

    public void goToGenderHeight(View v) {
        Intent genderHeighIntent = new Intent(this,GenderHeightActivity.class);
        startActivity(genderHeighIntent);

    }

    public void goToHowToPlay(View v){
        Intent tutorial= new Intent(this,TutorialArbor.class);
        startActivity(tutorial);
    }
    public void goToAbout(View v){
        Intent aboutIntent = new Intent(this,AboutActivity.class);
        startActivity(aboutIntent);

    }



    @Override
    protected void onResume() {
        super.onResume();
        boolean alive = sharedPreferences.getBoolean("TREE_ALIVE", true);
        // For TESTING
        // alive = false;

        if (!alive) {
            setDeathView();
        }
    }

    private void setDeathView() {
        ((TextView) findViewById(R.id.tv_age)).setText(ageMillis/1000/60/60/24 + "");
        ((TextView) findViewById(R.id.tv_total_steps)).setText(totalStepCount + "");
        ((TextView) findViewById(R.id.tv_total_distance)).setText(String.format("%.2f", totalDistance/1000));

        findViewById(R.id.tree_death_view).setVisibility(View.VISIBLE);
        findViewById(R.id.appbar).setVisibility(View.GONE);
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
    public void soundZERO(View v){}
    public void soundFIFTY(View v){}
    public void soundHUNDRED(View v){}
    @Override
    public void onBackPressed() {}


}
