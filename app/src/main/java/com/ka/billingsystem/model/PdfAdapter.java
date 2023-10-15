package com.ka.billingsystem.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ka.billingsystem.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfViewHolder> {
private Context context;
private List<File> pdfFiles;
private OnPdfFileSelectListener listener;

private List<String> mPbillno = new ArrayList();

private List<String> mPtamount = new ArrayList();
private List<String> mPDate = new ArrayList();
private List<String> mPusername = new ArrayList();
private List<String> mPtime = new ArrayList();
private  List<String> mPcusname=new ArrayList<>();
private  List<String>mPcuspnoneno=new ArrayList<>();

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PdfViewHolder(LayoutInflater.from(context).inflate(R.layout.element_holder,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, int position) {
        holder.tvname.setText(pdfFiles.get(position).getName());
        holder.tvname.setSelected(true);
        holder.IVbillno.setText(mPbillno.get(position).toString().toUpperCase());

        holder.IVDate.setText(mPDate.get(position).toString().toUpperCase());
        holder.IVtime.setText(mPtime.get(position).toString().toUpperCase());
        holder.IVuser.setText(mPusername.get(position).toString().toUpperCase());
        holder.IVtotal.setText(mPtamount.get(position).toString().toUpperCase());
        holder.IVCusName.setText(mPcusname.get(position).toString().toUpperCase());
        holder.IVcuspno.setText(mPcuspnoneno.get(position).toString().toUpperCase());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onpdfSelected(pdfFiles.get(position));
            }
        });

    }

    public PdfAdapter(Context context, List<File> pdfFiles, OnPdfFileSelectListener listener, List<String> mPbillno, List<String> mPtamount, List<String> mPDate, List<String> mPusername, List<String> mPtime ,List<String> mPcusname,List<String> mPcuspnoneno){
        this.context = context;
        this.pdfFiles = pdfFiles;
        this.listener = listener;
        this.mPbillno = mPbillno;
        this.mPtamount = mPtamount;
        this.mPDate = mPDate;
        this.mPusername = mPusername;
        this.mPtime=mPtime;
        this.mPcusname=mPcusname;
        this.mPcuspnoneno=mPcuspnoneno;
    }

    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }

}
