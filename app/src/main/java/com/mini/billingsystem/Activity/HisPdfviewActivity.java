package com.mini.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
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
import com.mini.billingsystem.DataBase.DataBaseHandler;
import com.mini.billingsystem.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HisPdfviewActivity extends AppCompatActivity {

    Long to_time;
    Long from_time;
    File file;

    PDFView pdfView1;
    ImageButton filter;
    ImageButton save;
    ImageButton share;
    private DataBaseHandler db = new DataBaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_pdfview);

        share=(ImageButton)findViewById(R.id.hisshare);
        save=(ImageButton)findViewById(R.id.hissave);

        pdfView1=(PDFView) findViewById(R.id.pdfView2);
        filter=(ImageButton)findViewById(R.id.filter);


        StrictMode.VmPolicy.Builder builder= new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File from = new File(Environment.getExternalStorageDirectory(), "his.pdf");
                File to = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),"Bill.pdf");
                from.renameTo(to);
                Toast.makeText(HisPdfviewActivity.this,"file saved",Toast.LENGTH_SHORT).show();

            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputFile;

                outputFile= new File(Environment.getExternalStorageDirectory(), "his.pdf");

                while (!outputFile.exists()){
                    outputFile= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Bill.pdf");
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


        Intent i=getIntent();
        to_time=i.getExtras().getLong("to_time");
        from_time=i.getExtras().getLong("from");
        System.out.println(to_time);
        System.out.println(from_time);

        String query="SELECT * FROM Transation JOIN customer ON Transation.cus_id=customer.cus_id  WHERE Transation.time BETWEEN "+from_time+ " AND " +to_time ;

        Pdf(query);



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

        int pageWidth=2480;
        int PageHight=0;
        PdfDocument document=new PdfDocument();
        Paint myPaint=new Paint();
        Paint titlePaint=new Paint();

        Paint line=new Paint();
        line.setStrokeWidth(2f);


        PdfDocument.PageInfo myPageInfo1=new PdfDocument.PageInfo.Builder(2480 ,3508,1).create();
        PdfDocument.Page page=document.startPage(myPageInfo1);
        Canvas canvas=page.getCanvas();
        Cursor c1 = null;


        canvas.drawLine(0,350,1200,350,line);
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
                    if (temp_Bill_No!=Integer.parseInt(Bill_No)) {
                        if(Total!=0f) {


                            System.out.println(PageHight);
                            canvas.drawText(String.valueOf(Total), 2200,PageHight+300 , titlePaint);

                            Total = (float) 0;
                        }
                        titlePaint.setTextAlign(Paint.Align.LEFT);
                        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        titlePaint.setTextSize(60);

                        canvas.drawText(Bill_No, 100, PageHight + 300, titlePaint);
                        canvas.drawText(dateString, 150, PageHight +300, titlePaint);

                        canvas.drawText(cus_id, 500, PageHight + 300, titlePaint);

                        canvas.drawText(cus_name, 600, PageHight + 300, titlePaint);

                        PageHight = PageHight + 130;

                        temp_Bill_No=Integer.parseInt(Bill_No);
                        System.out.println(Bill_No);
                    }



                        Total= Total+Float.valueOf(amount);
                     System.out.println(Total);
                        canvas.drawText(Product_Id, 900, PageHight + 300, titlePaint);
                        canvas.drawText(Product_Name, 1200, PageHight + 300, titlePaint);

                        canvas.drawText(quantity, 1500, PageHight + 300, titlePaint);
                        canvas.drawText(rate, 1800, PageHight + 300, titlePaint);

                        canvas.drawText(amount, 2100, PageHight + 300, titlePaint);

                        amt=amt+230+70;

                        PageHight=PageHight + 150;




            } while (c1.moveToNext());

        }
        if(Total!=0f) {

            canvas.drawText(String.valueOf(Total), 2200, PageHight+300 , titlePaint);

        }






        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setTextSize(70);
        canvas.drawText("Shopping Center",pageWidth/2,80,titlePaint);

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

        pdfView1.fromFile(file).load();

    }

}