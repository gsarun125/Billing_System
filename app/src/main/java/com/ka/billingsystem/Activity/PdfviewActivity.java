package com.ka.billingsystem.Activity;

import static com.ka.billingsystem.Activity.SalesActivity.Bill_NO;
import static com.ka.billingsystem.Activity.SalesActivity.Customer_Name;
import static com.ka.billingsystem.Activity.SalesActivity.Net_AMT;
import static com.ka.billingsystem.Activity.SalesActivity.PHone_NO;
import static com.ka.billingsystem.Activity.SalesActivity.count;
import static com.ka.billingsystem.Activity.SalesActivity.mCost;
import static com.ka.billingsystem.Activity.SalesActivity.mProduct_name;
import static com.ka.billingsystem.Activity.SalesActivity.mQty;
import static com.ka.billingsystem.Activity.SalesActivity.mTotal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.java.invoice1;
import com.ka.billingsystem.java.invoice2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfviewActivity extends AppCompatActivity {


    PDFView pdfView;
    private DataBaseHandler db = new DataBaseHandler(this);
    ImageButton save;
    ImageButton share;
    Button textButton;
    String SHARED_PREFS_KEY = "signature";
    String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String SPIS_FIRST_TIME;

    String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        pdfView=(PDFView)findViewById(R.id.pdfView);
        share=(ImageButton)findViewById(R.id.share);
        save=(ImageButton)findViewById(R.id.save);
        textButton = findViewById(R.id.textButton);
        sharedpreferences= getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SPIS_FIRST_TIME=sharedpreferences.getString(SHARED_PREFS_KEY,null);

        fileName = "Invoice" + Bill_NO + ".pdf";


        File  dir= new File(Environment.getExternalStorageDirectory(),"DATA");
        StrictMode.VmPolicy.Builder builder= new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        File file =invoice1.PDF1(count,Net_AMT,Bill_NO,Customer_Name,PHone_NO,mQty,mCost,mTotal,mProduct_name,SPIS_FIRST_TIME,fileName,0,db);
        pdfView.fromFile(file).load();

        textButton.setOnClickListener(new View.OnClickListener() {
            boolean isMethod1 = true;

            @Override
            public void onClick(View v) {
                if (isMethod1) {
                    pdfView.recycle();
                    File file1 = invoice2.PDF2(count,Net_AMT,Bill_NO,Customer_Name,PHone_NO,mQty,mCost,mTotal,mProduct_name,SPIS_FIRST_TIME,fileName,db);
                    pdfView.fromFile(file1).load();
                    textButton.setText("New invoice");

                } else {

                    pdfView.recycle();
                    File file =invoice1.PDF1(count,Net_AMT,Bill_NO,Customer_Name,PHone_NO,mQty,mCost,mTotal,mProduct_name,SPIS_FIRST_TIME,fileName,0,db);
                    pdfView.fromFile(file).load();

                    textButton.setText("Classic invoice");
                }
                isMethod1 = !isMethod1;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File from = new File(dir, fileName);
                File to = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),fileName);
                from.renameTo(to);
                Toast.makeText(PdfviewActivity.this,"file saved",Toast.LENGTH_SHORT).show();

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputFile;
                outputFile= new File(dir, fileName);
                while (!outputFile.exists()){
                     outputFile= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);
                 }
                if(outputFile.exists()){
                    Uri uri = FileProvider.getUriForFile(PdfviewActivity.this, PdfviewActivity.this.getPackageName() + ".provider", outputFile);

                    Intent share = new Intent();
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.setAction(Intent.ACTION_SEND);
                    share.setAction(Intent.ACTION_SEND);
                    share.setType("application/pdf");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share"));
                }
                else {
                    Toast.makeText(PdfviewActivity.this,"file not found",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        mQty.clear();
        mTotal.clear();
        mProduct_name.clear();
        mCost.clear();
        finish();
    }
}