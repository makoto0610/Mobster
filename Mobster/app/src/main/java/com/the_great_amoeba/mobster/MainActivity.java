package com.the_great_amoeba.mobster;

import android.app.AlertDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;

import Helper.HelperMethods;

/**
 * Main activity for the application.
 *
 * @author Christine
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    private String searchedText;
    private int searchedArea;
    public boolean searching;
    private boolean searchingKeyword;
    private String[] keywords;

    public static boolean homeFragmentShown=false ;
    public static boolean otherFragmentShown=false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperMethods.setChosenTheme(this, getApplicationContext());
        setContentView(R.layout.activity_main);

        // Setup the DrawerLayout and NavigationView
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        //Inflating the HomeFragment as the first Fragment
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        homeFragmentShown=true;

        mFragmentTransaction.replace(R.id.containerView, new HomeTabFragment()).commit();

        //Setup click events on the Navigation View Items
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_search_questions) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Search");

                    // text inputs
                    final EditText input = new EditText(MainActivity.this);
                    if (searching) {
                        input.setText(searchedText);
                    } else {
                        input.setHint("Enter text here");
                    }
                    input.setInputType(InputType.TYPE_CLASS_TEXT);

                    // adding margins to the input line
                    FrameLayout container = new FrameLayout(MainActivity.this);
                    FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams
                                    (ViewGroup.LayoutParams.MATCH_PARENT
                                    , ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                    params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                    input.setLayoutParams(params);
                    container.addView(input);
                    builder.setView(container);

                    //builder.setView(input);

                    // radio buttons
                    final String[] choices = {"search my questions", "search all"};
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

                            searching = true;
                            searchedText = input.getText().toString().toLowerCase();
                            FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                            if (searchedArea == 0) {
                                Bundle b = new Bundle();
                                b.putBoolean("search", true);
                                MyQuestionsTabFragment myqtbFrag = new MyQuestionsTabFragment();
                                myqtbFrag.setArguments(b);
                                xfragmentTransaction.replace(R.id.containerView, myqtbFrag).commit();

                            } else {
                                Bundle b = new Bundle();
                                b.putBoolean("search", true);
                                HomeTabFragment homeFrag = new HomeTabFragment();
                                homeFrag.setArguments(b);
                                xfragmentTransaction.replace(R.id.containerView, homeFrag).commit();
                            }
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
                    otherFragmentShown=true;
                    homeFragmentShown=false;
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new MyQuestionsTabFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_statistics) {
                    otherFragmentShown=true;   //when moving to fragment1
                    homeFragmentShown=false;
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new StatisticsFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_rules) {
                    otherFragmentShown=true;   //when moving to fragment1
                    homeFragmentShown=false;
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new RulesFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    //Log out from Firebase Auth
                    FirebaseAuth.getInstance().signOut();
                    SaveSharedPreferences.setUserName(getApplicationContext(), "");
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.nav_item_Location) {
                    // start Google Maps activity
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    otherFragmentShown=true;   //when moving to fragment1
                    homeFragmentShown=false;
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                }


                return false;
            }

        });

        // Setup Drawer Toggle of the Toolbar
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

    /**
     * Determine whether it is searching or not.
     *
     * @return true if it is searching, otherwise false
     */
    public boolean isSearching() {
        return searching;
    }

    /**
     * Determine whether it is searching keyword or not.
     *
     * @return true if it is searching keywords, otherwise false
     */
    public boolean isSearchingKeyword() {
        return searchingKeyword;
    }

    /**
     * Getter for searched texts.
     *
     * @return searched texts
     */
    public String getSearchedText() {
        return searchedText;
    }

    /**
     * Get list of keywords.
     *
     * @return list of keywords
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * 1 = all questions
     * 0 = my questions
     *
     * @return 1 or 0 value
     */
    public int getSearchedArea() {
        return searchedArea;
    }

    @Override
    public void onBackPressed() {
            if(homeFragmentShown) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage("Do you want to Exit?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                homeFragmentShown=true;
                FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                xfragmentTransaction.replace(R.id.containerView, new HomeTabFragment()).commit();
            }
    }
}