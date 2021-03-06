package com.the_great_amoeba.mobster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Constants.Constant;

/**
 * Admin statistics class page.
 *
 * @author Natalie
 * @version 1.0
 */
public class AdminStatistics extends Activity {

    private DatabaseReference mDatabase;

    private static String users;
    private static String banned;
    private static String questions;
    private TextView usersText;
    private TextView bannedText;
    private TextView questionsText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(Constant.DB_URL);
        getNumbers(mDatabase);
        setContentView(R.layout.activity_admin_statistics);
    }


    /**
     *  Get the statistics based on the data in the database
     *
     * @param db reference to the database
     */
    private void getNumbers(DatabaseReference db) {
        DatabaseReference usersRef = db.child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = String.valueOf(dataSnapshot.getChildrenCount());
                usersText = (TextView)findViewById(R.id.num_users_col);
                usersText.setText(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference questionsRef = db.child("questions");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questions = String.valueOf(dataSnapshot.getChildrenCount());
                questionsText = (TextView)findViewById(R.id.num_questions_col);
                questionsText.setText(questions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference banRef = db.child("admin").child("banned");
        banRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                banned = String.valueOf(dataSnapshot.getChildrenCount());
                bannedText= (TextView)findViewById(R.id.num_banned_col);
                bannedText.setText(banned);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);
    }
}
