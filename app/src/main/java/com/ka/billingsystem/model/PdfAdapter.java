package com.ka.billingsystem.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;

import java.io.File;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<File> pdfFiles;
    private OnPdfFileSelectListener listener;

    private List<String> mPbillno;
    private List<String> mPtamount;
    private List<String> mPDate;
    private List<String> mPusername;
    private List<String> mPtime;
    private List<String> mPcusname;
    private List<String> mPcuspnoneno;
    private List<String> tempPbillno;

    public PdfAdapter(Context context, List<File> pdfFiles, OnPdfFileSelectListener listener,
                      List<String> mPbillno,List<String> tempPbillno, List<String> mPtamount, List<String> mPDate,
                      List<String> mPusername, List<String> mPtime, List<String> mPcusname,
                      List<String> mPcuspnoneno) {
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
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PdfViewHolder(LayoutInflater.from(context).inflate(R.layout.element_holder, parent, false));
    }

    @SuppressLint("NonConstantResourceId")
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
            holder.itemView.setOnClickListener(view -> {

                listener.onpdfSelected(pdfFiles.get(position),tempPbillno.get(position).toString(),pdfFiles.get(position).getName());
            });

            pdfViewHolder.overflowIcon.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(context,pdfViewHolder.overflowIcon);
                popupMenu.getMenuInflater().inflate(R.menu.menu_overflow, popupMenu.getMenu());

                // Handle menu item clicks
                popupMenu.setOnMenuItemClickListener(item -> {
                    int ch = item.getItemId();
                   if (ch==R.id.action_share)
                   {

                       listener.Share(pdfFiles.get(position));

                       return true;
                   } else if (ch==R.id.action_download) {
                       listener.Download(pdfFiles.get(position));

                   } else if (ch==R.id.action_delete)
                   {
                       listener.Delete(tempPbillno.get(position).toString());

                       return  true;
                   }

                    return false;
                });

                popupMenu.show();
            });

        } else {

            PdfViewHolder pdfViewHolder = (PdfViewHolder) holder;
            pdfViewHolder.overflowIcon.setVisibility(View.GONE);
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
