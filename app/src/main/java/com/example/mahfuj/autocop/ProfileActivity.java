package com.example.mahfuj.autocop;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private ListView userList;
    private ArrayList<RegistrationInfo> userInfo;
    private DatabaseReference databaseReference;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userList=(ListView)findViewById(R.id.lst);
        userInfo=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("autocop");
        getAllDataFromDB();

    }

    private void getAllDataFromDB() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    RegistrationInfo value = new RegistrationInfo();
                    //value=data.getValue(RegistrationInfo.class);
                    userInfo.add(value);
                }
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                ProfileInfoAdapter profileInfoAdapter=new ProfileInfoAdapter(ProfileActivity.this,userInfo);
                userList.setAdapter(profileInfoAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
