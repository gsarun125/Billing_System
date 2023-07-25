package com.mini.billingsystem.Activity;



import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.mini.billingsystem.Activity.DrawerBaseActivity;
import com.mini.billingsystem.databinding.ActivitySalesBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SalesActivity extends DrawerBaseActivity {
    ActivitySalesBinding activitySalesBinding;
    int pageWidth=1200;
    String Customer_Name="ARUN G";
    String PHone_NO="9443528960";
    String Bill_NO="CA1937";
    String Date="25/07/23";

    String Time="01:29 PM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySalesBinding = ActivitySalesBinding.inflate(getLayoutInflater());
        setContentView(activitySalesBinding.getRoot());
        activitySalesBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PdfDocument document=new PdfDocument();
                Paint myPaint=new Paint();
                Paint titlePaint=new Paint();

                PdfDocument.PageInfo myPageInfo1=new PdfDocument.PageInfo.Builder(1200,2010,1).create();
                PdfDocument.Page page=document.startPage(myPageInfo1);
                Canvas canvas=page.getCanvas();

                //title
                titlePaint.setTextAlign(Paint.Align.CENTER);
                titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                titlePaint.setTextSize(70);
                canvas.drawText("Shopping Center",pageWidth/2,270,titlePaint);

                myPaint.setColor(Color.rgb(0,133,200));
                myPaint.setTextSize(30);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("Call:9443528960",1160,40,myPaint);

                titlePaint.setTextAlign(Paint.Align.CENTER);
                titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
                titlePaint.setTextSize(70);
                canvas.drawText("--------------------------------------------------",pageWidth/2,400,titlePaint);


                titlePaint.setTextAlign(Paint.Align.CENTER);
                titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
                titlePaint.setTextSize(70);
                canvas.drawText("Invoice",pageWidth/2,500,titlePaint);

                myPaint.setTextAlign(Paint.Align.LEFT);
                myPaint.setTextSize(35f);
                myPaint.setColor(Color.BLACK);
                canvas.drawText("Customer Name: "+Customer_Name,20,590,myPaint);
                canvas.drawText("Phone No: "+PHone_NO,20,650,myPaint);

                myPaint.setTextAlign(Paint.Align.LEFT);
                myPaint.setTextSize(35f);
                myPaint.setColor(Color.BLACK);
                canvas.drawText("Bill No : "+Bill_NO,pageWidth-20,590,myPaint);
                canvas.drawText("Date : "+Date,pageWidth-20,650,myPaint);


                document.finishPage(page);

                File downloadsDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String fileName="stock.pdf";
                File file=new File(downloadsDir,fileName);
                try {
                    FileOutputStream fos=new FileOutputStream(file);
                    document.writeTo(fos);
                    document.close();
                    fos.close();
                    Toast.makeText(SalesActivity.this,"Successfully",Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}