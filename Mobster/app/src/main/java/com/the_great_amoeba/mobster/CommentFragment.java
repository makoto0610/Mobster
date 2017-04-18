package com.the_great_amoeba.mobster;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Constants.Constant;
import Objects.Comment;

/**
 * Comment fragment class for comments.
 *
 * @author Ani
 * @version 1.0
 */
public class CommentFragment extends Fragment {

    TextView noCommentsText;

    private DatabaseReference mDatabase;

    private List<Comment> comments;
    private List<String> commentsStrings;


    String questionPassed;

    ListView listView;
    Button postButton;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.comment_tab, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(Constant.DB_URL);
        Bundle bundle = getArguments();
        questionPassed = bundle.getString("questionPassed");
        comments = new ArrayList<>();
        commentsStrings = new ArrayList<>();
        noCommentsText = (TextView) view.findViewById(R.id.noCommentsText);
        this.listView = (ListView) this.view.findViewById(R.id.comment_list);;

        this.postButton = (Button) view.findViewById(R.id.postButton);

        View.OnClickListener listener = new View.OnClickListener()  {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setMessage("Enter your comment.")
                        .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String comment = input.getText().toString();
                                addComment(comment);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog ad = builder.create();
                ad.setView(input);
                ad.show();
            }
        };

        postButton.setOnClickListener(listener);

        initCommentsFromFB();

        return view;
    }

    /**
     * Add a comment to comment section.
     *
     * @param comment a new comment to add
     */
    private void addComment(String comment) {
        if (!comment.equals("")) {
            DatabaseReference cref = mDatabase.child("questions").child(questionPassed).child("comments").push();
            Comment c = new Comment(comment);
            cref.setValue(c);

            //update array adapter and remove no comments text
            comments.add(c);
            commentsStrings.add(c.getComment());
            this.listView.setAdapter(generateArrayAdapter());

            noCommentsText.setVisibility(View.GONE);

        }
    }

    /**
     * Initialize all the comments from the database
     */
    private void initCommentsFromFB() {
        DatabaseReference commentsRef = mDatabase.child("questions")
                .child(questionPassed).child("comments");
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    HashMap commentMap = (HashMap) d.getValue();
                    Helper.Log.d(Constant.DEBUG, commentMap.toString());
                    Comment c = new Comment((String) commentMap.get("comment"));
                    comments.add(c);
                    commentsStrings.add(c.getComment());
                }
                initCommentDisplay();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Create an array adapter for display comments
     *
     * @return an array adapter
     */
    private ArrayAdapter<String> generateArrayAdapter() {
        return new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                this.commentsStrings);
    }


    /**
     * Initialize comments display
     */
    private void initCommentDisplay() {
        this.listView.setAdapter(generateArrayAdapter());
        if (!comments.isEmpty()) {
            noCommentsText.setVisibility(View.GONE);
        } else {
            noCommentsText.setVisibility(View.VISIBLE);
        }
    }

}
