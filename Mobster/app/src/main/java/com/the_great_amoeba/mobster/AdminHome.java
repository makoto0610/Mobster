package com.the_great_amoeba.mobster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by singh on 2/22/2017.
 */

public class AdminHome extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home);
    }

    public void onBanUsersButtonClick(View view) {
        Intent intent = new Intent(this, BanUser.class);
        startActivity(intent);
    }

    public void onFlaggedQuestionsButtonClick(View view) {
        Intent intent = new Intent(this, deleteFlaggedQuestions.class);
        startActivity(intent);
    }

    public void onLogoutButtonClick(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

}
