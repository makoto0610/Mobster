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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import Constants.Constant;
import Helper.HelperMethods;
import Objects.Adapters.CustomListViewAdapter;
import Objects.DisplayQuestion;
import Objects.Question;


public class TrendingFragment extends Fragment {

    private DatabaseReference mDatabase;
    private View view;
    private DisplayQuestion[] array;
    private String user;

    private ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constant.DB_URL);
        this.view = inflater.inflate(R.layout.new_layout, null);
        Log.d(Constant.DEBUG, "in OncreateView of TrendingFragment");

        this.progressBar = (ProgressBar) this.view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        this.user = SaveSharedPreferences.getUserName(getActivity().getApplicationContext());
        getNewQuestionsFromFirebase();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(Constant.DEBUG, "onResume TrendingFrag");
    }



    /**
     * Get new questions from Firebase (async)
     */
    public void getNewQuestionsFromFirebase() {
        Query contain = mDatabase.child("questions").orderByKey()
                .limitToFirst(Constant.NUM_OF_QUESTIONS);
        final LinkedList<DisplayQuestion> questions = new LinkedList<>();

        progressBar.setVisibility(View.VISIBLE);


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
                boolean keywordStatus = getKeywordSearchStatus();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String keyQuestion = postSnapshot.getKey();
                    HashMap value = (HashMap) postSnapshot.getValue();
                    String status = (String) value.get("status");
                    if (status.equals(Question.Status.CLOSED)) {
                        continue;
                    }
                    String questionTitle = (String) value.get("question");

                    boolean containsAll = keywordsMatch(keywordStatus, postSnapshot);

                    boolean noSearch = noSearchStatus(searchStatus, keywordStatus);

                    if (noSearch) {
                        DisplayQuestion question = HelperMethods.getQuestion(postSnapshot, value, keyQuestion);
                        if (!checkDuratationAndUpdateStatus(question)) {
                            questions.add(question);
                        }
                    } else if (searchStatus && questionTitle.contains(searchText)) {
                        DisplayQuestion question = HelperMethods.getQuestion(postSnapshot, value, keyQuestion);
                        if (!checkDuratationAndUpdateStatus(question)) {
                            questions.add(question);
                        }
                    } else if (containsAll) {
                        DisplayQuestion question = HelperMethods.getQuestion(postSnapshot, value, keyQuestion);
                        if (!checkDuratationAndUpdateStatus(question)) {
                            questions.add(question);
                        }
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

                progressBar.setVisibility(View.GONE);

                init_Questions_Display();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
                progressBar.setVisibility(View.GONE);
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
            boolean buttonPressed;
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {


                final DisplayQuestion dq = (DisplayQuestion) parentAdapter.getAdapter().getItem(position);
                final String questionKey = dq.getQuestionId();
                final String username = dq.getUsername();
                LinkedList<String> votedUsernames = dq.getVotedUsers();

                DisplayQuestion data = (DisplayQuestion) parentAdapter.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putString("questionPassed", data.getQuestionId());
                System.out.println("User name is " + username);
                System.out.println("USER is " + user);
                if (username.equals(user) || votedUsernames.contains(user)) {
                    Intent intent = new Intent(view.getContext(), Results.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(view.getContext(), Voting.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    // Searching Helpers
    private String getSearchText() {
        String toReturn = "";
        if (((MainActivity)getActivity()).isSearching() &&
                (((MainActivity)getActivity()).getSearchedArea() == 1)) {
            toReturn = ((MainActivity)getActivity()).getSearchedText();
        }
        return toReturn;
    }

    private boolean getKeywordSearchStatus() {
        if (((MainActivity)getActivity()).isSearchingKeyword() &&
                (((MainActivity)getActivity()).getSearchedArea() == 1)) {
            return true;
        }
        return false;
    }

    private boolean keywordsMatch(boolean keywordStatus, DataSnapshot postSnapshot) {
        if (keywordStatus) {
            String[] searchedKeywords = ((MainActivity)getActivity()).getKeywords();
            String[] questionKeywords = new String[(int)postSnapshot.child("keywords").getChildrenCount()];
            int arrayCount = 0;
            for (DataSnapshot k : postSnapshot.child("keywords").getChildren()) {
                questionKeywords[arrayCount] = (String)k.getValue();
                arrayCount++;
            }
            return Arrays.asList(questionKeywords)
                    .containsAll(Arrays.asList(searchedKeywords));
        }
        return false;
    }

    private boolean noSearchStatus(boolean searchText, boolean searchKeyword) {
        if (searchText || searchKeyword) {
            return false;
        }
        return true;
    }

    private boolean checkDuratationAndUpdateStatus(DisplayQuestion question) {
        if (question.getDuration().getMillis() == 0) {
            mDatabase.child("questions")
                    .child(question.getQuestionId())
                    .child("status").setValue(Question.Status.CLOSED);
            return true;
        }
        return false;
    }
}