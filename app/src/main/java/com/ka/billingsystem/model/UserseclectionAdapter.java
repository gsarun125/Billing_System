package com.ka.billingsystem.model;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;

import java.util.List;

public class UserseclectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private selectionListener listener;

    private List<String> mUsername;
    private List<String> mUserid;
    private List<String> mGen_Date;


    public UserseclectionAdapter(Context context, selectionListener listener, List<String> mUsername, List<String> mUserid, List<String> mGen_Date) {
        this.context = context;
        this.listener = listener;
        this.mUsername = mUsername;
        this.mUserid = mUserid;
        this.mGen_Date = mGen_Date;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserseclectionHolder(LayoutInflater.from(context).inflate(R.layout.user_selection_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mUsername != null && mUsername.size() > 0) {
            System.out.println("fjgjhj");
            UserseclectionHolder userseclectionHolder = (UserseclectionHolder) holder;
            userseclectionHolder.username.setText(mUsername.get(position).toString().toUpperCase());
            userseclectionHolder.userid.setText(mUserid.get(position).toString());
            userseclectionHolder.generated_date.setText(mGen_Date.get(position).toString());
            holder.itemView.setOnClickListener(view -> {
                listener.onpdfSelected(mUserid.get(position));
            });
        }else {
            UserseclectionHolder userseclectionHolder = (UserseclectionHolder) holder;
            userseclectionHolder.username.setVisibility(View.GONE);
            userseclectionHolder.userid.setVisibility(View.GONE);
            userseclectionHolder.generated_date.setVisibility(View.GONE);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setMargins(50, 300, 50, 0);
            layoutParams.height=500;
            layoutParams.width=600;
            holder.itemView.setPivotY(Gravity.CENTER_VERTICAL);
            holder.itemView.setLayoutParams(layoutParams);
            LayoutInflater.from(context).inflate(R.layout.empty_state_user_layout, ((UserseclectionHolder) holder).container, true);

        }
    }

    @Override
    public int getItemCount() {
        if (mUsername != null && mUsername.size() > 0) {
            return mUsername.size();
        } else {
            return 1; // Return 1 to show the empty state view
        }
    }
}
