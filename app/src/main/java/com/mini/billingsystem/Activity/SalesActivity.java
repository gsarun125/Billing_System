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
    float net_amount=180.0f;

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
                canvas.drawText("Bill No : "+Bill_NO,pageWidth-350,590,myPaint);
                canvas.drawText("Date : "+Date,pageWidth-350,650,myPaint);
                canvas.drawText("Time : "+Time,pageWidth-350,710,myPaint);

                canvas.drawText("--------------------------------------------------",pageWidth/2,780,titlePaint);

                //box
                myPaint.setStyle(Paint.Style.STROKE);
                myPaint.setStrokeWidth(2);
                canvas.drawRect(20,800,pageWidth-20,860,myPaint);

                myPaint.setTextAlign(Paint.Align.LEFT);
                myPaint.setStyle(Paint.Style.FILL);
                canvas.drawText("Sl.No.",pageWidth-1168,850,myPaint);
                canvas.drawText("Pid",pageWidth-1000,850,myPaint);
                canvas.drawText("Particulars",pageWidth-792,850,myPaint);
                canvas.drawText("Qty",pageWidth-500,850,myPaint);
                canvas.drawText("Rate",pageWidth-376,850,myPaint);
                canvas.drawText("Amount",pageWidth-198,850,myPaint);

                canvas.drawLine(pageWidth-1168+120,800,pageWidth-1168+120,860,myPaint);
                canvas.drawLine(pageWidth-970+100,800,pageWidth-970+100,860,myPaint);
                canvas.drawLine(pageWidth-792+248,800,pageWidth-792+248,860,myPaint);
                canvas.drawLine(pageWidth-500+100,800,pageWidth-500+100,860,myPaint);
                canvas.drawLine(pageWidth-376+148,800,pageWidth-376+148,860,myPaint);

                int start_item1=50;
                int start_item2=200;
                int start_item3=400;
                int start_item4=700;
                int start_item5=850;
                int start_item6=1000;

                int end_item=950;
                //product
                //if(itemspiner.getSelectedItemPosition()!=0)
                //{
               // max 12
                int i=12;
                 while (i>0){
                     canvas.drawText("1", start_item1, end_item, myPaint);
                     canvas.drawText("18", start_item2, end_item, myPaint);
                     canvas.drawText("APPLE", start_item3, end_item, myPaint);
                     canvas.drawText("6", start_item4, end_item, myPaint);
                     canvas.drawText("29", start_item5, end_item, myPaint);
                     canvas.drawText("174", start_item6, end_item, myPaint);
                     end_item = end_item + 70;
                     System.out.println(i);
                     i=i-1;
                 }
                // }

                canvas.drawLine(680,end_item,pageWidth-20,end_item,myPaint);
                 end_item=end_item+50;

                 canvas.drawText("Net Amt",730,end_item,myPaint);
                 canvas.drawText(":",970,end_item,myPaint);
                 canvas.drawText(""+net_amount,1000,end_item,myPaint);
                 end_item=end_item+30;
                 canvas.drawLine(680,end_item,pageWidth-20,end_item,myPaint);

                 end_item=end_item+70;
                titlePaint.setTextSize(30);
                canvas.drawText("**THANK YOUR VISIT**",pageWidth/2,end_item,titlePaint);



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