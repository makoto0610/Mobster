package com.the_great_amoeba.mobster;

/**
 * Created by C. Shih on 12/23/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StatisticsFragment extends Fragment {

    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;

    private static String asked;
    private static String answered;
    private TextView askedText;
    private TextView answeredText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.statistics_layout,container, false);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
        DatabaseReference askedRef = mDatabase.child("users").child("jeff").child("asked");
        askedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                asked = String.valueOf(dataSnapshot.getValue());
//                System.out.println("ASK: " + asked);
                askedText = (TextView)view.findViewById(R.id.asked);
                askedText.setText(asked);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference answeredRef = mDatabase.child("users").child("jeff").child("answered");
        answeredRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                answered = String.valueOf(dataSnapshot.getValue());
//                System.out.println("ANS1: " + answered);
                answeredText = (TextView)view.findViewById(R.id.answered);
                answeredText.setText(answered);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        System.out.println("ANS2:  " + answered);

//        askedText = (TextView)view.findViewById(R.id.asked);
//        askedText.setText(asked);
//        answeredText = (TextView)view.findViewById(R.id.answered);
//        answeredText.setText(answered);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Results.class);
                startActivity(i);
            }
        };
        Button button = (Button) view.findViewById(R.id.to_results_temp);
        button.setOnClickListener(listener);

        return view;
//        return inflater.inflate(R.layout.statistics_layout,null);
    }
}

