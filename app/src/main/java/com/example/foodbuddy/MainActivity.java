package com.example.foodbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.foodbuddy.model.Recipe;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    RecyclerView recipeLists;
    FirestoreRecyclerAdapter<Recipe,RecipeViewHolder> recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        recipeLists = findViewById(R.id.recipelist);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();

        checkLoginState(fAuth);
        checkEmailVerification(fUser);

        Query query = fStore.collection("recipes").orderBy("name",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Recipe> allRecipes = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(query,Recipe.class)
                .build();

        recipeAdapter = new FirestoreRecyclerAdapter<Recipe, RecipeViewHolder>(allRecipes) {
            @Override
            protected void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int i, @NonNull Recipe recipe) {
                recipeViewHolder.recipeNameCard.setText(recipe.getName());
                recipeViewHolder.recipeDurationCard.setText(recipe.getDuration());
                String docId = recipeAdapter.getSnapshots().getSnapshot(i).getId();
                if (recipe.getFavourite()) {
                    recipeViewHolder.recipeFavouriteCard.setVisibility(View.VISIBLE);
                } else {
                    recipeViewHolder.recipeFavouriteCard.setVisibility(View.INVISIBLE);
                }

                recipeViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), RecipeDetails.class);
                        i.putExtra("recipeId",docId);
                        i.putExtra("name",recipe.getName());
                        i.putExtra("description",recipe.getDescription());
                        i.putExtra("favourite",recipe.getFavourite());
                        i.putExtra("duration",recipe.getDuration());
                        v.getContext().startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_view_layout,parent,false);
                return new RecipeViewHolder(view);
            }
        };

        recipeLists.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recipeLists.setAdapter(recipeAdapter);
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

    public class RecipeViewHolder extends RecyclerView.ViewHolder{
        TextView recipeNameCard,recipeDurationCard;
        ImageView recipeFavouriteCard;
        View view;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeNameCard = itemView.findViewById(R.id.recipeNameCard);
            recipeDurationCard = itemView.findViewById(R.id.recipeDurationCard);
            recipeFavouriteCard = itemView.findViewById(R.id.recipeFavouriteCard);
            view = itemView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        recipeAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recipeAdapter != null) {
            recipeAdapter.stopListening();
        }
    }
}