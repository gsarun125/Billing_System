package com.mini.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.mini.billingsystem.R;
import com.mini.billingsystem.model.OnPdfFileSelectListener;
import com.mini.billingsystem.model.PdfAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecentInvoiceActivity extends AppCompatActivity implements OnPdfFileSelectListener {

    private PdfAdapter pdfAdapter;
    private List<File> pdfList;

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_invoice);
        displayPdf();
    }

    public ArrayList<File> findPdf(File file){
        ArrayList<File> arrayList=new ArrayList<>();
        File[] files=file.listFiles();

        for (File singleFile:files){
            if(singleFile.isDirectory()&&!singleFile.isHidden()){
                arrayList.addAll(findPdf(singleFile));
            }
            else {
                if(singleFile.getName().endsWith(".pdf")){
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }
    private void displayPdf(){
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        pdfList=new ArrayList<>();
        File  dir= new File(Environment.getExternalStorageDirectory(),"DATA");
        pdfList.addAll(findPdf(dir));
        pdfAdapter=new PdfAdapter(this,pdfList,this);
        recyclerView.setAdapter(pdfAdapter);
    }

    @Override
    public void onpdfSelected(File file) {
        Intent i=new Intent(RecentInvoiceActivity.this,DocmentActivity.class);
        i.putExtra("path",file.getAbsolutePath());
        startActivity(i);
    }
}