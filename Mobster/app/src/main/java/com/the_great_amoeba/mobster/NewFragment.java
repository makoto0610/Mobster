package com.the_great_amoeba.mobster;

/**
 * Created by C. Shih on 12/23/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.util.HashMap;
import java.util.LinkedList;

import Constants.Constant;
import Objects.Adapters.CustomListViewAdapter;
import Objects.DisplayQuestion;
import Helper.HelperMethods;

public class NewFragment extends Fragment {

    private DatabaseReference mDatabase;
    private View view;
    private DisplayQuestion[] array;

    private String user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constant.DB_URL);
        this.view = inflater.inflate(R.layout.new_layout, null);
        Helper.Log.d(Constant.DEBUG, "in OncreateView of NewFragment");
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
                String searchText = "";
                if (((MainActivity)getActivity()).isSearching() &&
                        (((MainActivity)getActivity()).getSearchedArea() == 1)) {
                    searchStatus = true;
                    searchText = ((MainActivity)getActivity()).getSearchedText();
                }

                // search keywords
                boolean keywordStatus = false;
                if (((MainActivity)getActivity()).isSearchingKeyword() &&
                        (((MainActivity)getActivity()).getSearchedArea() == 1)) {
                    keywordStatus = true;
                }
                System.out.println("ORIGINAL");
                System.out.println(searchStatus);
                System.out.println(keywordStatus);
                System.out.println("---------------");

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String keyQuestion = postSnapshot.getKey();
                    HashMap value = (HashMap) postSnapshot.getValue();
                    String status = (String) value.get("status");
                    String username = (String) value.get("username");
                    String questionTitle = (String) value.get("question");

                    boolean containsAll = false;
                    if (keywordStatus) {
                        String[] searchedKeywords = ((MainActivity)getActivity()).getKeywords();
                        String[] questionKeywords = new String[(int)postSnapshot.child("keywords").getChildrenCount()];
                        int arrayCount = 0;
                        for (DataSnapshot k : postSnapshot.child("keywords").getChildren()) {
                            questionKeywords[arrayCount] = (String)k.getValue();
                            arrayCount++;
                        }
                        containsAll = Arrays.asList(questionKeywords)
                                .containsAll(Arrays.asList(searchedKeywords));
                    }
                    boolean noSearch = true;
                    if (searchStatus || keywordStatus) {
                        noSearch = false;
                    }
                    System.out.println(questionTitle);
                    System.out.println(noSearch);
                    System.out.println(searchStatus);
                    System.out.println(keywordStatus);
                    System.out.println("---------------");
                    if (status.equals("NEW")
                            && ((searchStatus && questionTitle.contains(searchText))
                            || (containsAll)) || noSearch) {
//                        Helper.Log.i(Constant.DEBUG, "keys:" + value.keySet().toString());
//                        Helper.Log.i(Constant.DEBUG, "values: " + value.values().toString());
                        if (isHomeFragment()) {
                            DisplayQuestion question = HelperMethods.getQuestion(postSnapshot, value, keyQuestion);
                            questions.add(question);
                        } else { // else it is to be displayed in the My Questions Fragment
                            if (username.equals(user)) {
                                DisplayQuestion question = HelperMethods.getQuestion(postSnapshot, value, keyQuestion);
                                questions.add(question);
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

    //TODO: !!
    public void setDurationForQuestion(){

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
                    Intent intent = new Intent(view.getContext(), Voting.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    bundle.putChar("homeTabPassed", 'm');
                    Intent intent = new Intent(view.getContext(), Results.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }


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





}
