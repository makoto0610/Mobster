package com.the_great_amoeba.mobster;

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

import Constants.Constant;
import Helper.Log;

/**
 * My questions tab to display users' questions.
 *
 * @author Christine
 * @version 1.0
 */
public class MyQuestionsTabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle b = getArguments();
        if (b != null) {
            boolean searchStatus = b.getBoolean("search");
            try {
                ((MainActivity) getActivity()).searching = searchStatus;
            } catch (Exception e){
                Helper.Log.d(Constant.DEBUG, e.getLocalizedMessage());
            }
        } else {
            try {
                ((MainActivity) getActivity()).searching = false;
            } catch (Exception e){
                Helper.Log.d(Constant.DEBUG, e.getLocalizedMessage());
            }
        }

        // Inflate home_tab_layout and setup Views
        View x = inflater.inflate(R.layout.home_tab_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        // Set an Adapter for the View Pages
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(1);

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

        // Return fragment with respect to Position
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new NewFragment();
                case 1:
                    return new ClosedFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        // This method returns the title of the tab according to the position
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "New";
                case 1:
                    return "Closed";
            }
            return null;
        }
    }
}