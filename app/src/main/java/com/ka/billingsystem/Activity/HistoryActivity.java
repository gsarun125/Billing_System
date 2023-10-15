package com.ka.billingsystem.Activity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.databinding.ActivityAddProductBinding;
import com.ka.billingsystem.databinding.ActivityHistoryBinding;
import com.ka.billingsystem.databinding.ActivitySalesBinding;
import com.ka.billingsystem.model.OnPdfFileSelectListener;
import com.ka.billingsystem.model.PdfAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements OnPdfFileSelectListener {
    private ActivityHistoryBinding activityHistoryBinding;
    private List<String> mPbillno = new ArrayList();

    private List<String> mPtamount = new ArrayList();
    private List<String> mPDate = new ArrayList();
    private List<String> mPtime = new ArrayList();
    private List<String> mPusername = new ArrayList();
    private List<String> mPcusname = new ArrayList();
    private List<String> mPcusPhoneno = new ArrayList();
    private PdfAdapter pdfAdapter;
    private List<File> pdfList;

    private RecyclerView recyclerView;
    private DataBaseHandler db = new DataBaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHistoryBinding=ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(activityHistoryBinding.getRoot());
        Cursor c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC");
        displayPdf(c1);
        activityHistoryBinding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityHistoryBinding.Hbillno.getText().toString().length() !=0 & activityHistoryBinding.Hcusname.getText().toString().length() !=0){
                    Cursor c1 = db.get_value("SELECT DISTINCT * FROM ( SELECT *  FROM Transation WHERE cus_id = '"+activityHistoryBinding.Hbillno.getText().toString()+"'  GROUP BY cus_id  UNION ALL SELECT * FROM Transation GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id WHERE customer.cus_name = '"+activityHistoryBinding.Hcusname.getText().toString()+"' AND sorted.cus_id = '"+activityHistoryBinding.Hbillno.getText().toString()+"' ORDER BY sorted.time DESC");
                    displayPdf(c1);
                } else if (activityHistoryBinding.Hcusname.getText().toString().length() !=0&&activityHistoryBinding.HPhoneno.getText().toString().length() !=0) {
                    Cursor c1 = db.get_value("SELECT * FROM ( SELECT * FROM Transation GROUP BY cus_id ) AS sorted JOIN customer  ON sorted.cus_id = customer.cus_id WHERE customer.cus_name = '"+activityHistoryBinding.Hcusname.getText().toString()+"' AND customer.cus_Phone = '"+activityHistoryBinding.HPhoneno.getText().toString()+"' ORDER BY sorted.time DESC");
                    displayPdf(c1);
                } else if (activityHistoryBinding.Hbillno.getText().toString().length() !=0 ){
                Cursor c1 = db.get_value("SELECT *  FROM (SELECT * FROM Transation WHERE cus_id = '"+activityHistoryBinding.Hbillno.getText().toString()+"' GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC");
                displayPdf(c1);
                }
                else if (activityHistoryBinding.Hcusname.getText().toString().length() !=0 ){
                    Cursor c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation GROUP BY cus_id) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id WHERE customer.cus_name = '"+activityHistoryBinding.Hcusname.getText().toString()+"' ORDER BY sorted.time DESC");
                    displayPdf(c1);
                } else if (activityHistoryBinding.HPhoneno.getText().toString().length()!=0) {
                    Cursor c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation GROUP BY cus_id) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id WHERE customer.cus_Phone = '"+activityHistoryBinding.HPhoneno.getText().toString()+"' ORDER BY sorted.time DESC");
                    displayPdf(c1);
                }
            }
        });

    }
    private void displayPdf(Cursor c1){
        recyclerView=findViewById(R.id.hisrecyle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        pdfList=new ArrayList<>();
        mPbillno.clear();
        mPtamount.clear();
        mPtime.clear();
        mPDate.clear();
        mPusername.clear();
        mPcusname.clear();
        mPcusPhoneno.clear();
        pdfList.clear();

        if (c1.moveToFirst()) {
            do {
                @SuppressLint("Range") String path = c1.getString(c1.getColumnIndex("file_Path"));
                @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("Bill_No"));
                @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("tamount"));

                @SuppressLint("Range") Long data3 = c1.getLong(c1.getColumnIndex("time"));

                @SuppressLint("Range") String data4 = c1.getString(c1.getColumnIndex("sales_user"));
                @SuppressLint("Range") String data5 = c1.getString(c1.getColumnIndex("cus_name"));
                @SuppressLint("Range") String data6 = c1.getString(c1.getColumnIndex("cus_Phone"));

                File file=new File(path);
                if(file.exists()){

                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");


                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date res = new Date(data3);

                    mPbillno.add("Bill No: "+data1);
                    mPtamount.add("Total Amount: "+data2+" Rs.");
                    mPtime.add("Time:"+formatter.format(res));
                    mPDate.add("Generated Date : "+formatter1.format(res));
                    mPusername.add("Generated BY: "+data4);
                    mPcusname.add("Customer Name: "+data5);
                    mPcusPhoneno.add("Cus Phone no: "+data6);
                    pdfList.add(file);
                }
            } while (c1.moveToNext());
        }
        System.out.println(mPcusname);
        System.out.println(mPcusPhoneno);
        pdfAdapter=new PdfAdapter(this,pdfList,this,mPbillno,mPtamount,mPDate,mPusername,mPtime,mPcusname,mPcusPhoneno);
        recyclerView.setAdapter(pdfAdapter);
        activityHistoryBinding.Hbillno.setText("");
        activityHistoryBinding.Hcusname.setText("");
        activityHistoryBinding.HPhoneno.setText("");
    }


    @Override
    public void onpdfSelected(File file) {
        Intent i=new Intent(HistoryActivity.this,DocmentActivity.class);
        i.putExtra("path",file.getAbsolutePath());
        startActivity(i);
    }
}