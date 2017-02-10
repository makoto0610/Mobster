package com.the_great_amoeba.mobster;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

import Helper.HelperMethods;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/users";
    private DatabaseReference mDatabase;

    private static final String AUTH_TAG = "AUTH";
    private static final String AUTH_FAIL = "AUTH_FAILED";

    private EditText username;
    private EditText password;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(DB_URL);

        mAuth = FirebaseAuth.getInstance();
        // [START auth_state_listener] - Firebase Auth State Listener that listens to changes
        // in a user's logged in status
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(AUTH_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(AUTH_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        this.username = (EditText) findViewById(R.id.login_username);
        this.password = (EditText) findViewById(R.id.login_password);

        Log.d(AUTH_TAG, "IN ON_CREATE");

        // Check shared preferences, if already logged in go directly to MainActivity
        if (!(SaveSharedPreferences.getUserName(getApplicationContext()).length() == 0)) {
            Log.d(AUTH_TAG, "Username is NOT empty in shared preferences");
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        } else {
            Log.d(AUTH_TAG, "Username is empty in shared preferences");
        }
    }

    /**
     * Method called when the login button is pressed.
     * Handles input validation and displays appropriate error messages or if everything is fine,
     * logs the user in and starts MainActivity.
     *
     * @param view The current view (which is Login)
     */
    public void onLoginClick(View view) {
        final String username = this.username.getText().toString().trim();
        final String password = this.password.getText().toString().trim();


        //Find the email associated with the username (required for Firebase Auth)
        Query contain = mDatabase.orderByKey().equalTo(username);
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
                        Log.d(AUTH_TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

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

}
