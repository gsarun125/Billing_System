package com.bill.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.bill.billingsystem.R;

import java.io.File;

public class DocmentActivity extends AppCompatActivity {
    String filePath="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docment);
        PDFView pdfView=(PDFView) findViewById(R.id.pdfView3);
        filePath=getIntent().getStringExtra("path");
        File file=new File(filePath);
        Uri path=Uri.fromFile(file);
        pdfView.fromUri(path).load();

    }
}