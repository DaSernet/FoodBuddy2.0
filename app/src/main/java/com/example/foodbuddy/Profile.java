package com.example.foodbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {
    TextView username,email;
    String userId;
    Button updateProfileButton, updatePasswordButton;
    ImageView avatar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        avatar = findViewById(R.id.profileAvatar);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = fAuth.getCurrentUser().getUid();


        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(avatar);
            }
        });

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                email.setText(documentSnapshot.getString("email"));
                username.setText(documentSnapshot.getString("username"));
            }
        });


        final FirebaseUser fUser = fAuth.getCurrentUser();

        updateProfileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),EditProfile.class);
                i.putExtra("username","username");
                i.putExtra("email","email");
                startActivity(i);
            }
        });
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                        passwordResetDialog.setTitle("Password reset");
                        passwordResetDialog.setMessage("Are u sure u want to reset your password?");
                        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fAuth.sendPasswordResetEmail(String.valueOf(email.getText())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Profile.this, "Update password email sent", Toast.LENGTH_LONG).show();
                                    }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Profile.this,"Password email not sent! Error: ", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        passwordResetDialog.show();
                    }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),EditProfile.class);
                i.putExtra("username",username.getText().toString());
                i.putExtra("email",email.getText().toString());
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(View view){
        onStop();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}