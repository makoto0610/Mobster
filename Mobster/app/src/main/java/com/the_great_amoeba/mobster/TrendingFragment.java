package com.the_great_amoeba.mobster;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

/**
 * Trending page for main pages.
 *
 * @author makoto
 * @version 1.0
 */
public class TrendingFragment extends Fragment {

    private DatabaseReference mDatabase;
    private View view;
    private DisplayQuestion[] array;
    private ListView listView;

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
        this.listView = (ListView) this.view.findViewById(R.id.mobile_list);
        progressBar.setVisibility(View.INVISIBLE);


        this.user = SaveSharedPreferences.getUserName(getActivity().getApplicationContext());
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

        listView.setVisibility(View.INVISIBLE);
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

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String keyQuestion = postSnapshot.getKey();
                    HashMap value = (HashMap) postSnapshot.getValue();
                    String status = (String) value.get("status");
                    if (status.equals(Question.Status.CLOSED.toString())) {
                        continue;
                    }
                    String questionTitle = (String) value.get("question");


                    if (searchStatus) {
                        //NOTE: sort by accesses
                        if (searchMatch(searchText, questionTitle, postSnapshot)) {
                            DisplayQuestion question = HelperMethods.getQuestion(postSnapshot, value, keyQuestion);
                            if (!checkDuratationAndUpdateStatus(question)) {
                                questions.add(question);
                            }
                        }
                    } else {
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

                //wait for a set amount of time before we display anything
                Handler handler=new Handler();
                Runnable r=new Runnable() {
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);

                        init_Questions_Display();
                    }
                };
                handler.postDelayed(r, Constant.DEFAULT_LOADING_WAIT);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //NOTE: questions not loading error message
                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Initialize all of the questions for display on the ListView
     */
    public void init_Questions_Display() {

        CustomListViewAdapter questions = new CustomListViewAdapter(getContext(), R.layout.list_view,
                Arrays.asList(this.array));

        this.listView.setAdapter(questions);
        // react to click
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    /**
     * Private search method helper to get text
     *
     * @return text of the search
     */
    private String getSearchText() {
        String toReturn = "";
        if (((MainActivity) getActivity()).isSearching() &&
                (((MainActivity) getActivity()).getSearchedArea() == 1)) {
            toReturn = ((MainActivity) getActivity()).getSearchedText();
        }
        return toReturn;
    }

    /**
     * Boolean function to see whether there is a match or not
     *
     * @param searched searched text
     * @param question content of the question
     * @param postSnapshot data snap shot
     * @return true if match, false otherwise
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

    /**
     * Check and update the status of the question
     *
     * @param question question to check for
     * @return true if updated to closed, false otherwise
     */
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