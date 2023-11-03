package com.ka.billingsystem.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;
import com.ka.billingsystem.java.ImageEncodeAndDecode;

import java.io.File;
import java.util.List;

public class DeleteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<File> pdfFiles;
    private onpdfDelete listener;

    private List<String> mPbillno;
    private List<String> mPtamount;
    private List<String> mPDate;
    private List<String> mPusername;
    private List<String> mPtime;
    private List<String> mPcusname;
    private List<String> mPcuspnoneno;
    private List<String> tempPbillno;
    private List<String> image;

    public DeleteAdapter(Context context, List<File> pdfFiles, onpdfDelete listener,List<String> mPbillno,List<String> tempPbillno, List<String> mPtamount, List<String> mPDate, List<String> mPusername, List<String> mPtime, List<String> mPcusname, List<String> mPcuspnoneno,List<String> image) {
        this.context = context;
        this.pdfFiles = pdfFiles;
        this.listener = listener;
        this.mPbillno = mPbillno;
        this.mPtamount = mPtamount;
        this.mPDate = mPDate;
        this.mPusername = mPusername;
        this.mPtime = mPtime;
        this.mPcusname = mPcusname;
        this.tempPbillno=tempPbillno;
        this.mPcuspnoneno = mPcuspnoneno;
        this.image=image;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PdfViewHolder(LayoutInflater.from(context).inflate(R.layout.element_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mPbillno != null && mPbillno.size() > 0) {
            // Show regular item views
            System.out.println("gfbgffgfg");
            holder.itemView.setVisibility(View.VISIBLE);
            PdfViewHolder pdfViewHolder = (PdfViewHolder) holder;
            pdfViewHolder.tvname.setText(pdfFiles.get(position).getName());
            pdfViewHolder.tvname.setSelected(true);
            pdfViewHolder.IVbillno.setText(mPbillno.get(position).toString().toUpperCase());
            pdfViewHolder.IVDate.setText(mPDate.get(position).toString().toUpperCase());
            pdfViewHolder.IVtime.setText(mPtime.get(position).toString().toUpperCase());
            pdfViewHolder.IVuser.setText(mPusername.get(position).toString().toUpperCase());
            pdfViewHolder.IVtotal.setText(mPtamount.get(position).toString().toUpperCase());
            pdfViewHolder.IVCusName.setText(mPcusname.get(position).toString().toUpperCase());
            pdfViewHolder.IVcuspno.setText(mPcuspnoneno.get(position).toString().toUpperCase());

            if(image.get(position)!=null){
                pdfViewHolder.Printerimg.setImageBitmap(ImageEncodeAndDecode.decodeBase64ToBitmap(image.get(position)));
            }
            else {

            }
            pdfViewHolder.Printerimg.setOnClickListener(view -> {
                listener.image(image.get(position));
            });

            holder.itemView.setOnClickListener(view -> {

                listener.onpdfSelected(pdfFiles.get(position),tempPbillno.get(position).toString(),pdfFiles.get(position).getName());
            });



        } else {

            PdfViewHolder pdfViewHolder = (PdfViewHolder) holder;

            pdfViewHolder.Printerimg.setVisibility(View.GONE);
            pdfViewHolder.IVbillno.setVisibility(View.GONE);
            pdfViewHolder.IVDate.setVisibility(View.GONE);
            pdfViewHolder.IVtime.setVisibility(View.GONE);
            pdfViewHolder.IVuser.setVisibility(View.GONE);
            pdfViewHolder.IVtotal.setVisibility(View.GONE);
            pdfViewHolder.IVCusName.setVisibility(View.GONE);
            pdfViewHolder.IVcuspno.setVisibility(View.GONE);
            pdfViewHolder.tvname.setVisibility(View.GONE);


            holder.itemView.setVisibility(View.VISIBLE);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setMargins(0, 300, 0, 0);
            layoutParams.height=600;

            holder.itemView.setLayoutParams(layoutParams);
            LayoutInflater.from(context).inflate(R.layout.empty_state_layout, ((PdfViewHolder) holder).container, true);
        }
    }
    @Override
    public int getItemCount() {
        if (mPbillno != null && mPbillno.size() > 0) {
            return mPbillno.size();
        } else {
            return 1; // Return 1 to show the empty state view
        }
    }
}
