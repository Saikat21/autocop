package com.example.mahfuj.autocop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    private TextView txtSignUp,tv;
    private EditText edtPass, edtEmail;
    private Button btnLogIn;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        /*tv=findViewById(R.id.scroll_txt);
        tv.setSingleLine();
        tv.isSelected();
        tv.setText("Start Early, Drive Slowly, Reach Safely");
*/


        ctx = this;
        progressDialog=new ProgressDialog(this);

        txtSignUp = findViewById(R.id.tvSignUp);
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(SignInActivity.this);
                builderSingle.setIcon(R.drawable.cop96);
                builderSingle.setTitle("Account Category");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        SignInActivity.this, android.R.layout.select_dialog_singlechoice);

                arrayAdapter.add("Owner");
                arrayAdapter.add("Cop");

                builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);

                        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                        intent.putExtra("ACC_TYPE", strName);
                        startActivity(intent);

                    }
                });
                builderSingle.show();


            }
        });

        edtEmail = findViewById(R.id.etEmail);
        edtPass = findViewById(R.id.etPass);

        btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edtEmail.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();

                if (!email.equals("") && !pass.equals("")) {

                   progressDialog.setMessage("Please wait...");
                   progressDialog.show();
                   signIn(email, pass);

                } else {
                    Toast.makeText(SignInActivity.this, "Please fill up both fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void signIn(String email, String password) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("mytag", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("mytag", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private void updateUI(final FirebaseUser user) {

        if (user == null) {
            progressDialog.dismiss();

            Toast.makeText(this, "Unable To Log In...", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("logging in...");
            progressDialog.show();

            // Check Acc info
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ownerRef = rootRef.child("Owner");
            final DatabaseReference copRef = rootRef.child("Cop");


            ownerRef.orderByKey().equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){

                        Log.w("mytag", "OWNER ACC TYPE " + dataSnapshot.getValue() + " EMAIL " + user.getEmail());

                        //((AutoCop)getApplication()).setACC_TYPE("Owner");

                        progressDialog.dismiss();


                        Toast.makeText(ctx, "Logged In", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                        //finish();

                    }else {

                        copRef.orderByKey().equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){

                                    Log.w("mytag", "COP ACC TYPE " + dataSnapshot.getValue() + " EMAIL " + user.getEmail());


                                    progressDialog.dismiss();


                                    Toast.makeText(ctx, "Logged In", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignInActivity.this, DashboardCopActivity.class));
                                    //finish();

                                }else {

                                    progressDialog.dismiss();

                                    Toast.makeText(ctx, "Unable To Fetch Account Info", Toast.LENGTH_SHORT).show();
                                    //SignInActivity.this.finish();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                Toast.makeText(ctx, "Unable To Fetch Account Info", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(ctx, "Unable To Fetch Account Info", Toast.LENGTH_SHORT).show();
                }
            });





        }


    }



}
