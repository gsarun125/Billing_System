package com.ka.billingsystem.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.ka.billingsystem.R;

import com.ka.billingsystem.DataBase.DataBaseHandler;


import com.ka.billingsystem.databinding.ActivityStockBinding;
import com.ka.billingsystem.model.NoteAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StockActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private File filePDFOutput;
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

        Refresh_Feed();

    }

    void Refresh_Feed(){
         recyclerView = findViewById(R.id.list);

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
            NoteAdapter noteAdapter = new NoteAdapter(StockActivity.this, mProduct_ID, mProduct_Name, mQuantity, mCost);
            recyclerView.setAdapter(noteAdapter);

    }

    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }

                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }

        }
        return bigBitmap;
    }
}