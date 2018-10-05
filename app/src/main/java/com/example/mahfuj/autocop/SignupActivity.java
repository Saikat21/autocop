package com.example.mahfuj.autocop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {
    private Button sign_up_form, image;
    Context ctx;
    private EditText name, fname, mobNo, eMail, vehNo, passWord;
    private ImageView profile;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private String n, fn, mno, em, vno, imDwUrl, pass;
    private DatabaseReference refDatabase;
    private RegInfo reg;
    private static final int IMAGE_READ = 10;
    private Uri uri = null;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ctx=this;
        name = findViewById(R.id.name);
        fname = findViewById(R.id.father_name);
        mobNo = findViewById(R.id.mobile_no);
        eMail = findViewById(R.id.email_adds);
        vehNo = findViewById(R.id.veh_no);
        sign_up_form= findViewById(R.id.sign_up);
        image = findViewById(R.id.image_select);
        profile = findViewById(R.id.textView);
        passWord = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();
        refDatabase = FirebaseDatabase.getInstance().getReference("AllInfo");
        mStorageRef = FirebaseStorage.getInstance().getReference("images/");
        progressDialog = new ProgressDialog(this);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }

        });

        sign_up_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllInputData();
                signUp();
            }
        });
    }

    void getAllInputData(){
        n = name.getText().toString();
        fn = fname.getText().toString();
        mno = mobNo.getText().toString();
        em = eMail.getText().toString();
        vno = vehNo.getText().toString();
        pass = passWord.getText().toString();
    }

    void openGallery(){
        Intent pickImage = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickImage.addCategory(Intent.CATEGORY_OPENABLE);
        pickImage.setType("image/*");
        startActivityForResult(pickImage, IMAGE_READ);
    }

    void createRegInfo(){
        reg = new RegInfo(n, fn, mno, em, vno, imDwUrl);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent resData){
        if(reqCode == IMAGE_READ && resCode == Activity.RESULT_OK){
            if(resData != null){
                uri = resData.getData();
                profile.setImageURI(null);
                profile.setImageURI(uri);
            }
        }
    }

    private void signUp() {
        progressDialog.setMessage("Connecting...");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(em, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MainActivity", "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            StorageReference imgRef = mStorageRef.child(user.getEmail() + "/");
                            imgRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    imDwUrl = downloadUrl.toString();
                                    createRegInfo();
                                    refDatabase = FirebaseDatabase.getInstance().getReference();
                                    refDatabase.child(user.getUid()).setValue(reg);
                                    Intent i= new Intent(ctx, LoginActivity.class);
                                    startActivity(i);
                                    Toast.makeText(getApplicationContext(), "Registration Complete", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MainActivity", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                        progressDialog.dismiss();
                    }
                });
    }
}
