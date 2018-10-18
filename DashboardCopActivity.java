package com.example.mahfuj.autocop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class DashboardCopActivity extends AppCompatActivity {

    Button active_track;
    Button log_out;
    Button history;
    Button profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        log_out=findViewById(R.id.btn_logout);

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences SM = getSharedPreferences("userrecord", 0);
                SharedPreferences.Editor edit = SM.edit();
                edit.putBoolean("userlogin", false);
                edit.commit();

                Intent intent = new Intent(DashboardCopActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();

            }
        });

        history=findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardCopActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        profile=findViewById(R.id.btn_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardCopActivity.this, ProfileActivity.class));
            }
        });


        active_track = findViewById(R.id.active_track);
        active_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(DashboardCopActivity.this, MapsCopActivity.class));

            }
        });
    }
}
