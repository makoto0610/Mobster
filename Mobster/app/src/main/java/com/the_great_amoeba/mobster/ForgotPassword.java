package com.the_great_amoeba.mobster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import Helper.HelperMethods;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailInput;
    private Button sendPassword;
    private FirebaseAuth auth;
    private ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Setup UI values
        emailInput = (EditText) findViewById(R.id.email_input);
        sendPassword = (Button) findViewById(R.id.send_password);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        // functionality for when user clicks on "forgot password" button
        sendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    HelperMethods.errorDialog(ForgotPassword.this, "Email not entered",
                            "You did not enter an email.");
                    return;
                }

                progress.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPassword.this, "Instructions to reset your password has been sent.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    HelperMethods.errorDialog(ForgotPassword.this, "Email Failed",
                                            "The instructions failed to send.");
                                }

                                progress.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }


}
