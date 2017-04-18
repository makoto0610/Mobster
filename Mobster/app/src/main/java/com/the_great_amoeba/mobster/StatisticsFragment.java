package com.the_great_amoeba.mobster;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Constants.Constant;

/**
 * Statistics fragment for users.
 *
 * @author Christine
 * @version 1.0
 */
public class StatisticsFragment extends Fragment {

    private DatabaseReference mDatabase;

    private static String asked;
    private static String answered;
    private TextView askedText;
    private TextView answeredText;

    private String username;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.statistics_layout,container, false);

        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(Constant.DB_URL);

        username = SaveSharedPreferences.getUserName(getActivity().getApplicationContext());
        System.out.println("Username: " + username);

        DatabaseReference askedRef = mDatabase.child("users").child(username).child("asked");
        askedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                asked = String.valueOf(dataSnapshot.getValue());
                askedText = (TextView)view.findViewById(R.id.asked);
                askedText.setText(asked);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference answeredRef = mDatabase.child("users").child(username).child("answered");
        answeredRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                answered = String.valueOf(dataSnapshot.getValue());
                answeredText = (TextView)view.findViewById(R.id.answered);
                answeredText.setText(answered);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}

