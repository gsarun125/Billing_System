package com.bill.billingsystem.model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.billingsystem.R;


public class PdfViewHolder extends RecyclerView.ViewHolder {
    public TextView tvname;
    public CardView container;
    public TextView IVbillno;
    public TextView IVDate;
    public TextView IVtime;
    public TextView IVuser;
    public TextView IVtotal;

    public PdfViewHolder(@NonNull View itemView) {
        super(itemView);
        tvname=itemView.findViewById(R.id.textPdfname);
        container=itemView.findViewById(R.id.container1);
        IVbillno=itemView.findViewById(R.id.IVBillNo);
        IVDate=itemView.findViewById(R.id.IVDate);
        IVtime=itemView.findViewById(R.id.IVTime);
        IVuser=itemView.findViewById(R.id.IVUser);
        IVtotal=itemView.findViewById(R.id.IVTotal);
    }
}
