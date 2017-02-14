package com.the_great_amoeba.mobster;

/**
 * Created by C. Shih on 12/23/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;

import Constants.Constant;
import Objects.Question;


public class NewFragment extends Fragment {

    private DatabaseReference mDatabase;
    private View view;
    private String[] array;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constant.DB_URL);
        this.view = inflater.inflate(R.layout.new_layout, null);
        Log.d(Constant.DEBUG, "in OncreateView");
        getNewQuestionsFromFirebase();
        return view;
    }


    /**
     * Get new questions from Firebase (async)
     */
    public void getNewQuestionsFromFirebase() {
        Query contain = mDatabase.child("questions").orderByKey()
                .limitToFirst(Constant.NUM_OF_QUESTIONS);
        final LinkedList<String> questions = new LinkedList<>();

        contain.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    HashMap value = (HashMap) postSnapshot.getValue();
                    Log.d(Constant.DEBUG, "keys:" + value.keySet().toString());
                    Log.d(Constant.DEBUG, "values: " + value.values().toString());
                    String status = (String) value.get("status");
//                    if (status.equals(Question.Status.NEW)) {
                    String question = (String) value.get("question");
                    questions.add(question);
//                    }
                }
                array = new String[questions.size()];
                array = questions.toArray(array);
                init_Questions_Display();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
            }
        });

    }


    /**
     * Initialize all of the questions for display on the ListView
     */
    public void init_Questions_Display() {
        ArrayAdapter<String> questions =
                new ArrayAdapter<>(getContext(), R.layout.list_view, this.array);

        ListView listView = (ListView) this.view.findViewById(R.id.mobile_list);
        listView.setAdapter(questions);
        // react to click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {
//                Intent intent = new Intent(NewFragment.this, QuestionDetail.class);
//                intent.putExtra("userIndex", id);
//                startActivity(intent);
            }
        });
    }





}
