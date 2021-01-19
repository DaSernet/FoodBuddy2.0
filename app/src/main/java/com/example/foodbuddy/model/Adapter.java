package com.example.foodbuddy.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodbuddy.R;
import com.example.foodbuddy.RecipeDetails;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    List<String> name;
    List<String> description;
    List<Boolean> favourite;
    List<String> duration;

    public Adapter(List<String> name,List<String> description,List<Boolean> favourite,List<String> duration) {
        this.name = name;
        this.description = description;
        this.favourite = favourite;
        this.duration = duration;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_view_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recipeNameCard.setText(name.get(position));
        holder.recipeDescriptionCard.setText(description.get(position));
        if (favourite.get(position) == true) {
            holder.recipeFavouriteCard.setVisibility(View.VISIBLE);
        } else {
            holder.recipeFavouriteCard.setVisibility(View.INVISIBLE);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), RecipeDetails.class);
                i.putExtra("name",name.get(position));
                i.putExtra("description",description.get(position));
                i.putExtra("favourite",favourite.get(position));
                i.putExtra("duration",duration.get(position));
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView recipeNameCard,recipeDescriptionCard;
        ImageView recipeFavouriteCard;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameCard = itemView.findViewById(R.id.recipeNameCard);
            recipeDescriptionCard = itemView.findViewById(R.id.recipeDescriptionCard);
            recipeFavouriteCard = itemView.findViewById(R.id.recipeFavouriteCard);
            view = itemView;
        }
    }
}
