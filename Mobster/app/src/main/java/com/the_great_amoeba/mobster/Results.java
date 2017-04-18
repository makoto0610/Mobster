package com.the_great_amoeba.mobster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import Helper.HelperMethods;

/**
 * Result page for questions.
 *
 * @author Natalie
 * @version 1.0
 */
public class Results extends AppCompatActivity{

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperMethods.setChosenTheme(this, getApplicationContext());

        setContentView(R.layout.activity_results);
        Bundle bundle = getIntent().getExtras();

        //Inflating the HomeFragment as the first Fragment
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();


        ResultsCommentTabFragment rcTab = new ResultsCommentTabFragment();
        rcTab.setArguments(bundle);
        mFragmentTransaction.replace(R.id.results_containerView, rcTab).commit();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
