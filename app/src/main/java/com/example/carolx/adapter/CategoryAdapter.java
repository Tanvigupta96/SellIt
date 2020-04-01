package com.example.carolx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carolx.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    ArrayList<String> dataSources;
    Context context;


    public CategoryAdapter(Context context,ArrayList<String> dataSource ){
        this.dataSources = dataSource;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowLayout = inflater.inflate(R.layout.row_layout,parent,false);
        return new CategoryViewHolder(rowLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.btn.setText(dataSources.get(position));

    }

    @Override
    public int getItemCount() {
        return dataSources.size();
    }
}
