package com.example.foodbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddRecipe extends AppCompatActivity {

    EditText mRecipeName,mRecipeDuration,mRecipeDescription;
    ToggleButton mFavouriteButton;
    FirebaseAuth fAuth;
    ProgressBar mRecipeProgressBar;
    FirebaseFirestore fStore;
    FirebaseUser fUser;
    Button mAddRecipeButton;
    private int recipeDurationHours;
    private int recipeDurationMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fStore = FirebaseFirestore.getInstance();
        fUser = fAuth.getInstance().getCurrentUser();

        mRecipeDuration = findViewById(R.id.addRecipeDuration);
        mAddRecipeButton = findViewById(R.id.addRecipeButton);
        mRecipeName = findViewById(R.id.addRecipeName);
        mRecipeDescription = findViewById(R.id.addRecipeDescription);
        mFavouriteButton = findViewById(R.id.addRecipeFavourite);
        mRecipeProgressBar = findViewById(R.id.addRecipeProgressBar);

        mRecipeDuration.setOnClickListener(v -> onClickDurationButton());

        mAddRecipeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String rName = mRecipeName.getText().toString();
                String rDescription = mRecipeDescription.getText().toString();
                String rDuration = mRecipeDuration.getText().toString();
                Boolean rFavourite = mFavouriteButton.isChecked();

                if(rName.isEmpty() || rDescription.isEmpty() || rDuration.isEmpty()){
                    Toast.makeText(AddRecipe.this,"Input all fields please",Toast.LENGTH_SHORT).show();
                    return;
                }

                mRecipeProgressBar.setVisibility(View.VISIBLE);

                //Save recipe to firebase
                DocumentReference docref = fStore.collection("recipes").document(fUser.getUid()).collection("myrecipes").document();
                Map<String,Object> recipe = new HashMap<>();
                recipe.put("name",rName);
                recipe.put("duration",rDuration);
                recipe.put("description",rDescription);
                recipe.put("favourite",rFavourite);

                docref.set(recipe).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddRecipe.this,"An error occurred",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    private void onClickDurationButton() {
        final ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.view_time_dialog, null);
        com.shawnlin.numberpicker.NumberPicker RecipeDurationHourNumberPicker = (com.shawnlin.numberpicker.NumberPicker) constraintLayout.findViewById(R.id.RecipeDurationHourNumberPicker);
        com.shawnlin.numberpicker.NumberPicker RecipeDurationMinuteNumberPicker = (com.shawnlin.numberpicker.NumberPicker) constraintLayout.findViewById(R.id.RecipeDurationMinuteNumberPicker);

        RecipeDurationHourNumberPicker.setValue(0);
        String[] data = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
        RecipeDurationMinuteNumberPicker.setMinValue(1);
        RecipeDurationMinuteNumberPicker.setMaxValue(data.length);
        RecipeDurationMinuteNumberPicker.setDisplayedValues(data);

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle("Total cooking duration")
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", null)
                .setView(constraintLayout)
                .setCancelable(false)
                .create();
        builder.show();

        builder.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener((View.OnClickListener) view -> {
            String recipeDurationMinutesString;
            recipeDurationHours = RecipeDurationHourNumberPicker.getValue();
            int pickedTime = RecipeDurationMinuteNumberPicker.getValue();
            recipeDurationMinutes = Integer.parseInt(data[pickedTime - 1]);
            if(recipeDurationMinutes < 10) {
                recipeDurationMinutesString = "0" + recipeDurationMinutes;
            } else {
                recipeDurationMinutesString = String.valueOf(recipeDurationMinutes);
            }
            mRecipeDuration.setText(recipeDurationHours + " : " + recipeDurationMinutesString);
            builder.dismiss();
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
}