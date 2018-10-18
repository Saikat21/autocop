package com.example.mahfuj.autocop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ListView memList;

    FirebaseDatabase database;
    DatabaseReference ref;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    profile_info member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        memList = findViewById(R.id.memList);
        member = new profile_info();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users");
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.user_info, R.id.userInfo, list);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    member = ds.getValue(profile_info.class);
                    list.add(member.getEtEmail().toString()
                            + "\n" + member.getEtFirstName().toString()
                            + "\n" + member.getEtLastName().toString()
                            + "\n" + member.getEtPhn().toString());
                }
                memList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(ProfileActivity.this, "Data Error", Toast.LENGTH_SHORT).show();

            }
        });

        memList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final String item = list.get(position);

                Toast.makeText(ProfileActivity.this, "The clicked item is : " + "\n" + item, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
