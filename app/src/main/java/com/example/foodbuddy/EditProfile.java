package com.example.foodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "editProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent data = getIntent();
        String username = data.getStringExtra("username");
        String email = data.getStringExtra("email");

        Log.d(TAG,"onCreate: " + username + " " + email);
    }
}