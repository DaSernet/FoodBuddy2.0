package com.example.foodbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddRecipe extends AppCompatActivity {

    EditText mRecipeName,mDuration,mIngredients,mInstructions;
    Button mRegisterButton;
    TextView mLoginButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    private int recipeDurationHours;
    private int recipeDurationMinutes;


    //makes the back button work
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDuration = findViewById(R.id.recipeDuration);
        mDuration.setOnClickListener(v -> onClickDurationButton());
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
            recipeDurationHours = RecipeDurationHourNumberPicker.getValue();
            int pickedTime = RecipeDurationMinuteNumberPicker.getValue();
            recipeDurationMinutes = Integer.parseInt(data[pickedTime - 1]);
            mDuration.setText("H " + recipeDurationHours + " : " + recipeDurationMinutes);
            builder.dismiss();
        });
    }
}