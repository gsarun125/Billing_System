package com.mini.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import com.mini.billingsystem.DataBase.DataBaseHandler;
import com.mini.billingsystem.DataBase.DataBaseHandlerKt;
import com.mini.billingsystem.R;
import com.mini.billingsystem.databinding.ActivityStockBinding;
import com.mini.billingsystem.model.NoteAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockActivity extends DrawerBaseActivity {
    private RecyclerView recyclerView;
    private List<String> mProduct_ID = new ArrayList();
    private List<String> mProduct_Name = new ArrayList();
    private List<String> mQuantity = new ArrayList();
    private List<String> mCost = new ArrayList();
    private DataBaseHandler db = new DataBaseHandler(this);
    ActivityStockBinding Binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding= ActivityStockBinding.inflate(getLayoutInflater());
        setContentView(Binding.getRoot());
        recyclerView = findViewById(R.id.list);
        mProduct_ID.add("Product ID");
        mProduct_Name.add("Product Name");
        mQuantity.add("quantity");
        mCost.add("cost");

        Refresh_Feed();
    }
    void Refresh_Feed(){
        Cursor c1 = db.get_value("SELECT  * FROM Stock");
        if (c1.moveToFirst()) {
            do {
                @SuppressLint("Range") String data = c1.getString(c1.getColumnIndex("Product_Name"));
                @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("Product_Id"));
                @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("quantity"));
                @SuppressLint("Range") String data3 = c1.getString(c1.getColumnIndex("cost"));

                mProduct_ID.add(data1);
                mProduct_Name.add(data);
                mQuantity.add(data2);
                mCost.add(data3);

            } while (c1.moveToNext());

        }
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        NoteAdapter noteAdapter = new NoteAdapter(StockActivity.this,mProduct_ID ,mProduct_Name, mQuantity,mCost);
        recyclerView.setAdapter(noteAdapter);



    }

}