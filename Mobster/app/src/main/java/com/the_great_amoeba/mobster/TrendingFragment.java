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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
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

                    String questionTitle = (String) value.get("question");

                    boolean containsAll = keywordsMatch(keywordStatus, postSnapshot);

                    boolean noSearch = noSearchStatus(searchStatus, keywordStatus);

                    if (noSearch) {
                        //TODO: sort by accesses
//                        Helper.Log.i(Constant.DEBUG, "keys:" + value.keySet().toString());
//                        Helper.Log.i(Constant.DEBUG, "values: " + value.values().toString());
                        long upvotes =  (long) value.get("num_upvotes");
                        long downvotes = (long) value.get("num_downvotes");
                        long rating = upvotes - downvotes;
                        long access = (long) value.get("num_access");
                        DisplayQuestion question = new DisplayQuestion((String) (value.get("question")),
                                new Duration(6000000),
                                rating, keyQuestion, access);
                        questions.add(question);

                    } else if (searchStatus && questionTitle.contains(searchText)) {
//                        Helper.Log.i(Constant.DEBUG, "keys:" + value.keySet().toString());
//                        Helper.Log.i(Constant.DEBUG, "values: " + value.values().toString());
                        long upvotes =  (long) value.get("num_upvotes");
                        long downvotes = (long) value.get("num_downvotes");
                        long rating = upvotes - downvotes;
                        long access = (long) value.get("num_access");
                        DisplayQuestion question = new DisplayQuestion((String) (value.get("question")),
                                new Duration(6000000),
                                rating, keyQuestion, access);
                        questions.add(question);

                    } else if (containsAll) {
//                        Helper.Log.i(Constant.DEBUG, "keys:" + value.keySet().toString());
//                        Helper.Log.i(Constant.DEBUG, "values: " + value.values().toString());
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
            boolean buttonPressed;
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {
                buttonPressed = false;
                // after we change the icon(s):
                // 1) get the display question associated with the listview entry
                // 2) increment the number of upvotes / downvotes
                // a) TODO: Logic if the user has already voted on the question, then opposite one should be decremented (and vice versa)
                // b) if the user hasn't voted yet we can just increment one of them
                // 3) update the current rating

                final DisplayQuestion dq = (DisplayQuestion) parentAdapter.getAdapter().getItem(position);
                final String questionKey = dq.getQuestionId();

                final ImageView upVote = (ImageView) view.findViewById(R.id.imageView_upVote);
                final ImageView downVote = (ImageView) view.findViewById(R.id.imageView_downVote);

                final View relativeLayout = view;

                upVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonPressed = true;
                        downVote.setImageResource(R.drawable.ic_down_vote_green);
                        upVote.setImageResource(R.drawable.ic_up_vote_orange);

                        //NOTE: rating display changed before the database gets updated
                        // had to do it this way as a workaround
                        // ideally, want database updated THEN rating display changed
                        // database/display mismatch might occur if a database error occurs
                        dq.setRating(dq.getRating() + 1);
                        updateRating(relativeLayout, dq.getRating());

                        //begin upvote transaction
                        DatabaseReference accessUp = mDatabase.child("questions").child(questionKey).child("num_upvotes");
                        accessUp.runTransaction(new Transaction.Handler() {
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
                                if (databaseError == null) {
                                    Helper.Log.d(Constant.DEBUG, "Transaction finished.");
                                    //Helper.Log.d(Constant.DEBUG, dataSnapshot.toString());
                                }
                                else Helper.Log.d(Constant.DEBUG, "Transaction finished w/ database error " + databaseError.toString());
                            }

                        });

                    }
                });
                downVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonPressed = true;
                        downVote.setImageResource(R.drawable.ic_down_vote_orange);
                        upVote.setImageResource(R.drawable.ic_up_vote_green);

                        dq.setRating(dq.getRating() - 1 );
                        updateRating(relativeLayout, dq.getRating());

                        //begin downvote transaction
                        DatabaseReference accessDown = mDatabase.child("questions").child(questionKey).child("num_downvotes");
                        accessDown.runTransaction(new Transaction.Handler() {
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
                                if (databaseError == null) {
                                    Helper.Log.d(Constant.DEBUG, "Transaction finished.");
                                }
                                else Helper.Log.d(Constant.DEBUG, "Transaction finished w/ database error " + databaseError.toString());
                            }

                        });
                    }
                });
                DisplayQuestion data = (DisplayQuestion) parentAdapter.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), Voting.class);
                Bundle bundle = new Bundle();
                bundle.putString("questionPassed", data.getQuestionId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * Method called after the upvote/downvote transactions are processed
     * @param relativeLayout - the relativeLayout (the list view row) to update
     * @param newRating - the new rating to be displayed
     */
    private void updateRating(View relativeLayout, long newRating) {
        Helper.Log.d(Constant.DEBUG, relativeLayout.toString());
        TextView duration = (TextView) relativeLayout.findViewById(R.id.textView_Rating);
        duration.setText("" + newRating);
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
}