package com.the_great_amoeba.mobster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import Constants.Constant;
import Objects.User;
import Helper.HelperMethods;

public class Registration extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText username;
    private String usernameBanned;
    private EditText password;
    private EditText confirm;
    private EditText email;

    private Boolean isExist;

    private Context context;
    private String choices[];
    private boolean flagBanned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_registration);
        context = this;
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constant.DB_URL);
        mAuth = FirebaseAuth.getInstance();


        // [START auth_state_listener] - Firebase Auth State Listener that listens to changes
        // in a user's logged in status
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(Constant.AUTH_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(Constant.AUTH_TAG, "onAuthStateChanged:signed_out");
                }
                //updateUI(user);
            }
        };

        Log.d(Constant.AUTH_TAG, "Testing log");

        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        confirm = (EditText) findViewById(R.id.register_confirm_password);
        email = (EditText) findViewById(R.id.register_email);

    }

    /**
     * Method called after create button pressed.
     * Handles input validation and displays appropriate error messages or if everything is fine,
     * creates a new user and starts MainActivity.
     */
    public void onCreateAccountClick(View view) {
        /* ***************
            1) Check if the username and password fields are non-zero in length
            2) Check if the password matches the confirm password field
            3) Check if the username is unique (i.e. there is no matching username in the users
            table of the Firebase DB.

            Display an error dialogue in case any of the checks do not pass.
           *************** */

        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        String confirm = this.confirm.getText().toString();
        String email = this.email.getText().toString();
        Query contain = mDatabase.child("users").orderByKey().equalTo(username);
        //usernameBanned = username;

        if (username.length()== 0) {
            HelperMethods.errorDialog(this, "Username not entered",
                    "You did not enter a username.");
            this.confirm.setText("");
        } else if(password.length()== 0 ||  confirm.length()== 0) {
            HelperMethods.errorDialog(this, "Password or confirm password not entered",
                    "You did not enter a password.");
            this.confirm.setText("");
        } else if (username.contains(" ")) {
            HelperMethods.errorDialog(this, "Username invalid",
                    "You cannot have any whitespace in username.");
            this.confirm.setText("");
        } else if (!password.equals(confirm)) {
            HelperMethods.errorDialog(this, "Not matching password",
                    "Your password and confirmed password were different.");
            this.confirm.setText("");
        } else if (checkUsernameExists(username)) {
            HelperMethods.errorDialog(this, "Username invalid",
                    "Username already exists");
            this.confirm.setText("");
        } else if (email.length() == 0 || !email.contains("@") || email.contains(" ")) {
            HelperMethods.errorDialog(this, "Email invalid",
                    "Email cannot be empty and must be valid.");
        } else {
            User user = new User(username, password, email);
            addNewUser(user);
        }
    }

    /**
     * Method that queries Firebase DB to see if entered username matches an existing username.
     */
    public boolean checkUsernameExists(final String enteredUsername) {
        final Boolean[] isExist = {false};
        mDatabase.child("users").child(enteredUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    isExist[0] = true;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return isExist[0];
    }

    /**
     * Method that queries Firebase DB to see if entered username matches an existing username that was banned.
     */
    public  void checkUsernameBanned(final String username, final String password, final String email) {
        //To check for banned Users
        mDatabase.child("admin").child("banned").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HelperMethods.errorDialog(context, "Username invalid",
                            "Username already exists");
                } else {
                    //register the new user
                    User user = new User(username, password, email);
                    addNewUser(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    /**
     * Adds a new user by first authenticating through Firebase, and then adding to the
     * users table in the DB.
     * @param user - User object to be used for insertion in Firebase DB.
     */
    private void addNewUser(User user) {
        boolean success = false;
        final String _username = user.getUsername();
        mDatabase.child("users").child(user.getUsername())
                .setValue(user);
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Constant.AUTH_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            HelperMethods.errorDialog(context, "Failed to register",
                                    "Please make sure: 1) password is at least 6 chars. long.\n" +
                                            "2) There are no illegal chars.\n" +
                                            "3) Email is not already in use.");
                        } else {
                            Intent intent = new Intent(Registration.this, MainActivity.class);
                            SaveSharedPreferences.setUserName(getApplicationContext(), _username);
                            SaveSharedPreferences.setChosenTheme(getApplicationContext(), "dark");
                            startActivity(intent);
                        }

                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
