package com.the_great_amoeba.mobster;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DatabaseReference;

import java.util.LinkedList;

import Objects.Choice;

public class Voting extends Activity implements OnClickListener{

    private static final int MY_BUTTON = 9000;
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;
    private static String asked;
    private TextView questionText;
    private String[] choices; //choices/options provided in the question
    private Choice[] choiceObjects;
    private float[] votes; //votes on each choice
    private LinearLayout ll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_layout);

        ll = (LinearLayout)findViewById(R.id.linearLayout2);

        Button b = new Button(this);
        b.setText("Submit!");
        b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        b.setId(MY_BUTTON);
        b.setOnClickListener(this);
        ll.addView(b);

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
        DatabaseReference choicesRef = mDatabase.child("questions").child("How are you?").child("choices");
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

        DatabaseReference askedRef = mDatabase.child("questions").child("How are you?").child("question");
        askedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                asked = String.valueOf(dataSnapshot.getValue());
                questionText = (TextView)findViewById(R.id.textView5);
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
            rg.addView(rb[i]);

        }
        ll.addView(rg);
    }

    public void onClick(View v) {
        Toast toast;
        Log.w("ANDROID DYNAMIC VIEWS:", "View Id: " + v.getId());
        switch (v.getId()) {
            case MY_BUTTON:
                toast = Toast.makeText(this, "Answer Submitted", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
                saveAnswers();
                break;

        }
    }

    public void saveAnswers() {
        LinearLayout root = (LinearLayout) findViewById(R.id.linearLayout1);
        loopQuestions(root);
    }

    private void loopQuestions(ViewGroup parent) {
        for(int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if(child instanceof RadioGroup ) {
                RadioGroup radio = (RadioGroup)child;
                storeAnswer(radio.getId(), radio.getCheckedRadioButtonId());
            }


            if(child instanceof ViewGroup) {
                ViewGroup group = (ViewGroup)child;
                loopQuestions(group);
            }
        }
    }

    private void storeAnswer(int question, int answer) {
        Log.w("ANDROID DYNAMIC VIEWS:", "Question: " + String.valueOf(question) + " * "+ "Answer: " + String.valueOf(answer) );
        //add answer in database
        Choice ob = new Choice(choices[Integer.parseInt(String.valueOf(answer))]);
        ob.setVote((int)votes[Integer.parseInt(String.valueOf(answer))]);
        ob.incrementVote();
        mDatabase.child("questions").child("How are you?").child("choices").child(String.valueOf(answer)).setValue(ob);
        Toast toast = Toast.makeText(this,"Answer: " + String.valueOf(answer), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();


    }

}