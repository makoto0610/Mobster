package com.the_great_amoeba.mobster;

/**
 * Created by C. Shih on 12/23/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.Duration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import Constants.Constant;
import Objects.Adapters.CustomListViewAdapter;
import Objects.DisplayQuestion;


public class TrendingFragment extends Fragment {

    private DatabaseReference mDatabase;
    private View view;
    private DisplayQuestion[] array;

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
        final LinkedList<DisplayQuestion> questions = new LinkedList<>();

        contain.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String keyQuestion = postSnapshot.getKey();
                    HashMap value = (HashMap) postSnapshot.getValue();
                    String status = (String) value.get("status");

                    String questionTitle = (String) value.get("question");
                    boolean searchStatus = false;
                    if (((MainActivity)getActivity()).isSearching() &&
                            (((MainActivity)getActivity()).getSearchedArea() == 1)) {
                        searchStatus = true;
                    }
                    String searchText = "";
                    if (searchStatus) {
                        searchText = ((MainActivity)getActivity()).getSearchedText();
                    }
                    if (questionTitle.contains(searchText)) {
                        //TODO: sort by accesses
                        Helper.Log.i(Constant.DEBUG, "keys:" + value.keySet().toString());
                        Helper.Log.i(Constant.DEBUG, "values: " + value.values().toString());
                        long upvotes =  (long) value.get("num_upvotes");
                        long downvotes = (long) value.get("num_downvotes");
                        long rating = upvotes - downvotes;
                        long access = (long) value.get("num_access");
                        DisplayQuestion question = new DisplayQuestion((String) (value.get("question")),
                                new Duration(6000000),
                                rating, keyQuestion, access);
                        questions.add(question);

                    }
                }
                Collections.sort(questions, new Comparator<DisplayQuestion>() {
                    @Override
                    public int compare(DisplayQuestion o1, DisplayQuestion o2) {
                        return (int) (o2.getNum_access() - o1.getNum_access());
                    }
                });
                array = new DisplayQuestion[questions.size()];
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

        CustomListViewAdapter questions = new CustomListViewAdapter(getContext(), R.layout.list_view,
                Arrays.asList(this.array));

        ListView listView = (ListView) this.view.findViewById(R.id.mobile_list);
        listView.setAdapter(questions);
        // react to click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {
                DisplayQuestion data = (DisplayQuestion) parentAdapter.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), Voting.class);
                Bundle bundle = new Bundle();
                bundle.putString("questionPassed", data.getQuestionId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}