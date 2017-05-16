package se.kth.projectarbor.project_arbor.tutorial;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import se.kth.projectarbor.project_arbor.R;

/*
    Created by Lazar Cerovic and Johan Andersson on 2017-05-16.
 */
public class TutorialArbor extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    TutorialOne tutorialOne;
    TutorialTwo tutorialTwo;
    TutorialThree tutorialThree;
    TutorialFour tutorialFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_arbor);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

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
            switch (position){
                case 0:
                    return TutorialArbor.this.tutorialOne = new TutorialOne();
                case 1:
                    return TutorialArbor.this.tutorialTwo = new TutorialTwo();
                case 2:
                    return TutorialArbor.this.tutorialThree = new TutorialThree();
                case 3:
                    return TutorialArbor.this.tutorialFour = new TutorialFour();

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Tutorial 1";
                case 1:
                    return "Tutorial 2";
                case 2:
                    return "Tutorial 3";
                case 3:
                    return "Tutorial 4";
            }
            return null;
        }

    }
    public void next_fragment(View view){
        mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
    }
    public void previous_fragment(View view){
        mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
    }
}
