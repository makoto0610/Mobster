package com.the_great_amoeba.mobster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.Duration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import Constants.Constant;
import Objects.Adapters.CustomListViewAdapter;
import Objects.DisplayQuestion;

/**
 * Created by singh on 2/22/2017.
 */

public class BanUser extends Activity {
    private ListView listView ;
    private String[] array;
    private DatabaseReference mDatabase;
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban_user);
        getUsersFirebase();

    }

    public void getUsersFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
        Query contain = mDatabase.child("users");

        contain.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0;
                int childCount = (int)(dataSnapshot.getChildrenCount());
                array = new String[childCount];
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    array[index] = (String)d.child("username").getValue();
                    index++;
                }
                init_Users_Display();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
            }
        });

    }

    public void init_Users_Display() {

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, array);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  data    = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), Ban.class);
                Bundle bundle = new Bundle();
                bundle.putString("userToBan", data);
                intent.putExtras(bundle);
                startActivity(intent);

            }

        });
    }

    public void onLogoutButtonClick(View view) {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);
    }

}
