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

public class Voting extends Activity implements OnClickListener{

    private static final int MY_BUTTON = 9000;
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;
    private static String asked;
    private TextView questionText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_layout);

        LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout2);

        Button b = new Button(this);
        b.setText("Submit!");
        b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        b.setId(MY_BUTTON);
        b.setOnClickListener(this);
        ll.addView(b);

        //add radio buttons
        final RadioButton[] rb = new RadioButton[5];
        RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.VERTICAL);
        for(int i=0; i<5; i++){
            rb[i]  = new RadioButton(this);
            rb[i].setText("Dynamic Radio Button " + i);
            rb[i].setId(i);
            rg.addView(rb[i]); //the RadioButtons are added to the radioGroup instead of the layout

        }
        ll.addView(rg);//you add the whole RadioGroup to the layout

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);

        // DatabaseReference askedRef = mDatabase.child("questions").child("question").child();//put the question id passed from the view page
        //pull question from database instead of question
        DatabaseReference askedRef = mDatabase.child("users").child("bob").child("email");
//        Query query = mDatabase.child("issue").orderByChild("id").equalTo(0);
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

        Toast toast = Toast.makeText(this,"Answer: " + String.valueOf(answer), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();


    }

}