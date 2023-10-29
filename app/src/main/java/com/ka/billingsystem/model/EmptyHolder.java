package com.ka.billingsystem.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;

public class EmptyHolder extends RecyclerView.ViewHolder{
    public CardView container1;
    public EmptyHolder(@NonNull View itemView) {
        super(itemView);
        container1=itemView.findViewById(R.id.container3);
    }
}
