package se.kth.projectarbor.project_arbor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SwipeDismissBehavior;
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
import android.view.KeyEvent;

import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

public class MainUIActivity extends AppCompatActivity {
    Log log;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private FloatingActionButton fab;
    // Creates new tree if true
    SharedPreferences sharedPreferences = null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Snackbar snackbar;
    private boolean start = true;

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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!start) {
                    if (snackbar.isShown()) {
                        snackbar.dismiss();
                        fab.hide();
                    } else {
                        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
                        Snackbar.SnackbarLayout mSnacks = (Snackbar.SnackbarLayout) snackbar.getView();
                        mSnacks.addView(getLayoutInflater().inflate(R.layout.settings, null));
                        snackbar.removeCallback(null);
                        snackbar.show();
                    }
                }else{
                    snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
                    Snackbar.SnackbarLayout mSnacks = (Snackbar.SnackbarLayout) snackbar.getView();
             //       ViewGroup.LayoutParams lp = mSnacks.getLayoutParams();
              //      if (lp instanceof CoordinatorLayout.LayoutParams) {
               //         ((CoordinatorLayout.LayoutParams) lp).setBehavior(new DisableSwipeBehavior());
                //        mSnacks.setLayoutParams(lp);
                    mSnacks.addView(getLayoutInflater().inflate(R.layout.settings, null));
                    snackbar.show();
                    start = false;
                }
            }
        });
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
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Toast.makeText(getApplicationContext(), "There is a return", Toast.LENGTH_LONG).show();
            fab.show();
            fab.callOnClick();
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "There is no return", Toast.LENGTH_LONG).show();
    }
    public class DisableSwipeBehavior extends SwipeDismissBehavior<Snackbar.SnackbarLayout> {
        @Override
        public boolean canSwipeDismissView(View view) {
            return false;
        }
    }
}
