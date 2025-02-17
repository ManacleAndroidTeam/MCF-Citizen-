package com.citizen.fmc.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.citizen.fmc.R;

public class ChangePasswordSuccessfullyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_successfully);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.animation_enter_from_right,R.anim.animation_leave_out_to_left);

    }
}
