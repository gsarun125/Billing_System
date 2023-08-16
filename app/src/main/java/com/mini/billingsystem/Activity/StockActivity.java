package com.mini.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
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

import com.mini.billingsystem.DataBase.DataBaseHandler;
import com.mini.billingsystem.DataBase.DataBaseHandlerKt;
import com.mini.billingsystem.R;
import com.mini.billingsystem.databinding.ActivityStockBinding;
import com.mini.billingsystem.model.NoteAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
       // getSupportActionBar().setTitle("Stock");
        recyclerView = findViewById(R.id.list);
        mProduct_ID.add("Product ID");
        mProduct_Name.add("Product Name");
        mQuantity.add("quantity");
        mCost.add("cost");

        Refresh_Feed();
        Binding.Imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("working");
                convertXMltoPDF();

            }
        });
    }
    void convertXMltoPDF(){


        View view= LayoutInflater.from(this).inflate(R.layout.activity_stock,null);
        DisplayMetrics displayMetrics=new DisplayMetrics();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            this.getDisplay().getRealMetrics(displayMetrics);
        }
        else{
            this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels,View.MeasureSpec.EXACTLY));
        view.layout(0,0,displayMetrics.widthPixels,displayMetrics.heightPixels);


        Bitmap recycler_view_bm =     getScreenshotFromRecyclerView(recyclerView);
        PdfDocument document=new PdfDocument();
        int viewWidth=view.getMeasuredWidth();
        int viewHight=view.getMeasuredHeight();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(recycler_view_bm.getWidth(),
                recycler_view_bm.getHeight(),
                9).create();
            recycler_view_bm.prepareToDraw();
         PdfDocument.Page page=document.startPage(pageInfo);

        Canvas canvas=page.getCanvas();
        canvas.drawBitmap(recycler_view_bm,0,0,null);
        recyclerView.draw(canvas);
        document.finishPage(page);




        File downloadsDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd_MM_yyyy");
        Date day = new Date();

        String date=formatter1.format(day);
        String file1="stock"+date+".pdf";
        System.out.println(file1);
        String fileName=file1;
        File file=new File(downloadsDir,fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {

            FileOutputStream fos=new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(this,"Successfully Generated!",Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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