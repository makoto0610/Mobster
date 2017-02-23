package com.the_great_amoeba.mobster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by singh on 2/22/2017.
 */

public class Ban extends AppCompatActivity {
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;
    private String userToBanPassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban);
        Bundle bundle = getIntent().getExtras();
        userToBanPassed = bundle.getString("userToBan");
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
        TextView myTextView = (TextView)findViewById(R.id.textView);
        myTextView.setText("Are you sure you want to ban user "+ userToBanPassed + "?");
    }

    public void onBanButtonClick(View view) {

        mDatabase.child("users").child(userToBanPassed).setValue(null);
        userBanned();
    }

    private void userBanned(){
        Toast.makeText(getApplicationContext(),
                userToBanPassed+ " Banned!" , Toast.LENGTH_LONG)
                .show();
        Intent intent = new Intent(this, BanUser.class);
        startActivity(intent);
    }
}
