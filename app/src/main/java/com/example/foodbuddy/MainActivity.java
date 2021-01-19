package com.example.foodbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodbuddy.model.Adapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    FirebaseAuth fAuth;
    Adapter adapter;
    private RecyclerView recipeLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recipeLists = findViewById(R.id.recipelist);

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();

        checkLoginState(fAuth);
        checkEmailVerification(fUser);

        List<String> name = new ArrayList<>();
        List<String> description = new ArrayList<>();
        List<Boolean> favourite = new ArrayList<>();

        name.add("Spaghetti");
        description.add("Strings of spaghetti");
        favourite.add(true);

        name.add("Fries");
        description.add("who doesn't love fries?");
        favourite.add(false);

        /*adapter = new Adapter(name,description,favourite);
        recipeLists.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recipeLists.setAdapter(adapter);*/
    }

    private void checkLoginState(FirebaseAuth fAuth) {
        if(fAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }
    }

    private void checkEmailVerification(FirebaseUser fUser){
        if(!fUser.isEmailVerified()) {
            final AlertDialog.Builder verifyEmailDialog = new AlertDialog.Builder(MainActivity.this);
            verifyEmailDialog.setTitle("Verify your email");
            verifyEmailDialog.setMessage("Verify your email before logging in");
            verifyEmailDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
            });
            verifyEmailDialog.setNegativeButton("Resend code", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this,"Verification email sent", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(),Login.class));
                            finish();
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
}