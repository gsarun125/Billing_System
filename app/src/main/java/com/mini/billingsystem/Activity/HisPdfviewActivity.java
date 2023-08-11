package com.mini.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.mini.billingsystem.DataBase.DataBaseHandler;
import com.mini.billingsystem.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class HisPdfviewActivity extends AppCompatActivity {

    Long to_time;
    Long from_time;
    File file;

    PDFView pdfView1;
    private DataBaseHandler db = new DataBaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_pdfview);

        pdfView1=(PDFView) findViewById(R.id.pdfView2);

        Intent i=getIntent();
        to_time=i.getExtras().getLong("to_time");
        from_time=i.getExtras().getLong("from");
        System.out.println(to_time);
        System.out.println(from_time);
        Pdf();
        pdfView1.fromFile(file).load();


    }
    public void Pdf(){

        int pageWidth=1200;
        int PageHight=0;
        PdfDocument document=new PdfDocument();
        Paint myPaint=new Paint();
        Paint titlePaint=new Paint();

        Paint line=new Paint();
        line.setStrokeWidth(2f);


        PdfDocument.PageInfo myPageInfo1=new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        PdfDocument.Page page=document.startPage(myPageInfo1);
        Canvas canvas=page.getCanvas();
        Cursor c1 = null;


        canvas.drawLine(0,350,1200,350,line);


        try {
            c1 = db.get_value("SELECT * FROM Transation JOIN customer ON Transation.cus_id=customer.cus_id  WHERE Transation.time BETWEEN 1691499071635 AND 1691499109041");

        }
        catch (Exception e){
            Toast.makeText(this,"no data found",Toast.LENGTH_SHORT).show();
             return;
        }
        int temp_Bill_No = 0;
        Float Total= (float) 0;
        if (c1.moveToFirst()) {
            do {
                @SuppressLint("Range") String Bill_No = c1.getString(c1.getColumnIndex("Bill_No"));
                @SuppressLint("Range") String cus_id = c1.getString(c1.getColumnIndex("cus_id"));
                @SuppressLint("Range") String cus_name = c1.getString(c1.getColumnIndex("cus_name"));
                @SuppressLint("Range") String Product_Id = c1.getString(c1.getColumnIndex("Product_Id"));
                @SuppressLint("Range") String Product_Name = c1.getString(c1.getColumnIndex("Product_Name"));

                @SuppressLint("Range") String quantity = c1.getString(c1.getColumnIndex("quantity"));
                @SuppressLint("Range") String rate = c1.getString(c1.getColumnIndex("rate"));

                @SuppressLint("Range") String amount = c1.getString(c1.getColumnIndex("amount"));

                    if (temp_Bill_No!=Integer.parseInt(Bill_No)) {
                        if(Total!=0f) {


                            System.out.println(PageHight);
                            canvas.drawText(String.valueOf(Total), 900, PageHight , titlePaint);

                            Total = (float) 0;
                        }
                        titlePaint.setTextAlign(Paint.Align.LEFT);
                        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        titlePaint.setTextSize(40);

                        canvas.drawText(Bill_No, 100, PageHight + 230, titlePaint);

                        canvas.drawText(cus_id, 200, PageHight + 230, titlePaint);

                        canvas.drawText(cus_name, 300, PageHight + 230, titlePaint);

                        PageHight = PageHight + 130;

                        temp_Bill_No=Integer.parseInt(Bill_No);
                        System.out.println(Bill_No);
                    }



                        Total= Total+Float.valueOf(amount);
                     System.out.println(Total);
                        canvas.drawText(Product_Id, 400, PageHight + 230, titlePaint);
                        canvas.drawText(Product_Name, 500, PageHight + 230, titlePaint);

                        canvas.drawText(quantity, 600, PageHight + 230, titlePaint);
                        canvas.drawText(rate, 700, PageHight + 230, titlePaint);

                        canvas.drawText(amount, 800, PageHight + 230, titlePaint);


                        PageHight=PageHight + 70;




            } while (c1.moveToNext());

        }
        if(Total!=0f) {

            canvas.drawText(String.valueOf(Total), 900, PageHight , titlePaint);

        }






        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setTextSize(70);
        canvas.drawText("Shopping Center",pageWidth/2,270,titlePaint);

        document.finishPage(page);
        String fileName="his.pdf";
        try {
            file= new File(Environment.getExternalStorageDirectory(),fileName);
            // file= File.createTempFile(fileName, null, this.getCacheDir());
            FileOutputStream fos=new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(HisPdfviewActivity.this,"Successfully",Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}