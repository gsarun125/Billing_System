package com.ka.billingsystem.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.java.ImageEncodeAndDecode;
import com.ka.billingsystem.java.invoice1;
import com.ka.billingsystem.model.DeleteAdapter;
import com.ka.billingsystem.model.PdfAdapter;
import com.ka.billingsystem.model.onpdfDelete;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeletedInvoice extends AppCompatActivity implements onpdfDelete {
    private DataBaseHandler db = new DataBaseHandler(this);
    private List<String> mPcusname = new ArrayList();
    private List<String> mPcusPhoneno = new ArrayList();
    private List<String> mPbillno = new ArrayList();
    private List<String> tempbillno = new ArrayList();
    private List<String> mPtamount = new ArrayList();
    private List<String> mPDate = new ArrayList();
    private List<String> mPtime = new ArrayList();
    private List<String> mPusername = new ArrayList();
    private List<String> image = new ArrayList();
    private DeleteAdapter pdfAdapter;
    private List<File> pdfList;

    private RecyclerView recyclerView;
    String SHARED_PREFS = "shared_prefs";

    String SHARED_PREFS_KEY = "signature";
    String SHARED_PREFS_Logo = "logo";
    SharedPreferences sharedpreferences;
    String SPuser;
    String USER_KEY = "user_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_invoice);
        sharedpreferences= getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SPuser = sharedpreferences.getString(USER_KEY,null);

        LinearLayout backbutton = findViewById(R.id.backbutton_Deleted);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


        displayPdf();
    }
    private void displayPdf(){
        recyclerView=findViewById(R.id.delrecycler_view);
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
        tempbillno.clear();
        image.clear();

        Cursor c1;
        c1 = db.get_value("SELECT * FROM (SELECT * FROM Deleted WHERE sales_user='" + SPuser + "' GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC");

        if (c1.moveToFirst()) {
            do {
                @SuppressLint("Range") String path = c1.getString(c1.getColumnIndex("file_Path"));
                @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("Bill_No"));
                @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("tamount"));

                @SuppressLint("Range") Long data3 = c1.getLong(c1.getColumnIndex("time"));

                @SuppressLint("Range") String data4 = c1.getString(c1.getColumnIndex("sales_user"));
                @SuppressLint("Range") String data5 = c1.getString(c1.getColumnIndex("cus_name"));
                @SuppressLint("Range") String data6 = c1.getString(c1.getColumnIndex("cus_Phone"));
                @SuppressLint("Range") String data7 = c1.getString(c1.getColumnIndex("printer_img"));

                File file;
                if(path == null) {
                    file=new File("/storage/emulated/0/DATA/Invoice"+data1+".pdf");
                }
                else {
                    file = new File(path);
                }


                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");


                SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                Date res = new Date(data3);
                image.add(data7);
                tempbillno.add(data1);
                mPbillno.add("Bill No: "+data1);
                mPtamount.add("Total Amount: "+data2+" Rs.");
                mPtime.add("Time:"+formatter.format(res));
                mPDate.add("Generated Date : "+formatter1.format(res));
                mPusername.add("Generated BY: "+data4);
                mPcusname.add("Customer Name: "+data5);
                mPcusPhoneno.add("Mobile no: "+data6);
                System.out.println(file);
                pdfList.add(file);

            } while (c1.moveToNext());
        }
        pdfAdapter=new DeleteAdapter(this,pdfList,this,mPbillno,tempbillno,mPtamount,mPDate,mPusername,mPtime,mPcusname,mPcusPhoneno,image);
        recyclerView.setAdapter(pdfAdapter);
    }

    @Override
    public void onpdfSelected(File file, String mPbillno, String filename) {
        if (file.exists()){
            String a="1";
            Intent i=new Intent(this,DocmentActivity.class);
            i.putExtra("path",file.getAbsolutePath());
            i.putExtra("billno",mPbillno);
            i.putExtra("option",a);
            startActivity(i);

        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.file_is_removed_form_internal_storage_do_you_want_to_generate_again);
            builder.setTitle(R.string.alert);
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.no,(DialogInterface.OnClickListener)(dialog, which) ->{
                dialog.dismiss();
            });
            builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
                String cusname=null;
                String phoneno=null;
                Long time=0l;
                List<String> mQty = new ArrayList();
                List<String> mProduct_name = new ArrayList();
                List<Long> mTotal = new ArrayList();
                List<Long> mCost= new ArrayList();
                int  count=0;
                long Net_AMT = 0;
                String  SPIS_FIRST_TIME = null;

                Cursor c1=db.get_value("SELECT * FROM Deleted INNER JOIN customer ON  Deleted.cus_id= customer.cus_id WHERE Bill_No ='"+mPbillno+"'");

                if (c1.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("quantity"));
                        @SuppressLint("Range") Long data2 = c1.getLong(c1.getColumnIndex("rate"));

                        @SuppressLint("Range") Long data3 = c1.getLong(c1.getColumnIndex("amount"));
                        @SuppressLint("Range") String data4 = c1.getString(c1.getColumnIndex("Product_Name"));
                        @SuppressLint("Range") String data5 = c1.getString(c1.getColumnIndex("cus_name"));
                        @SuppressLint("Range") String data6 = c1.getString(c1.getColumnIndex("cus_Phone"));
                        @SuppressLint("Range") Long data7 = c1.getLong(c1.getColumnIndex("time"));
                        @SuppressLint("Range") String data8 = c1.getString(c1.getColumnIndex("signature"));

                        mQty.add(data1);
                        mCost.add(data2);
                        mTotal.add(data3);
                        mProduct_name.add(data4);
                        if(cusname == null || phoneno == null ||time==0l){
                            cusname=data5;
                            phoneno=data6;
                            time=data7;
                            SPIS_FIRST_TIME=data8;
                        }
                        count++;
                    } while (c1.moveToNext());
                }
                for (Long total : mTotal) {

                    Net_AMT = Net_AMT + total;

                }
                String fileName = "Invoice" + mPbillno + ".pdf";

                String SPIS_FIRST_logo=sharedpreferences.getString(SHARED_PREFS_Logo,null);
                File dir = new File(this.getFilesDir(), "DATA");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file2= new File(dir,fileName);
               // File file1 = invoice1.PDF1(count,Net_AMT, Integer.parseInt(mPbillno),cusname,phoneno,mQty,mCost,mTotal,mProduct_name,SPIS_FIRST_TIME,SPIS_FIRST_logo,file2,time);
                new DeletedInvoice.PdfREGenerationTask(count,Net_AMT, Integer.parseInt(mPbillno),cusname,phoneno,mQty,mCost,mTotal,mProduct_name,SPIS_FIRST_TIME,SPIS_FIRST_logo,file2,time).execute();




            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }

    @Override
    public void image(String image) {
        Bitmap Printerimg= ImageEncodeAndDecode.decodeBase64ToBitmap(image);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.image_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        ImageView imageView = dialogView.findViewById(R.id.dialogImageView);
        imageView.setImageBitmap(Printerimg);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    /*@Override
    public void Share(File file) {
        if(file.exists()){
            Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);

            Intent share = new Intent();
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setAction(Intent.ACTION_SEND);
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share"));
        }
        else {
            Toast.makeText(this,"file not found",Toast.LENGTH_SHORT).show();
        }
    }


     */
    @Override
    public void Undo(String mPbillno) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.do_you_want_to_delete);
        builder.setTitle(R.string.alert);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.no,(DialogInterface.OnClickListener)(dialog, which) ->{
            dialog.dismiss();
        });
        builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
            db.undoMoveDataFromTable2ToTable5(mPbillno);
            displayPdf();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private class PdfREGenerationTask extends AsyncTask<Void, Void, File> {

        private int count;
        private long netAmt;
        private int billNo;
        private String customerName;
        private String phoneNo;
        private List<String> mQty;
        private List<Long> mCost;
        private List<Long> mTotal;
        private List<String> mProductName;
        private String spIsFirstTime;
        private String spIsFirstLogo;
        private File file;
        Long time;

        public PdfREGenerationTask(int count, long netAmt, int billNo, String customerName, String phoneNo,
                                   List<String> mQty, List<Long> mCost, List<Long> mTotal, List<String> mProductName,
                                   String spIsFirstTime, String spIsFirstLogo, File file,Long time) {
            this.count = count;
            this.netAmt = netAmt;
            this.billNo = billNo;
            this.customerName = customerName;
            this.phoneNo = phoneNo;
            this.mQty = mQty;
            this.mCost = mCost;
            this.mTotal = mTotal;
            this.mProductName = mProductName;
            this.spIsFirstTime = spIsFirstTime;
            this.file = file;
            this.time=time;
            this.spIsFirstLogo=spIsFirstLogo;
        }
        private android.app.AlertDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = new ProgressBar(DeletedInvoice.this, null, android.R.attr.progressBarStyleLarge);
            progressBar.setIndeterminate(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            progressBar.setLayoutParams(params);

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DeletedInvoice.this);
            builder.setView(progressBar);
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();

        }
        @Override
        protected File doInBackground(Void... voids) {
            return invoice1.PDF1(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime,spIsFirstLogo, file, time);
        }

        @Override
        protected void onPostExecute(File result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            String a="1";
            Intent i=new Intent(DeletedInvoice.this,DocmentActivity.class);
            i.putExtra("billno",billNo);
            i.putExtra("option",a);
            i.putExtra("path",result.getAbsolutePath());
            startActivity(i);

        }
    }
}