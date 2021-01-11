package com.example.foodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "RegisterUser";
    EditText mUsername,mEmail,mPassword,mPassword2;
    Button mRegisterButton;
    TextView mLoginButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = findViewById(R.id.username);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPassword2 = findViewById(R.id.password2);
        mRegisterButton = findViewById(R.id.registerButton);
        mLoginButton = findViewById(R.id.loginText);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        //If user is already logged in send to main activity
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String password2 = mPassword2.getText().toString().trim();
                String username = mUsername.getText().toString().trim();

                if(password.length() < 6){
                    mPassword.setError("Password must be at least 6 characters");
                    Toast.makeText(Register.this, "Password too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!doStringsMatch(password,password2)){
                    mPassword.setError("Password don't match");
                    mPassword2.setError("Passwords don't match");
                    Toast.makeText(Register.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(username)){
                    mEmail.setError("Your name cannot be empty!");
                    Toast.makeText(Register.this, "Name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email cannot be empty!");
                    Toast.makeText(Register.this, "Email is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password cannot be empty!");
                    Toast.makeText(Register.this, "Password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //firebase registration
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser fUser = fAuth.getCurrentUser();
                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this,"Verification email sent", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"Email not sent" + e.getMessage());
                                }
                            });

                            Toast.makeText(Register.this, "User registered", Toast.LENGTH_SHORT).show();

                            //Get UID from current user
                            userID = fAuth.getCurrentUser().getUid();
                            //Make collection using the userID
                            DocumentReference documentReference = fStore.collection("users").document(userID);

                            //creating new user object
                            Map<String,Object> user = new HashMap<>();
                            user.put("username",username);
                            user.put("email",email);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"onSuccess: user profile is created for"+ userID);
                                }
                            });

                            startActivity(new Intent(getApplicationContext(),Login.class));
                        } else{
                            Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }

    private boolean doStringsMatch(String string1, String string2){
        return string1.equals(string2);
    }
}