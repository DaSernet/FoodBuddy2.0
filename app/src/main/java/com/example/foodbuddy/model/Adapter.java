package com.example.foodbuddy.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodbuddy.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    List<String> name;
    List<String> description;
    List<Boolean> favourite;

    public Adapter(List<String> name,List<String> description,List<Boolean> favourite) {
        this.name = name;
        this.description = description;
        this.favourite = favourite;
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
                Toast.makeText(v.getContext(),"Opening recipe", Toast.LENGTH_SHORT).show();
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
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameCard = itemView.findViewById(R.id.recipeNameCard);
            recipeDescriptionCard = itemView.findViewById(R.id.recipeDescriptionCard);
            recipeFavouriteCard = itemView.findViewById(R.id.recipeFavouriteCard);
            mCardView = itemView.findViewById(R.id.recipeCard);
            view = itemView;
        }
    }
}
