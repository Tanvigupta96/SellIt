package com.example.carolx.adapter;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carolx.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    Button btn;

    public CategoryViewHolder(@NonNull View itemView) {

        super(itemView);
        btn = itemView.findViewById(R.id.button_cat);
    }
}
