package com.ka.billingsystem.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.ka.billingsystem.R;
import com.ka.billingsystem.DataBase.DataBaseHandler;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HisPdfviewActivity extends AppCompatActivity {

    Long to_time;
    Long from_time;
    File file;
    String hisFrom;
    String hisTo;

    PDFView pdfView1;
    ImageButton filter;
    ImageButton save;
    ImageButton share;
    String  fileName;
    private DataBaseHandler db = new DataBaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_pdfview);

        share=(ImageButton)findViewById(R.id.hisshare);
        save=(ImageButton)findViewById(R.id.hissave);

        pdfView1=(PDFView) findViewById(R.id.pdfView2);
        filter=(ImageButton)findViewById(R.id.filter);

        Intent i=getIntent();
        to_time=i.getExtras().getLong("to_time");
        from_time=i.getExtras().getLong("from");
        DateFormat obj = new SimpleDateFormat("dd MMM yyyy");
        Date res = new Date(from_time);
        Date res1=new Date(to_time);
        hisFrom=obj.format(res);
        hisTo=obj.format(res1);
       fileName=hisFrom+" TO "+hisTo+".pdf";

        String query="SELECT * FROM Transation JOIN customer ON Transation.cus_id=customer.cus_id  WHERE Transation.time BETWEEN "+from_time+ " AND " +to_time ;

        Pdf(query);



        StrictMode.VmPolicy.Builder builder= new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File  dir= new File(Environment.getExternalStorageDirectory(),"DATA");
                File  subdir= new File(dir,"HISTORYS");

                File from = new File(subdir, fileName);
                File to = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),fileName);
                from.renameTo(to);
                Toast.makeText(HisPdfviewActivity.this,"file saved",Toast.LENGTH_SHORT).show();

            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputFile;

                File  dir= new File(Environment.getExternalStorageDirectory(),"DATA");
                File  subdir= new File(dir,"HISTORYS");

                outputFile= new File(subdir, fileName);

                while (!outputFile.exists()){
                    outputFile= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);
                }
                if(outputFile.exists()){
                    Uri uri = FileProvider.getUriForFile(HisPdfviewActivity.this, HisPdfviewActivity.this.getPackageName() + ".provider", outputFile);

                    Intent share = new Intent();
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.setAction(Intent.ACTION_SEND);
                    share.setAction(Intent.ACTION_SEND);
                    share.setType("application/pdf");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share"));
                }
                else {
                    Toast.makeText(HisPdfviewActivity.this,"file not found",Toast.LENGTH_SHORT).show();
                }
            }
        });


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked");
                PopupMenu popupMenu=new PopupMenu(HisPdfviewActivity.this,view);
                popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int ch=menuItem.getItemId();
                        if(ch==R.id.date)
                        {
                            System.out.println("date");
                            String query="SELECT * FROM Transation JOIN customer ON Transation.cus_id=customer.cus_id  WHERE Transation.time BETWEEN "+from_time+ " AND " +to_time+" ORDER BY time DESC" ;
                            Pdf(query);

                        }
                        else if (ch==R.id.billno) {
                            String query="SELECT * FROM Transation JOIN customer ON Transation.cus_id=customer.cus_id  WHERE Transation.time BETWEEN "+from_time+ " AND " +to_time+" ORDER BY Bill_No ASC" ;
                            Pdf(query);
                        }
                        else if (ch==R.id.menucus) {

                            String query="SELECT * FROM Transation JOIN customer ON Transation.cus_id=customer.cus_id  WHERE Transation.time BETWEEN "+from_time+ " AND " +to_time+" ORDER BY cus_name ASC" ;
                            Pdf(query);

                        }
                        else if (ch==R.id.lowpur) {
                            String query="SELECT * FROM Transation JOIN customer ON Transation.cus_id=customer.cus_id  WHERE Transation.time BETWEEN "+from_time+ " AND " +to_time+" ORDER BY tamount ASC" ;
                            Pdf(query);

                        }
                        else if (ch==R.id.highpar) {
                            String query="SELECT * FROM Transation JOIN customer ON Transation.cus_id=customer.cus_id  WHERE Transation.time BETWEEN "+from_time+ " AND " +to_time+" ORDER BY tamount DESC" ;
                            Pdf(query);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });



    }
    public void Pdf(String query){

        int pageWidth=0;
        int PageHight=0;
        int linePageHight=200;
        PdfDocument document=new PdfDocument();
        Paint myPaint=new Paint();
        Paint titlePaint=new Paint();

        Paint line=new Paint();
        line.setStrokeWidth(2f);


        PdfDocument.PageInfo myPageInfo1=new PdfDocument.PageInfo.Builder(2480 ,3508,1).create();
        PdfDocument.Page page=document.startPage(myPageInfo1);
        Canvas canvas=page.getCanvas();
        Cursor c1 = null;
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setTextSize(70);
        canvas.drawText("Shopping Center",2480/2,50,titlePaint);

        Canvas cavaDrawline=page.getCanvas();
        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(60);

        canvas.drawText( "Bill No",0,200,titlePaint);
        canvas.drawText( "Date",250,200,titlePaint);
        canvas.drawText( "Cus id",450,200,titlePaint);
        canvas.drawText( "Name",550,200,titlePaint);
        canvas.drawText( "Product Id",750,200,titlePaint);
        canvas.drawText( "Product Name",1100,200,titlePaint);
        canvas.drawText( "QTY",1900,200,titlePaint);
        canvas.drawText( "Cost",2050,200,titlePaint);
        canvas.drawText( "Amount",2250,200,titlePaint);

        cavaDrawline.drawLine(0,200,2480,200,line);
        System.out.println(query);
        try {
            c1 = db.get_value(query);

        }
        catch (Exception e){
            Toast.makeText(this,"no data found",Toast.LENGTH_SHORT).show();
             return;
        }
        int temp_Bill_No = 0;
        int amt=0;
        Float Total= (float) 0;
        if (c1.getCount()>0) {
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
                    @SuppressLint("Range") Long time = c1.getLong(c1.getColumnIndex("time"));

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String dateString = formatter.format(new Date(time));
                    System.out.println(dateString);
                    if (temp_Bill_No != Integer.parseInt(Bill_No)) {
                        if (Total != 0f) {

                            cavaDrawline.drawLine(2230, linePageHight, 2230, PageHight - 100, line);
                            cavaDrawline.drawLine(2000, linePageHight, 2000, PageHight + 100, line);

                            linePageHight = PageHight + 100;

                            System.out.println(PageHight);
                            cavaDrawline.drawLine(2000, PageHight - 100, 2480, PageHight - 100, line);

                            canvas.drawText("Total:" + String.valueOf(Total), 2100, PageHight, titlePaint);
                            cavaDrawline.drawLine(0, PageHight + 100, 2480, PageHight + 100, line);

                            Total = (float) 0;

                        }
                        PageHight = PageHight + 300;


                        canvas.drawText(Bill_No, 100, PageHight, titlePaint);
                        canvas.drawText(dateString, 150, PageHight, titlePaint);

                        canvas.drawText(cus_id, 500, PageHight, titlePaint);

                        canvas.drawText(cus_name, 600, PageHight, titlePaint);

                        PageHight = PageHight + 130;

                        temp_Bill_No = Integer.parseInt(Bill_No);
                        System.out.println(Bill_No);
                    }


                    Total = Total + Float.valueOf(amount);
                    System.out.println(Total);
                    canvas.drawText(Product_Id, 900, PageHight, titlePaint);
                    canvas.drawText(Product_Name, 1200, PageHight, titlePaint);

                    canvas.drawText(quantity, 1900, PageHight, titlePaint);
                    canvas.drawText(rate, 2050, PageHight, titlePaint);

                    canvas.drawText(amount, 2300, PageHight, titlePaint);

                    amt = amt + 230 + 70;

                    PageHight = PageHight + 150;


                } while (c1.moveToNext());

            }
            if (Total != 0f) {

                //cavaDrawline.drawLine(2000,linePageHight-PageHight,2000,PageHight-100,line);
                //linePageHight=linePageHight+PageHight-100;


                cavaDrawline.drawLine(2000, PageHight - 100, 2480, PageHight - 100, line);

                canvas.drawText("Total:" + String.valueOf(Total), 2100, PageHight, titlePaint);

                cavaDrawline.drawLine(0, PageHight + 100, 2480, PageHight + 100, line);


            }

            document.finishPage(page);
            try {
                File dir = new File(Environment.getExternalStorageDirectory(), "DATA");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File subdir = new File(dir, "HISTORYS");
                if (!subdir.exists()) {
                    subdir.mkdir();
                }

                file = new File(subdir, fileName);
                // file= File.createTempFile(fileName, null, this.getCacheDir());
                FileOutputStream fos = new FileOutputStream(file);
                document.writeTo(fos);
                document.close();
                fos.close();
                Toast.makeText(HisPdfviewActivity.this, "Successfully", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            pdfView1.fromFile(file).load();
        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("you have no history");
            builder.setTitle("Alert...!");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

}