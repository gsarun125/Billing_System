package com.ka.billingsystem.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;

import java.util.ArrayList;
import java.util.List;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder> {


    @NonNull
    @Override
    public UpdateAdapter.UpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.updateview,parent,false);
        return new UpdateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateAdapter.UpdateViewHolder holder, int position) {


        holder.pname.setText(mpname.get(position));
        holder.pname.setSelected(true);

        holder.Qty.setText(mqty.get(position));
        holder.Qty.setSelected(true);

        holder.cost.setText(mcost.get(position));
        holder.cost.setSelected(true);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            listenter.onClick(mpname.get(position),mqty.get(position),mcost.get(position) );
            }
        });

    }

    @Override
    public int getItemCount() {
        return mcost.toArray().length;
    }
    public  class UpdateViewHolder extends RecyclerView.ViewHolder  {
        TextView pname;
        ImageButton imageButton;
        TextView Qty;
        TextView cost;
        public UpdateViewHolder(@NonNull View itemView) {
            super(itemView);
            pname=itemView.findViewById(R.id.pname);
            Qty=itemView.findViewById(R.id.qty);
            cost=itemView.findViewById(R.id.cost);
            imageButton=itemView.findViewById(R.id.edit);
        }
    }

    public UpdateAdapter(UpdateListener listenter, Context context ,List<String> pname, List<String> qty, List<String> cost) {
        this.context=context;
        this.listenter = listenter;
        this.mpname = pname;
        this.mqty = qty;
        this.mcost = cost;
    }
    Context context;
    private UpdateListener listenter;
    List<String> mpname = new ArrayList();
    List<String> mqty = new ArrayList();
    List<String> mcost = new ArrayList();


}
