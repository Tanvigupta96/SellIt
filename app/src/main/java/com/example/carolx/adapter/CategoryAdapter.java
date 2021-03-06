package com.example.carolx.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carolx.Interface.CategoryItemClickListener;
import com.example.carolx.Interface.SelectedCategoryInterface;
import com.example.carolx.R;
import com.example.carolx.activity.ProfileActivity;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder>  {
    ArrayList<String> dataSources;
    Context context;
    CategoryItemClickListener listener;

    public static final String TAG = "Category_adapter";

    public CategoryAdapter(Context context,ArrayList<String> dataSource,CategoryItemClickListener listener){
        this.dataSources = dataSource;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowLayout = inflater.inflate(R.layout.row_layout,parent,false);
        return new CategoryViewHolder(rowLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, final int position) {
        holder.btn.setText(dataSources.get(position));
        listener.fetchedCategoriesIndex(holder.btn, position);
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.selectedViewHolder(holder.btn, position);
                Log.d(TAG, "Category Clicked: "+position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSources.size();
    }


}
