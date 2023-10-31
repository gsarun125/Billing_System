package com.ka.billingsystem.model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;

public class UserseclectionHolder extends RecyclerView.ViewHolder {
    public CardView container;
    TextView username;
    TextView userid;
    TextView generated_date;
    public UserseclectionHolder(@NonNull View itemView) {
        super(itemView);
        username=itemView.findViewById(R.id.Suser_name);
        userid=itemView.findViewById(R.id.Suser_id);
        generated_date=itemView.findViewById(R.id.Sgenerate_date);

    }
}
