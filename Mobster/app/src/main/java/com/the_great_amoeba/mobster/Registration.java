package com.the_great_amoeba.mobster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Registration extends AppCompatActivity {

    private StorageReference mStorageRef;
    private EditText username;
    private EditText password;
    private EditText confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        confirm = (EditText) findViewById(R.id.register_confirm_password);
    }

    public void onCreateAccountClick(View view) {
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        String confirm = this.confirm.getText().toString();
        if (!password.equals(confirm)) {
            new AlertDialog.Builder(this)
                    .setTitle("Not matching password")
                    .setMessage("Your password and confirmed password were different.")
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            this.confirm.setText("");
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
