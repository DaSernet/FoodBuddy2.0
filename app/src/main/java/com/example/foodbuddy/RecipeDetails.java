package com.example.foodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class RecipeDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Intent data = getIntent();

        TextView description = findViewById(R.id.recipeDetailsDescription);
        TextView name = findViewById(R.id.recipeDetailsName);
        TextView duration = findViewById(R.id.recipeDetailsDuration);
        ImageView favourite = findViewById(R.id.recipeDetailsFavourite);

        description.setMovementMethod(new ScrollingMovementMethod());

        description.setText(data.getStringExtra("description"));
        name.setText(data.getStringExtra("name"));
        duration.setText("  " + data.getStringExtra("duration" ) + "  ");
        if (data.getBooleanExtra("favourite", false)) {
            favourite.setVisibility(View.VISIBLE);
        } else {
            favourite.setVisibility(View.INVISIBLE);
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
}