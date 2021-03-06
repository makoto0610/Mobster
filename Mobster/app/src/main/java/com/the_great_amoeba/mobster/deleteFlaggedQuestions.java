package com.the_great_amoeba.mobster;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Constants.Constant;

/**
 * Activity to delete flagged questions.
 *
 * @author Esha
 * @version 1.0
 */
public class deleteFlaggedQuestions extends AppCompatActivity {
    private ListView listView ;
    private List<String> array;
    private DatabaseReference mDatabase;
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";

    private String questionKeyToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_flagged_questions);
        this.array = new ArrayList<>();
        getFlaggedQuestionsFirebase();
    }

    /**
     * Get flagged questions from the firebase database.
     */
    public void getFlaggedQuestionsFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
        Query contain = mDatabase.child("questions").orderByKey()
                .limitToFirst(Constant.NUM_OF_QUESTIONS);

        contain.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0;
                int childCount = (int)(dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String keyQuestion = postSnapshot.getKey();
                    HashMap value = (HashMap) postSnapshot.getValue();
                    String question = (String) value.get("question");
                    if(value.get("isFlagged")!= null) {
                        Long isFlagged = (Long) value.get("isFlagged");
                        if (isFlagged >= 3) {
                            array.add(question);
                        }
                        index++;
                    }
                    Helper.Log.d(Constant.DEBUG, "" + index);
                }

                init_FlaggedQuestions_Display();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
            }
        });
    }

    /**
     * Initialize flagged questions display
     */
    public void init_FlaggedQuestions_Display() {

        // Get ListView object from xml

        listView = (ListView) findViewById(R.id.list);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, array);


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
                final String data = (String) listView.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(deleteFlaggedQuestions.this);
                builder.setTitle("Are you sure you want to delete question: "
                            + "\"" + data + "\"" + "?");
                builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Query contain = mDatabase.child("questions").orderByKey()
                                .limitToFirst(Constant.NUM_OF_QUESTIONS);

                        contain.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    String keyQuestion = postSnapshot.getKey();
                                    HashMap value = (HashMap) postSnapshot.getValue();
                                    String queriedquestion = (String) value.get("question");
                                    if (data.equals(queriedquestion)) {
                                        questionKeyToDelete = keyQuestion;
                                    }
                                }
                                deleteQuestion(data);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //TODO: questions not loading error message
                            }
                        });
                    }
                });
                builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setIcon(R.drawable.ic_warning);
                builder.show();
            }

        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);
    }

    /**
     * Delete a question.
     * @param deleteQuestion question to be deleted
     */
    private void deleteQuestion(String deleteQuestion){
        if(questionKeyToDelete != null) {
            mDatabase.child("questions").child(questionKeyToDelete).removeValue();
            Toast.makeText(getApplicationContext(),
                    "\"" + deleteQuestion + "\"" + " deleted.", Toast.LENGTH_LONG)
                    .show();
            Intent intent = new Intent(this, deleteFlaggedQuestions.class);
            startActivity(intent);
        }
    }
}
