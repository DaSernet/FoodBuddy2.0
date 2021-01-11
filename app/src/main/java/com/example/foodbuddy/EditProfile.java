package com.example.foodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "editProfile";
    EditText usernameEdit,emailEdit;
    ImageView avatarEdit;
    Button saveButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser fUser;

    //makes the back button work
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(),Profile.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();
        String username = data.getStringExtra("username");
        String email = data.getStringExtra("email");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fUser = fAuth.getCurrentUser();

        usernameEdit = findViewById(R.id.usernameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        avatarEdit = findViewById(R.id.avatarEdit);
        saveButton  = findViewById(R.id.saveButton);

        avatarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = usernameEdit.getText().toString().trim();
                String newEmail = emailEdit.getText().toString().trim();

                if(TextUtils.isEmpty(newUsername)){
                    usernameEdit.setError("Your username cannot be empty!");
                    Toast.makeText(EditProfile.this, "Name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(newEmail)){
                    emailEdit.setError("Email cannot be empty!");
                    Toast.makeText(EditProfile.this, "Email is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!doStringsMatch(email,newEmail)){
                    fUser.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DocumentReference documentReference = fStore.collection("users").document(fUser.getUid());
                            Map<String,Object> edited = new HashMap<>();
                            edited.put("email",newEmail);
                            documentReference.update(edited); //eventlistener here to debug possible errors
                            Toast.makeText(EditProfile.this, "Email has been changed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                };

                if(!doStringsMatch(username,newUsername)){
                    DocumentReference documentReference = fStore.collection("users").document(fUser.getUid());
                    Map<String,Object> edited = new HashMap<>();
                    edited.put("username",newUsername);
                    documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditProfile.this, "Username has been changed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                };

                Toast.makeText(EditProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),Profile.class));
                finish();
            }
        });

        usernameEdit.setText(username);
        emailEdit.setText(email);
    }

    private boolean doStringsMatch(String string1, String string2){
        return string1.equals(string2);
    }
}