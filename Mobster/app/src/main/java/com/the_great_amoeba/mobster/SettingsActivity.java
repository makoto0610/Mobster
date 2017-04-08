package com.the_great_amoeba.mobster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.util.Log;
import android.widget.EditText;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import Constants.Constant;
import Helper.HelperMethods;
import Objects.Comment;

/**
 * Created by natalie on 3/22/2017.
 */

public class SettingsActivity extends AppCompatActivity {


    private Button verify;
    private Button changeEmail;
    private EditText newEmail;
    private TextView textEmail;
    private ImageView verified;
    private Context context;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperMethods.setChosenTheme(this, getApplicationContext());
        setContentView(R.layout.activity_settings);

        verify = (Button) findViewById(R.id.verifyButton);
        changeEmail = (Button) findViewById(R.id.emailChangeButton);
        textEmail = (TextView) findViewById(R.id.email);
        context = this;
        newEmail = (EditText) findViewById(R.id.newEmail);
        user = FirebaseAuth.getInstance().getCurrentUser();
        verified = (ImageView) findViewById(R.id.verified);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);

        if (!user.isEmailVerified()) {
            verified.setVisibility(View.GONE);
        }


        textEmail.setText(user.getEmail());
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null ) {
                    Log.e("AUTH", user.isEmailVerified() ? "User is signed in and email is verified" : "Email is not verified");
                } else {
                    Log.e("AUTH", "onAuthStateChanged:signed_out");
                }
            }
        };

        verify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String changedEmail = newEmail.getText().toString().trim();
                if (changedEmail.length() == 0) {
                    HelperMethods.errorDialog(context, "Empty email",
                            "You cannot have an empty email");
                } else if (!changedEmail.contains("@") || !changedEmail.contains(".") || changedEmail.contains(" ")) {
                    HelperMethods.errorDialog(context, "Invalid Email",
                            "Please enter a valid email");
                } else {
                    changeEmail(user, changedEmail);
                    newEmail.setText("");
                }
            }
        });


        onClickSave();


    }

    private void onClickSave() {
        if (SaveSharedPreferences.getChosenTheme(getApplicationContext()).equals("dark")) {
            RadioButton butt = (RadioButton) findViewById(R.id.dark_theme);
            butt.setChecked(true);
        } else {
            RadioButton butt = (RadioButton) findViewById(R.id.light_theme);
            butt.setChecked(true);
        }

        ToggleButton note = (ToggleButton) findViewById(R.id.notification_toggle);
        if (SaveSharedPreferences.getNotification(getApplicationContext()).equals("on")) {
            note.setChecked(true);
        } else {
            note.setChecked(false);
        }

        RadioGroup themes = (RadioGroup) findViewById(R.id.theme_choices);
        themes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                if (checkedRadioButton.getText().equals("Dark")) {
                    SaveSharedPreferences.setChosenTheme(getApplicationContext(), "dark");
                } else {
                    SaveSharedPreferences.setChosenTheme(getApplicationContext(), "light");

                }
            }
        });

        note.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SaveSharedPreferences.setNotification(getApplicationContext(), "on");
                } else {
                    SaveSharedPreferences.setNotification(getApplicationContext(), "off");
                }
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
        Button button = (Button) findViewById(R.id.save_settings);
        button.setOnClickListener(listener);

    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void changeEmail(final FirebaseUser user, final String email) {

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(Constant.AUTH_TAG, "User email address updated.");
                            Toast.makeText(SettingsActivity.this, "Email Updated!",
                                    Toast.LENGTH_LONG).show();
                            textEmail.setText(user.getEmail());

                            String username = SaveSharedPreferences.getUserName(getApplicationContext());
                            System.out.println("username: " + username);

                            DatabaseReference updateEmail = mDatabase.child("users").child(username).child("email");
                            updateEmail.setValue(email);
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
