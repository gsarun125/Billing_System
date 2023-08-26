package com.mini.billingsystem.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mini.billingsystem.R;

import java.io.File;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfViewHolder> {
private Context context;
private List<File> pdfFiles;
private OnPdfFileSelectListener listener;

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PdfViewHolder(LayoutInflater.from(context).inflate(R.layout.element_holder,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, int position) {
        holder.tvname.setText(pdfFiles.get(position).getName());
        holder.tvname.setSelected(true);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onpdfSelected(pdfFiles.get(position));
            }
        });

    }

    public PdfAdapter(Context context, List<File> pdfFiles,OnPdfFileSelectListener listener) {
        this.context = context;
        this.pdfFiles = pdfFiles;
        this.listener=listener;
    }

    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }
}
