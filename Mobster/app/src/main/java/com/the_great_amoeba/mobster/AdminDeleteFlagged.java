package com.the_great_amoeba.mobster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Admin functionality for deleting flagged questions
 *
 * @author Esha
 * @version 1.0
 */
public class AdminDeleteFlagged extends Activity{
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;
    private String deleteQuestion;
    private String questionKeyToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_flagged);
        Bundle bundle = getIntent().getExtras();
        deleteQuestion = bundle.getString("questionsToDelete");
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
        TextView myTextView = (TextView)findViewById(R.id.textView);
        myTextView.setText("Are you sure you want to delete question: "+ deleteQuestion + "?");
    }

    /**
     * On delete button click function for admin functionality
     *
     * @param view from which the method is invoked
     */
    public void onDeleteButtonClick(View view) {
        Query contain = mDatabase.child("questions").orderByKey()
                .limitToFirst(Constant.NUM_OF_QUESTIONS);

        contain.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String keyQuestion = postSnapshot.getKey();
                    HashMap value = (HashMap) postSnapshot.getValue();
                    String queriedquestion = (String) value.get("question");
                    if (deleteQuestion.equals(queriedquestion)) {
                        questionKeyToDelete = keyQuestion;
                    }
                }
                deleteQuestion();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
            }
        });

    }

    /**
     * Private helper method to remove a question from database
     */
    private void deleteQuestion(){
        if(questionKeyToDelete != null) {
            mDatabase.child("questions").child(questionKeyToDelete).removeValue();
            Toast.makeText(getApplicationContext(),
                    deleteQuestion + " deleted!", Toast.LENGTH_LONG)
                    .show();
            Intent intent = new Intent(this, deleteFlaggedQuestions.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);
    }
}
