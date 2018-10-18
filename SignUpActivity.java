package com.example.mahfuj.autocop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class SignUpActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtPassword2, edtFirstName, edtLastName, edtPhn, edtReg;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private String ACC_TYPE = "Owner";
    private ProgressDialog progressDialog;
    private Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ctx = this;
        progressDialog = new ProgressDialog(this);


        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Register");


        ACC_TYPE = getIntent().getStringExtra("ACC_TYPE");


        edtEmail = findViewById(R.id.etEmail);
        edtPassword = findViewById(R.id.etPassword);
        edtPassword2 = findViewById(R.id.etPassword2);
        edtFirstName = findViewById(R.id.etFirstName);
        edtLastName = findViewById(R.id.etLastName);
        edtPhn = findViewById(R.id.etPhn);
        edtReg = findViewById(R.id.etReg);

        if (ACC_TYPE.equals("Owner"))
            edtReg.setVisibility(View.VISIBLE);
        else
            edtReg.setVisibility(View.GONE);

        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edtEmail.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();
                String pass2 = edtPassword2.getText().toString().trim();
                String fName = edtFirstName.getText().toString().trim();
                String lName = edtLastName.getText().toString().trim();
                String phn = edtPhn.getText().toString().trim();
                String reg = edtReg.getText().toString().trim();

                if (ACC_TYPE.equals("Owner")) {

                    if (!email.equals("") && !pass.equals("") && !pass2.equals("") && !fName.equals("") && !reg.equals("")&&!phn.equals("")) {

                        if (pass.equals(pass2)) {

                            signUp(email, pass, fName, lName, phn, reg);

                        } else {
                            Toast.makeText(SignUpActivity.this, "Password Didn't Match", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SignUpActivity.this, "Please fill up the required information", Toast.LENGTH_SHORT).show();
                    }

                } else {


                    if (!email.equals("") && !pass.equals("") && !pass2.equals("") && !fName.equals("")) {

                        if (pass.equals(pass2)) {

                            signUp(email, pass, fName, lName, phn, reg);

                        } else {
                            Toast.makeText(SignUpActivity.this, "Password Didn't Match", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SignUpActivity.this, "Please fill up the required information", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


    }

    private void signUp(String email, String password, final String fName, final String lName, final String phn, final String reg) {


        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("mytag", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, fName, lName, phn, reg);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w("mytag", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null, fName, lName, phn, reg);
                        }

                        // ...
                    }
                });


    }

    private void updateUI(FirebaseUser user, String fName, String lName, String phn, String reg) {

        if (user != null) {

            RegistrationInfo registrationInfo;

            if (ACC_TYPE.equals("Owner")) {


                registrationInfo = new RegistrationInfo(
                        fName, lName, phn, user.getEmail(), reg);

            } else {

                registrationInfo = new RegistrationInfo(
                        fName, lName, phn, user.getEmail());
            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference rootRef;
            rootRef = database.getReference(ACC_TYPE);

            rootRef.child(user.getUid()).setValue(registrationInfo);


            progressDialog.show();

            Toast.makeText(this, ACC_TYPE + " Registration Successful. Please Log In", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

}
