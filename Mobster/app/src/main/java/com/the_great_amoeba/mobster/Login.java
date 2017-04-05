package com.the_great_amoeba.mobster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import Helper.HelperMethods;
import Objects.User;

import static android.R.style.Theme;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;


    private EditText username;
    private EditText password;

    private Context context;
    private String[] choices;

    private boolean flagBanned;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_login);
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
            }
        };
        this.username = (EditText) findViewById(R.id.login_username);
        this.password = (EditText) findViewById(R.id.login_password);

        Log.d(Constant.AUTH_TAG, "IN ON_CREATE");

        // Check shared preferences, if already logged in go directly to MainActivity
        if (!(SaveSharedPreferences.getUserName(getApplicationContext()).length() == 0)) {
            Log.d(Constant.AUTH_TAG, "Username is NOT empty in shared preferences");
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        } else {
            Log.d(Constant.AUTH_TAG, "Username is empty in shared preferences");
        }
    }

    /**
     * Method called when the login button is pressed.
     * Checks if the username entered is banned from using the application. If not does validation
     *
     * @param view The current view (which is Login)
     */
    public void onLoginClick(View view) {
        final String username = this.username.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        //To check for banned Users
        mDatabase.child("admin").child("banned").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HelperMethods.errorDialog(context, "User Banned",
                            "Sorry you have been banned from using Mobster");
                } else {
                    checkLogin(username, password);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    /**
     * Checks if the user is admin
     * Handles input validation and displays appropriate error messages or if everything is fine,
     * logs the user in and starts MainActivity.
     *
     * @param username username entered
     * @param password password entered
     */
    public void checkLogin(final String username, final String password){
        if(!flagBanned && username.equals("admin") && password.equals("password")) {
            Intent intent = new Intent(Login.this, AdminHome.class);
            startActivity(intent);
        } else if(!flagBanned){


            //Find the email associated with the username (required for Firebase Auth)
            Query contain = mDatabase.child("users").orderByKey().equalTo(username);
            contain.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        HashMap value = (HashMap) dataSnapshot.getValue();
                        HashMap attributes = (HashMap) value.get(username);
                        String stored_email = (String) attributes.get("email");
                        firebaseAuth(stored_email, username, password);
                    } else {
                        //User does not exist
                        HelperMethods.errorDialog(context, "Could not log in",
                                "User does not exist.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    /**
     * Method that calls Firebase's authentication method to log a user in and
     * starts MainActivity.
     *
     * @param email that corresponds to the user
     * @param username the user's username
     * @param password the user's password
     */
    private void firebaseAuth(String email, String username, String password) {
        final String _email = email;
        final String _username = username;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Constant.AUTH_TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            HelperMethods.errorDialog(context, "Could not log in",
                                    "Invalid username/password");
                        } else {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            // Save shared preferences for persistence, save specifically the username
                            SaveSharedPreferences.setUserName(getApplicationContext(), _username);
                            startActivity(intent);
                        }

                    }
                });
    }

    /**
     * Brings the user to the Registration Activity.
     *
     * @param view The current view (which is the Login View)
     */
    public void onCreateAccountClick(View view) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
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

    public void onForgetPasswordClick(View view) {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
