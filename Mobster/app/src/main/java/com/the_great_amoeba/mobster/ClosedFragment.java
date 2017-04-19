package com.the_great_amoeba.mobster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.Duration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import Constants.Constant;
import Helper.HelperMethods;
import Objects.Adapters.CustomListViewAdapter;
import Objects.DisplayQuestion;

/**
 * Closed fragments for the main pages.
 *
 * @author Christine
 * @version 1.0
 */
public class ClosedFragment extends Fragment {

    private DatabaseReference mDatabase;
    private View view;
    private DisplayQuestion[] array;

    private String user;

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentTransaction mFragmentTransaction = getFragmentManager()
                .beginTransaction();
        mFragmentTransaction.addToBackStack(null);
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constant.DB_URL);
        this.view = inflater.inflate(R.layout.new_layout, null);
        Log.d(Constant.DEBUG, "in OncreateView of ClosedFragment");

        this.progressBar = (ProgressBar) this.view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        getNewQuestionsFromFirebase();

        this.user = SaveSharedPreferences.getUserName(getActivity().getApplicationContext());
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

                // search questions
                boolean searchStatus = false;
                String searchText = getSearchText();
                if (!searchText.equals("")) {
                    searchStatus = true;
                }

                // search keywords

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String keyQuestion = postSnapshot.getKey();
                    HashMap value = (HashMap) postSnapshot.getValue();
                    String username = (String) value.get("username");
                    String status = (String) value.get("status");

                    String questionTitle = (String) value.get("question");

                    if (status.equals("CLOSED")) {
                        if (searchStatus) {
                            if (searchMatch(searchText, questionTitle, postSnapshot)) {
                                if (isHomeFragment()) {
                                    DisplayQuestion question = HelperMethods.getQuestion(postSnapshot, value, keyQuestion);
                                    question.setDuration(new Duration(0));
                                    questions.add(question);
                                } else { // else it is to be displayed in the My Questions Fragment
                                    if (username.equals(user)) {
                                        DisplayQuestion question = HelperMethods.getQuestion(postSnapshot, value, keyQuestion);
                                        questions.add(question);
                                    }
                                }
                            }
                        } else {
                            if (isHomeFragment()) {
                                DisplayQuestion question = HelperMethods.getQuestion(postSnapshot, value, keyQuestion);
                                question.setDuration(new Duration(0));
                                questions.add(question);
                            } else { // else it is to be displayed in the My Questions Fragment
                                if (username.equals(user)) {
                                    DisplayQuestion question = HelperMethods.getQuestion(postSnapshot, value, keyQuestion);
                                    question.setDuration(new Duration(0));
                                    questions.add(question);
                                }
                            }
                        }
                    }

                }
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
                Bundle bundle = new Bundle();
                bundle.putString("questionPassed", data.getQuestionId());
                if (isHomeFragment()) {
                    bundle.putChar("homeTabPassed", 'h');
                } else {
                    bundle.putChar("homeTabPassed", 'm');
                }
                Intent intent = new Intent(view.getContext(), Results.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    /**
     * Returns whether or not the parent fragment is the Home Tab Fragment (or the My Questions Tab)
     *
     * @return true if the current fragment's parent is the HomeTabFragment. False if the parent
     *          is the MyQuestionsFragment
     */
    private boolean isHomeFragment() {
        return getParentFragment().getClass().equals(new HomeTabFragment().getClass());
    }

    /**
     * Searching text helper method
     *
     * @return string to search for
     */
    private String getSearchText() {
        String toReturn = "";
        if (((MainActivity)getActivity()).isSearching()) {
            toReturn = ((MainActivity)getActivity()).getSearchedText();
        }
        return toReturn;
    }

    /**
     * Searching function with regex
     *
     * @param searched searched words
     * @param question questions to be searched
     * @param postSnapshot data snap shot
     * @return boolean of whether criteria is matched
     */
    private boolean searchMatch(String searched, String question, DataSnapshot postSnapshot) {
        String[] words = searched.split("\\s*(,|\\?|\\s)\\s*");

        // check keywords
        boolean keywordsMatch = false;
        for (DataSnapshot k : postSnapshot.child("keywords").getChildren()) {
            if (Arrays.asList(words).contains((String)k.getValue())) {
                keywordsMatch = true;
                break;
            }
        }

        // check questions title
        String[] title = question.split("\\s*(,|\\?|\\s)\\s*");

        boolean titleMatch = Arrays.asList(title).containsAll(Arrays.asList(words));

        return (keywordsMatch || titleMatch);
    }
}
