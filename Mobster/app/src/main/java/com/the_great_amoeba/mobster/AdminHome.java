package com.the_great_amoeba.mobster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Admin home page class.
 *
 * @author Esha
 * @version 1.0
 */
public class AdminHome extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home);
    }

    /**
     * Start a new activity when ban user button is clicked
     *
     * @param view from which the method is invoked
     */
    public void onBanUsersButtonClick(View view) {
        Intent intent = new Intent(this, BanUser.class);
        startActivity(intent);
    }

    /**
     * Start a new activity when flagged question button is clicked
     *
     * @param view from which the method is invoked
     */
    public void onFlaggedQuestionsButtonClick(View view) {
        Intent intent = new Intent(this, deleteFlaggedQuestions.class);
        startActivity(intent);
    }

    /**
     * Start a new activity when log out button is clicked
     *
     * @param view from which the method is invoked
     */
    public void onLogoutButtonClick(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    /**
     * Start a new activity when statistics button is clicked
     *
     * @param view from which the method is invoked
     */
    public void onStatisticsButtonClick(View view) {
        Intent intent = new Intent(this, AdminStatistics.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            //if user pressed "yes", then he is allowed to exit from application
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
