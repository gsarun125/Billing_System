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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.java.invoice1;
import com.ka.billingsystem.model.OnPdfFileSelectListener;
import com.ka.billingsystem.model.PdfAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecentInvoiceActivity extends AppCompatActivity implements OnPdfFileSelectListener {

    private DataBaseHandler db = new DataBaseHandler(this);
    String SHARED_PREFS = "shared_prefs";
    String USER_KEY = "user_key";
    String SHARED_PREFS_KEY = "signature";
    String SPuser;

    private List<String> mPcusname = new ArrayList();
    private List<String> mPcusPhoneno = new ArrayList();
    private List<String> mPbillno = new ArrayList();
    private List<String> tempbillno = new ArrayList();
    private List<String> mPtamount = new ArrayList();
    private List<String> mPDate = new ArrayList();
    private List<String> mPtime = new ArrayList();
    private List<String> mPusername = new ArrayList();
    private ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;

    private PdfAdapter pdfAdapter;
    private List<File> pdfList;

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_invoice);
        sharedpreferences= getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SPuser = sharedpreferences.getString(USER_KEY,null);
        System.out.println(SPuser);
        displayPdf();
    }

    private void displayPdf(){
        recyclerView=findViewById(R.id.recycler_view);
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

        Cursor c1;
        if (SPuser.equals("admin")) {
            c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC LIMIT 10");
        }
        else {
            c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation WHERE sales_user="+"'"+SPuser+"' GROUP BY cus_id) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC LIMIT 10");
        }

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


                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");


                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                   Date res = new Date(data3);
                    tempbillno.add(data1);
                    mPbillno.add("Bill No: "+data1);
                    mPtamount.add("Total Amount: "+data2+" Rs.");
                    mPtime.add("Time:"+formatter.format(res));
                    mPDate.add("Generated Date : "+formatter1.format(res));
                    mPusername.add("Generated BY: "+data4);
                    mPcusname.add("Customer Name: "+data5);
                    mPcusPhoneno.add("Cus Phone no: "+data6);
                    System.out.println(file);
                    pdfList.add(file);

            } while (c1.moveToNext());
        }
        pdfAdapter=new PdfAdapter(this,pdfList,this,mPbillno,tempbillno,mPtamount,mPDate,mPusername,mPtime,mPcusname,mPcusPhoneno);
        recyclerView.setAdapter(pdfAdapter);
    }

    //@Override
    //public void onpdfSelected(File file) {
      //  Intent i=new Intent(RecentInvoiceActivity.this,DocmentActivity.class);
       // i.putExtra("path",file.getAbsolutePath());
        //startActivity(i);
    //}

    @Override
    public void onpdfSelected(File file, String mPbillno, String filename) {

        if (file.exists()){

             Intent i=new Intent(this,DocmentActivity.class);
             i.putExtra("path",file.getAbsolutePath());
            startActivity(i);
        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("File is removed form Internal Storage..Do you want to generate again? ");
            builder.setTitle("Alert !");
            builder.setCancelable(true);
            builder.setNegativeButton("No",(DialogInterface.OnClickListener)(dialog, which) ->{
                dialog.dismiss();
            });
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                String cusname=null;
                String phoneno=null;
                Long time=0l;
                List<String> mQty = new ArrayList();
                List<String> mProduct_name = new ArrayList();
                List<Long> mTotal = new ArrayList();
                List<Long> mCost= new ArrayList();
                int  count=0;
                long Net_AMT = 0;
                System.out.println("not exist");
                Cursor c1=db.get_value("SELECT * FROM Transation INNER JOIN customer ON  Transation.cus_id= customer.cus_id WHERE Bill_No ='"+mPbillno+"'");

                if (c1.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("quantity"));
                        @SuppressLint("Range") Long data2 = c1.getLong(c1.getColumnIndex("rate"));

                        @SuppressLint("Range") Long data3 = c1.getLong(c1.getColumnIndex("amount"));
                        @SuppressLint("Range") String data4 = c1.getString(c1.getColumnIndex("Product_Name"));
                        @SuppressLint("Range") String data5 = c1.getString(c1.getColumnIndex("cus_name"));
                        @SuppressLint("Range") String data6 = c1.getString(c1.getColumnIndex("cus_Phone"));
                        @SuppressLint("Range") Long data7 = c1.getLong(c1.getColumnIndex("time"));

                        mQty.add(data1);
                        mCost.add(data2);
                        mTotal.add(data3);
                        mProduct_name.add(data4);
                        if(cusname == null || phoneno == null ||time==0l){
                            cusname=data5;
                            phoneno=data6;
                            time=data7;
                        }
                        count++;
                    } while (c1.moveToNext());
                }
                for (Long total : mTotal) {

                    Net_AMT = Net_AMT + total;

                }
                String fileName = "Invoice" + mPbillno + ".pdf";
                String  SPIS_FIRST_TIME=sharedpreferences.getString(SHARED_PREFS_KEY,null);
                File file1 = invoice1.PDF1(count,Net_AMT, Integer.parseInt(mPbillno),cusname,phoneno,mQty,mCost,mTotal,mProduct_name,SPIS_FIRST_TIME,fileName,time,db);
                dialog.dismiss();
                Intent i=new Intent(this,DocmentActivity.class);

                i.putExtra("path",file1.getAbsolutePath());
                startActivity(i);

            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }

    }

    @Override
    public void Share(File file) {
        if(file.exists()){
            Uri uri = FileProvider.getUriForFile(RecentInvoiceActivity.this, RecentInvoiceActivity.this.getPackageName() + ".provider", file);

            Intent share = new Intent();
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setAction(Intent.ACTION_SEND);
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share"));
        }
        else {
            Toast.makeText(RecentInvoiceActivity.this,"file not found",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void Delete(String mPbillno) {
        System.out.println(mPbillno);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to Delete..? ");
        builder.setTitle("Alert !");
        builder.setCancelable(true);
        builder.setNegativeButton("No",(DialogInterface.OnClickListener)(dialog, which) ->{
            dialog.dismiss();
        });
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            db.moveDataFromTable2ToTable5(mPbillno);
            displayPdf();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void Download(File from) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Downloding file...");
        progressDialog.setMax(100);
        progressDialog.show();
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String fileName= from.getName();
                    File to = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), fileName);

                    long fileSize = from.length();
                    long totalBytesCopied = 0;

                    try (InputStream in = new FileInputStream(from); OutputStream out = new FileOutputStream(to)) {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                            totalBytesCopied += len;
                            int progress = (int) ((totalBytesCopied * 100) / fileSize);
                            progressDialog.setProgress(progress);
                            Thread.sleep(5);
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            showNotification(to);
                            Toast.makeText(RecentInvoiceActivity.this, "File saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(RecentInvoiceActivity.this, "Failed to save file", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void showNotification(File savedFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(RecentInvoiceActivity.this, getApplicationContext().getPackageName() + ".provider", savedFile);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);

                Notification notification = new NotificationCompat.Builder(this, channelId)
                        .setContentTitle("File Downloaded")
                        .setContentText("Click to open the downloaded file")
                        .setSmallIcon(R.drawable.logo)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

                notificationManager.notify(0, notification);
            }
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("File Downloaded")
                    .setContentText("Click to open the downloaded file")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(0, builder.build());
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}