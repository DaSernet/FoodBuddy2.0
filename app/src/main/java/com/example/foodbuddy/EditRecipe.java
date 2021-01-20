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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditRecipe extends AppCompatActivity {
    Intent data;
    EditText editRecipeDescription,editRecipeName,editRecipeDuration;
    ToggleButton editRecipeFavourite;
    FirebaseFirestore fStore;
    Button editRecipeButton;
    ProgressBar editRecipeProgressBar;
    private int recipeDurationHours;
    private int recipeDurationMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fStore = fStore.getInstance();

        data = getIntent();

        editRecipeDescription = findViewById(R.id.editRecipeDescription);
        editRecipeDuration = findViewById(R.id.editRecipeDuration);
        editRecipeName = findViewById(R.id.editRecipeName);
        editRecipeFavourite = findViewById(R.id.editRecipeFavourite);
        editRecipeButton = findViewById(R.id.editRecipeButton);
        editRecipeProgressBar = findViewById(R.id.editRecipeProgressBar);




        String recipeName = data.getStringExtra("name");
        String recipeDescription = data.getStringExtra("description");
        String recipeDuration = data.getStringExtra("duration");
        Boolean recipeFavourite = data.getBooleanExtra("favourite",false);


        editRecipeDescription.setText(recipeDescription);
        editRecipeDuration.setText(recipeDuration);
        editRecipeName.setText(recipeName);
        editRecipeFavourite.setChecked(recipeFavourite);

        editRecipeDuration.setOnClickListener(v -> onClickDurationButton());

        editRecipeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String rName = editRecipeName.getText().toString();
                String rDescription = editRecipeDescription.getText().toString();
                String rDuration = editRecipeDuration.getText().toString();
                Boolean rFavourite = editRecipeFavourite.isChecked();

                if(rName.isEmpty() || rDescription.isEmpty() || rDuration.isEmpty()){
                    Toast.makeText(EditRecipe.this,"Input all fields please",Toast.LENGTH_SHORT).show();
                    return;
                }

                editRecipeProgressBar.setVisibility(View.VISIBLE);

                //update recipe in firebase
                DocumentReference docref = fStore.collection("recipes").document(data.getStringExtra("recipeId"));

                Map<String,Object> recipe = new HashMap<>();
                recipe.put("name",rName);
                recipe.put("duration",rDuration);
                recipe.put("description",rDescription);
                recipe.put("favourite",rFavourite);

                docref.update(recipe).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditRecipe.this,"An error occurred",Toast.LENGTH_SHORT).show();
                        editRecipeProgressBar.setVisibility(View.VISIBLE);
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
        String[] minutes = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
        RecipeDurationMinuteNumberPicker.setMinValue(1);
        RecipeDurationMinuteNumberPicker.setMaxValue(minutes.length);
        RecipeDurationMinuteNumberPicker.setDisplayedValues(minutes);

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
            recipeDurationMinutes = Integer.parseInt(minutes[pickedTime - 1]);
            if(recipeDurationMinutes < 10) {
                recipeDurationMinutesString = "0" + recipeDurationMinutes;
            } else {
                recipeDurationMinutesString = String.valueOf(recipeDurationMinutes);
            }
            editRecipeDuration.setText(recipeDurationHours + " : " + recipeDurationMinutesString);
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