package com.example.mahfuj.autocop;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViolationActivity extends AppCompatActivity {

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation);
        ctx = this;
        Button btnHistory = (Button) findViewById(R.id.history);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, HistoryActivity.class);
                startActivity(i);
                //finish();
            }
        });

        Button btnTrack = (Button) findViewById(R.id.active_track);
        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, MapsActivity.class);
                startActivity(i);
                //finish();
            }
        });

        Button btnWarn = (Button) findViewById(R.id.warn_driver);
        btnWarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, WarnActivity.class);
                startActivity(i);
                //finish();
            }
        });

        Button btnSpeed = (Button) findViewById(R.id.check_speed);
        btnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, MapsActivity.class);
                startActivity(i);
                //finish();
            }
        });
    }
}
