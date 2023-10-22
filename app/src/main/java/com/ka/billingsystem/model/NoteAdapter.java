package com.ka.billingsystem.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private List<String> product_id;
    private List<String> product_name;
    private List<String> quantity;
    private List<String> cost;

    public NoteAdapter(Context context, List<String> product_id, List<String> product_name, List<String> quantity, List<String> cost) {
        this.context = context;
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.cost = cost;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        if (product_id.isEmpty()) {
            holder.empty.setVisibility(View.VISIBLE);
            holder.productid_view.setVisibility(View.GONE);
            holder.productname_view.setVisibility(View.GONE);
            holder.quantity_view.setVisibility(View.GONE);
            holder.cost_view.setVisibility(View.GONE);
            holder.t1.setVisibility(View.GONE);
            holder.t2.setVisibility(View.GONE);
            holder.t3.setVisibility(View.GONE);
            holder.t4.setVisibility(View.GONE);

        } else {
            holder.empty.setVisibility(View.GONE);

            holder.productid_view.setText(product_id.get(position));
            holder.productname_view.setText(product_name.get(position));
            holder.quantity_view.setText(quantity.get(position));
            holder.cost_view.setText(cost.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return product_id.isEmpty() ? 1 : product_id.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView productid_view;
        TextView productname_view;
        TextView quantity_view;
        TextView cost_view;
        TextView t1;
        TextView t2;
        TextView t3;
        TextView t4;
        TextView empty;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            productid_view = itemView.findViewById(R.id.productid);
            productname_view = itemView.findViewById(R.id.productname);
            quantity_view = itemView.findViewById(R.id.quantity);
            cost_view = itemView.findViewById(R.id.cost);
            empty=itemView.findViewById(R.id.noitem);
            t1=itemView.findViewById(R.id.t1);
            t2=itemView.findViewById(R.id.t2);
            t3=itemView.findViewById(R.id.t3);
            t4=itemView.findViewById(R.id.t4);
        }
    }
}
