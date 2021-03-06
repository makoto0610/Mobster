package com.the_great_amoeba.mobster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import Constants.Constant;
import Helper.HelperMethods;
import Objects.Choice;
import Objects.Comment;

/**
 * Voting page for the users.
 *
 * @author Esha
 * @version 1.0
 */
public class Voting extends Activity implements OnClickListener {

    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private static final int MY_BUTTON = 9000;
    private static String asked;
    private DatabaseReference mDatabase;
    private TextView questionText;
    private String[] choices; //choices/options provided in the question
    private float[] votes; //votes on each choice
    private LinearLayout ll;
    private String questionKey;
    private boolean selected;

    private EditText commentText;
    private String comment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperMethods.setChosenTheme(this, getApplicationContext());

        setContentView(R.layout.vote_layout);
        Bundle bundle = getIntent().getExtras();
        questionKey = bundle.getString("questionPassed");
        ll = (LinearLayout) findViewById(R.id.linearLayout2);
        commentText = (EditText) findViewById(R.id.commentText);

        final ImageView flag = (ImageView) findViewById(R.id.imageView_flag);
        flag.setTag(1);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);

        DatabaseReference flaggedRef = mDatabase.child("questions").child(questionKey)
                .child("flaggedByUsers");

        flaggedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String value = (String) postSnapshot.getValue();
                    if(value.equals(SaveSharedPreferences.getUserName(getApplicationContext()))){
                        flag.setTag(2);
                        flag.setImageResource(R.drawable.flag_button_red);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Checking if the flag has been pressed or unpressed
        flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if flag has been pressed update the image and add the user under flagged
                if(flag.getTag().equals(1)) {
                    flag.setTag(2);
                    flag.setImageResource(R.drawable.flag_button_red);
                    DatabaseReference flagged = mDatabase.child("questions").child(questionKey).child("isFlagged");
                    flagged.runTransaction(new Transaction.Handler() {

                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Long currentValue = (Long) mutableData.getValue();
                            if (currentValue == null) {
                                mutableData.setValue(1);
                            } else {
                                mutableData.setValue(currentValue + 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            Log.d(Constant.AUTH_TAG, "Transaction finished.");
                        }
                    });
                    DatabaseReference flaggedUsersRef = mDatabase.child("questions").child(questionKey)
                            .child("flaggedByUsers").push();
                    flaggedUsersRef.setValue(SaveSharedPreferences.getUserName(getApplicationContext()));
                } else {
                    //if flag was pressed then unpressed by the same user then update image and delete the user from the database
                    flag.setTag(1);
                    flag.setImageResource(R.drawable.flag_button);
                    DatabaseReference flagged = mDatabase.child("questions").child(questionKey).child("isFlagged");
                    flagged.runTransaction(new Transaction.Handler() {

                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Long currentValue = (Long) mutableData.getValue();
                            if (currentValue != null) {
                                mutableData.setValue(currentValue - 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            Log.d(Constant.AUTH_TAG, "Transaction finished.");
                        }
                    });
                    DatabaseReference flaggedRef = mDatabase.child("questions").child(questionKey)
                            .child("flaggedByUsers");

                    //removing user from flaggedbyUsers table if the user unflags the question
                    flaggedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String keyUser = postSnapshot.getKey();
                                String value = (String) postSnapshot.getValue();
                                if(value.equals(SaveSharedPreferences.getUserName(getApplicationContext()))){
                                    mDatabase.child("questions").child(questionKey).child("flaggedByUsers").child(keyUser).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
            }
        });

        DatabaseReference choicesRef = mDatabase.child("questions").child(questionKey).child("choices");

        //updates the number of accesses
        DatabaseReference access = mDatabase.child("questions").child(questionKey).child("num_access");
        access.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long currentValue = (Long) mutableData.getValue();
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(Constant.AUTH_TAG, "Transaction finished.");
            }
        });

        //pulls the choices from firebase and displays them
        choicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0;
                int childCount = (int) (dataSnapshot.getChildrenCount());
                choices = new String[childCount];
                votes = new float[childCount];
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    choices[index] = (String) d.child("option").getValue();
                    votes[index] = ((Long) d.child("vote").getValue()).floatValue();
                    index++;
                }
                createRadioButtons(childCount, choices, votes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);

        DatabaseReference askedRef = mDatabase.child("questions").child(questionKey).child("question");
        askedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                asked = String.valueOf(dataSnapshot.getValue());
                questionText = (TextView) findViewById(R.id.viewQuestion);
                questionText.setText(asked);
                questionText.setTextSize(33);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Creates radio buttons for choices.
     *
     * @param num The number of radio buttons to be created
     * @param x coordinates
     * @param y coordinates
     */
    private void createRadioButtons(int num, String[] x, float[] y) {
        //add radio buttons
        final RadioButton[] rb = new RadioButton[num];
        RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.VERTICAL);
        for (int i = 0; i < num; i++) {
            rb[i] = new RadioButton(this);
            rb[i].setText(x[i]);
            rb[i].setId(i);
            rb[i].setTextSize(27);
            rg.addView(rb[i]);

        }
        ll.addView(rg);
    }

    /**
     * When submit is pressed the vote is recorded.
     *
     * @param v The current view
     */
    public void onClick(View v) {
        Toast toast;
        Log.w("ANDROID DYNAMIC VIEWS:", "View Id: " + v.getId());
        System.out.print(selected);
        selected = false;
        saveAnswers();
        if (selected) {
            toast = Toast.makeText(this, "Answer Submitted", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
            Intent i = new Intent(this, Results.class);
            Bundle bundle = new Bundle();
            bundle.putString("questionPassed", questionKey);
            i.putExtras(bundle);
            startActivity(i);
        } else {
            Toast.makeText(this, "Invalid option. Please select an option before submitting",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Saves the answer that the user entered.
     */
    public void saveAnswers() {
        LinearLayout root = (LinearLayout) findViewById(R.id.linearLayout2);
        loopQuestions(root);
    }

    /**
     * Loop questions.
     * @param parent parent of the view group
     */
    private void loopQuestions(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof RadioGroup) {
                RadioGroup radio = (RadioGroup) child;
                if (radio.getCheckedRadioButtonId() != -1) {
                    selected = true;
                    storeAnswer(radio.getId(), radio.getCheckedRadioButtonId());
                }
            }

            if (child instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) child;
                loopQuestions(group);
            }
        }
    }

    /**
     * Store the voted result
     *
     * @param question question voted
     * @param answer answer chosen
     */
    private void storeAnswer(int question, int answer) {
        Log.w("ANDROID DYNAMIC VIEWS:", "Question: " + String.valueOf(question) + " * " + "Answer: "
                + String.valueOf(answer));
        //add answer in database
        Choice ob = new Choice(choices[Integer.parseInt(String.valueOf(answer))]);
        ob.setVote((int) votes[Integer.parseInt(String.valueOf(answer))]);
        ob.incrementVote();
        mDatabase.child("questions").child(questionKey).child("choices")
                .child(String.valueOf(answer)).setValue(ob);

        //store the comment (if not empty)
        comment = commentText.getText().toString();
        if (!comment.equals("")) {
            DatabaseReference cref = mDatabase.child("questions").child(questionKey).child("comments").push();
            Comment c = new Comment(comment);
            cref.setValue(c);
        }

        // adding the current user to answered list so he cannot answer questions more than once
        DatabaseReference votedUsersRef = mDatabase.child("questions").child(questionKey)
                .child("votedUsers").push();
        votedUsersRef.setValue(SaveSharedPreferences.getUserName(getApplicationContext()));


        // Updating the "answered" value for the current user
        final String username = SaveSharedPreferences.getUserName(getApplicationContext());
        DatabaseReference answered = mDatabase.child("users").child(username).child("answered");
        answered.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int currentAnswered = dataSnapshot.getValue(Integer.class);
                updateAnswered(currentAnswered + 1, username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Updates number of questions answered by the user in the database.
     *
     * @param current The current ount of the questions the user has voted on
     * @param username The username who voted
     */
    private void updateAnswered(int current, String username) {
        DatabaseReference toUpdate = mDatabase.child("users").child(username).child("answered");
        toUpdate.setValue(current);
    }
}