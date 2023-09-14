package com.ka.billingsystem.model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;


public class PdfViewHolder extends RecyclerView.ViewHolder {
    public TextView tvname;
    public CardView container;

    public PdfViewHolder(@NonNull View itemView) {
        super(itemView);
        tvname=itemView.findViewById(R.id.textPdfname);
        container=itemView.findViewById(R.id.container1);
    }
}
