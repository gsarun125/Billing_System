package com.ka.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;

import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.ka.billingsystem.R;

import java.io.File;

public class PdfviewActivity extends AppCompatActivity {


    PDFView pdfView;

    ImageButton save;
    ImageButton share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        pdfView=(PDFView)findViewById(R.id.pdfView);
        share=(ImageButton)findViewById(R.id.share);
        save=(ImageButton)findViewById(R.id.save);
        File  dir= new File(Environment.getExternalStorageDirectory(),"DATA");

        StrictMode.VmPolicy.Builder builder= new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        Intent i=getIntent();
        File file = (File)i.getExtras().get("File");

        String filename=i.getExtras().getString("Filename");
        pdfView.fromFile(file).load();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File from = new File(dir, filename);
                File to = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),filename);
                from.renameTo(to);
                Toast.makeText(PdfviewActivity.this,"file saved",Toast.LENGTH_SHORT).show();

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputFile;
                outputFile= new File(dir, filename);
                while (!outputFile.exists()){
                     outputFile= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename);
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
}