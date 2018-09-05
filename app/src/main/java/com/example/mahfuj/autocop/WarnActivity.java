package com.example.mahfuj.autocop;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WarnActivity extends AppCompatActivity {

    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn);
        ctx = this;
        final Button btnMsg = (Button) findViewById(R.id.send_msg);
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(ctx, ViolationActivity.class);
                //startActivity(i);
                Toast.makeText(ctx,"Message sent",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }
}
