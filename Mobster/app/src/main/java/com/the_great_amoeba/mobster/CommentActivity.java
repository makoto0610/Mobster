package com.the_great_amoeba.mobster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Constants.Constant;
import Helper.HelperMethods;
import Objects.Adapters.CustomListViewAdapterComment;
import Objects.Comment;

public class CommentActivity extends AppCompatActivity {

    TextView noCommentsText;

    private DatabaseReference mDatabase;
    private List<Comment> comments;
    String questionPassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperMethods.setChosenTheme(this, getApplicationContext());
        setContentView(R.layout.activity_comment);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(Constant.DB_URL);
        Bundle bundle = getIntent().getExtras();
        questionPassed = bundle.getString("questionPassed");
        comments = new ArrayList<>();
        noCommentsText = (TextView) findViewById(R.id.noCommentsText);

        initCommentsFromFB();

    }

    private void initCommentsFromFB() {
        DatabaseReference commentsRef = mDatabase.child("questions")
                .child(questionPassed).child("comments");
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    HashMap commentMap = (HashMap) d.getValue();
                    Helper.Log.d(Constant.DEBUG, commentMap.toString());
                    Comment c = new Comment((String) commentMap.get("comment"), (String) commentMap.get("user"));
                    comments.add(c);
                }
                initCommentDisplay();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initCommentDisplay() {
        CustomListViewAdapterComment commentsAdapter = new CustomListViewAdapterComment(getApplicationContext(), R.layout.list_view_comment,
              this.comments);

        final ListView listView = (ListView) findViewById(R.id.comment_list);
        listView.setAdapter(commentsAdapter);

        if (!comments.isEmpty()) {
            noCommentsText.setVisibility(View.GONE);
        } else {
            noCommentsText.setVisibility(View.VISIBLE);
        }

    }
}
