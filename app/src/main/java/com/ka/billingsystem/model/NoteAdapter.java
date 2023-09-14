package com.ka.billingsystem.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grid,parent,false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.productid_view.setText(product_id.get(position));
        holder.productid_view.setSelected(true);

        holder.productname_view.setText(product_name.get(position));
        holder.productname_view.setSelected(true);

        holder.quantity_view.setText(quantity.get(position));
        holder.quantity_view.setSelected(true);

        holder.cost_view.setText(cost.get(position));
        holder.cost_view.setSelected(true);

    }

    @Override
    public int getItemCount() {
        return quantity.toArray().length;
    }


    public  class NoteViewHolder extends RecyclerView.ViewHolder  {


        TextView productid_view;
        TextView productname_view;
        TextView quantity_view;
        TextView cost_view;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            productid_view=itemView.findViewById(R.id.productid);
            productname_view=itemView.findViewById(R.id.productname);
            quantity_view=itemView.findViewById(R.id.quantity);
            cost_view=itemView.findViewById(R.id.cost);


        }
    }
    public NoteAdapter(Context context, List<String> product_id, List<String> product_name, List<String> quantity,List<String> cost) {
        this.context = context;
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity=quantity;
        this.cost=cost;

    }

    Context context;
    List<String> product_id = new ArrayList();
    List<String> product_name = new ArrayList();
    List<String> quantity = new ArrayList();
    List<String> cost = new ArrayList();



}
