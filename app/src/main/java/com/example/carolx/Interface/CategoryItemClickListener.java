package com.example.carolx.Interface;

import android.widget.Button;

import com.example.carolx.adapter.CategoryViewHolder;

public interface CategoryItemClickListener {
//    void itemClick(int position);
    void selectedViewHolder(Button categoryButton, int position);
    void fetchedCategoriesIndex(Button categoryButton, int index);
}
