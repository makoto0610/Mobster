package com.the_great_amoeba.mobster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Objects.User;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    String searchedText;
    int searchedArea;
    boolean searching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Setup the DrawerLayout and NavigationView
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        /**
         * Inflating the HomeFragment as the first Fragment
         */
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new HomeTabFragment()).commit();

        /**
         * Setup click events on the Navigation View Items.
         */
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_search_questions) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Search");

                    // text inpus
                    final EditText input = new EditText(MainActivity.this);
                    if (searching) {
                        input.setText(searchedText);
                    }
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // radio buttons
                    final String[] choices = {"My Questions", "All Questions"};
                    builder.setSingleChoiceItems(choices, searchedArea, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            searchedArea = which;
                        }
                    });

                    // search and cancel buttons
                    builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            searchedText = input.getText().toString();
                            if (searchedText.equals("")) {
                                searching = false;
                            } else {
                                searching = true;
                            }
                            System.out.println("SEARCHING: " + searching);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setIcon(R.drawable.places_ic_search);
                    builder.show();
                }

                if (menuItem.getItemId() == R.id.nav_item_home) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new HomeTabFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_my_questions) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new MyQuestionsTabFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_statistics) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new StatisticsFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_rules) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new RulesFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    //Log out from Firebase Auth
                    FirebaseAuth.getInstance().signOut();
                    SaveSharedPreferences.setUserName(getApplicationContext(),"");
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.nav_item_Location) {
                    // start Google Maps activity
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.edit) {
            Intent intent = new Intent(this, CreateQuestion.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}