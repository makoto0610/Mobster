package com.the_great_amoeba.mobster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import Constants.Constant;
import Objects.BannedUser;

/**
 * Admin banning user activity.
 *
 * @author Esha
 * @version 1.0
 */
public class Ban extends AppCompatActivity {
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;
    private String userToBanPassed;
    private String[] questionsToDelete;
    private int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_ban);
        Bundle bundle = getIntent().getExtras();
        userToBanPassed = bundle.getString("userToBan");
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
        TextView myTextView = (TextView)findViewById(R.id.textView);
        myTextView.setText("Are you sure you want to ban user "+ userToBanPassed + "?");
    }

    /**
     * If ban button is pressed delete ban the user and delete the questions from the database
     *
     * @param view view that is on
     */
    public void onBanButtonClick(View view) {
        BannedUser user = new BannedUser(userToBanPassed);
        mDatabase.child("admin").child("banned").child(userToBanPassed).setValue(user);
        mDatabase.child("users").child(userToBanPassed).removeValue();
        Query contain = mDatabase.child("questions").orderByKey()
                .limitToFirst(Constant.NUM_OF_QUESTIONS);

        contain.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0;
                length = 0;
                int childCount = (int)(dataSnapshot.getChildrenCount());
                questionsToDelete = new String[childCount];
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String keyQuestion = postSnapshot.getKey();
                    HashMap value = (HashMap) postSnapshot.getValue();
                    String username = (String) value.get("username");
                    if (userToBanPassed.equals(username)) {
                        questionsToDelete[index] = keyQuestion;
                        length++;
                    }
                }
                userBanned();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
            }
        });
    }

    /**
     * Start a toast and a new activity after banning user
     */
    private void userBanned(){
        for (int i = 0; i < length; i++) {
            mDatabase.child("questions").child(questionsToDelete[i]).setValue(null);
        }
        Toast.makeText(getApplicationContext(),
                userToBanPassed+ " Banned!" , Toast.LENGTH_LONG)
                .show();
        Intent intent = new Intent(this, BanUser.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);
    }

}

