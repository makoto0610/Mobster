package com.the_great_amoeba.mobster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;

import Constants.Constant;

/**
 * Created by singh on 2/22/2017.
 */

public class deleteFlaggedQuestions extends AppCompatActivity {
    private ListView listView ;
    private int length;
    private String[] array;
    private DatabaseReference mDatabase;
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban_user);
        getFlaggedQuestionsFirebase();

    }

    public void getFlaggedQuestionsFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
        Query contain = mDatabase.child("questions").orderByKey()
                .limitToFirst(Constant.NUM_OF_QUESTIONS);

        contain.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                length = 0;
                int index = 0;
                int childCount = (int)(dataSnapshot.getChildrenCount());
                array = new String[childCount];
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String keyQuestion = postSnapshot.getKey();
                    HashMap value = (HashMap) postSnapshot.getValue();
                    String question = (String) value.get("question");
                    if(value.get("isFlagged")!= null) {
                        Long isFlagged = (Long) value.get("isFlagged");
                        if (isFlagged == 1) {
                            array[index] = question;
                            length++;
                        }
                        index++;
                    }
                }
                init_FlaggedQuestions_Display();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
            }
        });


    }

    public void init_FlaggedQuestions_Display() {
        if(length == 0) {
            Toast.makeText(getApplicationContext(),
                    "No Flagged Questions", Toast.LENGTH_LONG)
                    .show();
        } else {

            // Get ListView object from xml
            listView = (ListView) findViewById(R.id.list);

            // Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data
            String[] flaggedArr = new String[length];
            for(int i = 0; i < length; i++){
                flaggedArr[i] = array[i];
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, flaggedArr);


            // Assign adapter to ListView
            listView.setAdapter(adapter);

            // ListView Item Click Listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition = position;

                    // ListView Clicked item value
                    String data = (String) listView.getItemAtPosition(position);
                    Intent intent = new Intent(view.getContext(), AdminDeleteFlagged.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("questionsToDelete", data);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }

            });
        }
    }

    public void  onBackButtonClickFlagged(View view) {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);
    }

}
