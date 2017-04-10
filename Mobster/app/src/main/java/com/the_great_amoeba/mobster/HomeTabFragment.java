package com.the_great_amoeba.mobster;

/**
 * Created by C. Shih on 12/23/2016.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import Constants.Constant;
import Helper.Log;

import static android.R.attr.progress;
import static android.R.attr.tag;

public class HomeTabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;

    public static int int_items = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate home_tab_layout and setup Views.
         */
        View x = inflater.inflate(R.layout.home_tab_layout, null);

        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        //makes it so that the fragments are not created every time we switch a tab
        viewPager.setOffscreenPageLimit(2);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return x;

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Log.d(Constant.DEBUG, "Case 0");
                    return new NewFragment();
                case 1:
                    Log.d(Constant.DEBUG, "Case 1");
                    return new TrendingFragment();
                case 2:
                    Log.d(Constant.DEBUG, "Case 2");
                    return new ClosedFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "New";
                case 1:
                    return "Trending";
                case 2:
                    return "Closed";
            }
            return null;
        }
    }

}