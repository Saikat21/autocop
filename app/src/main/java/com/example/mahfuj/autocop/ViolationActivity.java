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
        final Button btnHistory = (Button) findViewById(R.id.history);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, HistoryActivity.class);
                startActivity(i);
                //finish();
            }
        });

        final Button btnTrack = (Button) findViewById(R.id.active_track);
        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, MapsActivity.class);
                startActivity(i);
                //finish();
            }
        });
    }
}
