package com.the_great_amoeba.mobster;

/**
 * Created by Ani Reddy on 4/13/17
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


public class ResultsCommentTabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;

    public static Fragment[] fragArray;

    public static int int_items = 2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate home_tab_layout and setup Views.

        this.fragArray = new Fragment[2];

        View x = inflater.inflate(R.layout.results_comments_tab_layout, null);

        tabLayout = (TabLayout) x.findViewById(R.id.results_tabs);
        viewPager = (ViewPager) x.findViewById(R.id.results_viewpager);

        //makes it so that the fragments are not created every time we switch a tab
        viewPager.setOffscreenPageLimit(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0:

                    case 1:

                    default:
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /**
         *Set an Apater for the View Pager
         */

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        // This is a workaround
        // The setupWithViewPager dose't works without the runnable
        // Maybe a Support Library Bug

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return x;

    }


    // Adapter to handle the tab fragments
    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        // Return fragment with respect to Position

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Fragment resultsFrag = new ResultsFragment();
                    resultsFrag.setArguments(getArguments());
                    fragArray[0] = resultsFrag;
                    return resultsFrag;
                case 1:
                    Fragment commentFrag = new CommentFragment();
                    commentFrag.setArguments(getArguments());
                    fragArray[1] = commentFrag;
                    return commentFrag;
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        // This method returns the title of the tab according to the position.


        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Results";
                case 1:
                    return "Comments";

            }
            return null;
        }
    }

}