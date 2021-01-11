package com.example.foodbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    FirebaseAuth fAuth;




    //override our default actionbar with the custom one we made
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //make our new menu options clickable
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.profileButton:
                startActivity(new Intent(getApplicationContext(),Profile.class));
                finish();
                return true;
            case R.id.addRecipeButton:
                startActivity(new Intent(getApplicationContext(),AddRecipe.class));
                finish();
                return true;
            case R.id.searchRecipeButton:
                return true;
            case R.id.filterFavouriteButton:
                return true;
            case R.id.filterAllergyButton:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();

        if(fAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }

        if(!fUser.isEmailVerified()) {
            final AlertDialog.Builder verifyEmailDialog = new AlertDialog.Builder(MainActivity.this);
            verifyEmailDialog.setTitle("Verify your email");
            verifyEmailDialog.setMessage("Verify your email before logging in");
            verifyEmailDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            verifyEmailDialog.setNegativeButton("Resend code", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this,"Verification email sent", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,"Email not sent" + e.getMessage());
                        }
                    });
                }
            });
            verifyEmailDialog.show();
        }
    }
}