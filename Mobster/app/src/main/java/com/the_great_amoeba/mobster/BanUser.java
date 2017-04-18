package com.the_great_amoeba.mobster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import Constants.Constant;
import Objects.BannedUser;

/**
 * Admin ban user page.
 *
 * @author Esha
 * @version 1.0
 */
public class BanUser extends Activity {
    private ListView listView ;
    private String[] array;
    private DatabaseReference mDatabase;
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";

    private int length;
    private String[] questionsToDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban_user);
        getUsersFirebase();

    }

    /**
     * Get list of users from the firebase
     */
    public void getUsersFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
        Query contain = mDatabase.child("users");

        contain.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0;
                int childCount = (int)(dataSnapshot.getChildrenCount());
                array = new String[childCount];
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    array[index] = (String)d.child("username").getValue();
                    index++;
                }
                init_Users_Display();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
            }
        });

    }

    /**
     * Initiate the users display in a list view format
     */
    public void init_Users_Display() {

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, array);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                final String data = (String) listView.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(BanUser.this);
                builder.setTitle("Are you sure you want to ban " + data + "?");
                builder.setPositiveButton("BAN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BannedUser user = new BannedUser(data);
                        mDatabase.child("admin").child("banned").child(data).setValue(user);
                        mDatabase.child("users").child(data).removeValue();
                        Query contain = mDatabase.child("questions").orderByKey()
                                .limitToFirst(Constant.NUM_OF_QUESTIONS);

                        contain.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int index = 0;
                                length = 0;
                                int childCount = (int)(dataSnapshot.getChildrenCount());
                                questionsToDelete = new String[childCount];
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    String keyQuestion = postSnapshot.getKey();
                                    HashMap value = (HashMap) postSnapshot.getValue();
                                    String username = (String) value.get("username");
                                    if (data.equals(username)) {
                                        questionsToDelete[index] = keyQuestion;
                                        length++;
                                    }
                                }
                                userBanned(data);
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //TODO: questions not loading error message
                            }
                        });


                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setIcon(R.drawable.ic_warning);
                builder.show();


//                Intent intent = new Intent(view.getContext(), Ban.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("userToBan", data);
//                intent.putExtras(bundle);
//                startActivity(intent);

            }

        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);

    }

    /**
     * Private helper method for banning users
     *
     * @param userToBanPassed username of user to be banned
     */
    private void userBanned(String userToBanPassed){
        for (int i = 0; i < length; i++) {
            mDatabase.child("questions").child(questionsToDelete[i]).setValue(null);
        }
        Toast.makeText(getApplicationContext(),
                userToBanPassed+ " Banned." , Toast.LENGTH_LONG)
                .show();
        Intent intent = new Intent(this, BanUser.class);
        startActivity(intent);
    }

}
