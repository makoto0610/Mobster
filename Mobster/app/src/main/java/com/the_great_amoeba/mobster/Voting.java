package com.the_great_amoeba.mobster;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.LinearLayout.LayoutParams;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DatabaseReference;

import java.util.LinkedList;

import Constants.Constant;
import Helper.HelperMethods;
import Objects.Choice;

public class Voting extends Activity implements OnClickListener{

    private static final int MY_BUTTON = 9000;
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;
    private static String asked;
    private TextView questionText;
    private String[] choices; //choices/options provided in the question
    private float[] votes; //votes on each choice
    private LinearLayout ll;
    private String questionKey;
    private boolean selected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperMethods.setChosenTheme(this, getApplicationContext());

        setContentView(R.layout.vote_layout);
        Bundle bundle = getIntent().getExtras();
        questionKey = bundle.getString("questionPassed");
        ll = (LinearLayout)findViewById(R.id.linearLayout2);

        final ImageView flag = (ImageView) findViewById(R.id.imageView_flag);

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);

        flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //buttonPressed = true;
                flag.setImageResource(R.drawable.flag_button_red);
                mDatabase.child("questions").child(questionKey).child("isFlagged")
                        .setValue(1);

            }
        });



        DatabaseReference choicesRef = mDatabase.child("questions").child(questionKey).child("choices");


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

        choicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0;
                int childCount = (int)(dataSnapshot.getChildrenCount());
                choices = new String[childCount];
                votes = new float[childCount];
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    choices[index] = (String)d.child("option").getValue();
                    votes[index] = ((Long)d.child("vote").getValue()).floatValue();
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
                questionText = (TextView)findViewById(R.id.viewQuestion);
                questionText.setText(asked);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createRadioButtons(int num, String[] x, float[] y) {
        //add radio buttons
        final RadioButton[] rb = new RadioButton[num];
        RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.VERTICAL);
        for(int i=0; i<num; i++){
            rb[i]  = new RadioButton(this);
            rb[i].setText(x[i]);
            rb[i].setId(i);
            rb[i].setTextSize(30);
            rg.addView(rb[i]);

        }
        ll.addView(rg);
    }

    public void onClick(View v) {
        Toast toast;
        Log.w("ANDROID DYNAMIC VIEWS:", "View Id: " + v.getId());
        System.out.print(selected);
        selected = false;
        saveAnswers();
        if(selected) {
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

    public void saveAnswers() {
        LinearLayout root = (LinearLayout) findViewById(R.id.linearLayout2);
        loopQuestions(root);
    }

    private void loopQuestions(ViewGroup parent) {
        for(int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if(child instanceof RadioGroup ) {
                RadioGroup radio = (RadioGroup)child;
                if(radio.getCheckedRadioButtonId() != -1){
                    selected = true;
                    storeAnswer(radio.getId(), radio.getCheckedRadioButtonId());
                }
            }


            if(child instanceof ViewGroup) {
                ViewGroup group = (ViewGroup)child;
                loopQuestions(group);
            }
        }
    }

    private void storeAnswer(int question, int answer) {
        Log.w("ANDROID DYNAMIC VIEWS:", "Question: " + String.valueOf(question) + " * "+ "Answer: "
                + String.valueOf(answer) );
        //add answer in database
        Choice ob = new Choice(choices[Integer.parseInt(String.valueOf(answer))]);
        ob.setVote((int)votes[Integer.parseInt(String.valueOf(answer))]);
        ob.incrementVote();
        mDatabase.child("questions").child(questionKey).child("choices")
                .child(String.valueOf(answer)).setValue(ob);

        // Updating the "answered" value for the current user
        final String username = SaveSharedPreferences.getUserName(getApplicationContext());
        DatabaseReference answered = mDatabase.child("users").child(username).child("answered");;
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

    private void updateAnswered(int current, String username) {
        DatabaseReference toUpdate = mDatabase.child("users").child(username).child("answered");
        toUpdate.setValue(current);

    }

}