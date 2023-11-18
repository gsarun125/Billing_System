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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.java.Export;
import com.ka.billingsystem.java.ImageEncodeAndDecode;
import com.ka.billingsystem.java.invoice1;
import com.ka.billingsystem.model.OnPdfFileSelectListener;
import com.ka.billingsystem.model.PdfAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecentInvoiceActivity extends AppCompatActivity implements OnPdfFileSelectListener {

    private DataBaseHandler db = new DataBaseHandler(this);
    String SHARED_PREFS = "shared_prefs";
    String USER_KEY = "user_key";
    String SHARED_PREFS_KEY = "signature";
    String SHARED_PREFS_Logo = "logo";
    String SPuser;

    private List<String> mPcusname = new ArrayList();
    private List<String> mPcusPhoneno = new ArrayList();
    private List<String> mPbillno = new ArrayList();
    private List<String> tempbillno = new ArrayList();
    private List<String> mPtamount = new ArrayList();
    private List<String> mPDate = new ArrayList();
    private List<String> mPtime = new ArrayList();
    private List<String> mPusername = new ArrayList();
    private List<String> image = new ArrayList();
    private ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;

    private PdfAdapter pdfAdapter;
    private List<File> pdfList;

    private RecyclerView recyclerView;
    private int scrollPosition = 0;
    private static final String TAG = "RecentInvoiceActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_invoice);
        sharedpreferences= getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SPuser = sharedpreferences.getString(USER_KEY,null);
        System.out.println(SPuser);
        // Define the initial scroll position

        recyclerView=findViewById(R.id.recycler_view);

        LinearLayout backbutton = findViewById(R.id.backbutton_Recent);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


        displayPdf();
    }


    private void displayPdf(){
        try {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            pdfList = new ArrayList<>();

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
            if (SPuser.equals("admin")) {
                c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC LIMIT 10");
            } else {
                c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation WHERE sales_user=" + "'" + SPuser + "' GROUP BY cus_id) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC LIMIT 10");
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
                    @SuppressLint("Range") String data7 = c1.getString(c1.getColumnIndex("printer_img"));

                    File file;
                    if (path == null) {
                        file = new File("/storage/emulated/0/DATA/Invoice" + data1 + ".pdf");
                    } else {
                        file = new File(path);
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");


                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date res = new Date(data3);
                    image.add(data7);
                    tempbillno.add(data1);
                    mPbillno.add("Bill No: " + data1);
                    mPtamount.add("Total Amount: " + data2 + " Rs.");
                    mPtime.add("Time:" + formatter.format(res));
                    mPDate.add("Generated Date : " + formatter1.format(res));
                    mPusername.add("Generated By: " + data4);
                    mPcusname.add("Name: " + data5);
                    mPcusPhoneno.add("Mobile no: " + data6);
                    System.out.println(file);
                    pdfList.add(file);

                } while (c1.moveToNext());
            }
            System.out.println(image);
            pdfAdapter = new PdfAdapter(this, pdfList, this, mPbillno, tempbillno, mPtamount, mPDate, mPusername, mPtime, mPcusname, mPcusPhoneno, image);
            recyclerView.setAdapter(pdfAdapter);
        }
        catch (Exception e) {
            Log.e(TAG, "Error displaying PDF", e);
        }
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

            String a="0";
             Intent i=new Intent(this,DocmentActivity.class);
             i.putExtra("billno",mPbillno);
             i.putExtra("option",a);
             startActivity(i);


        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.file_is_removed_form_internal_storage_do_you_want_to_generate_again);
            builder.setTitle(R.string.alert);
            builder.setCancelable(true);
            builder.setNegativeButton("No",(DialogInterface.OnClickListener)(dialog, which) ->{
                dialog.dismiss();
            });
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
                Intent i=new Intent(RecentInvoiceActivity.this,DocmentActivity.class);

                String a="0";
                i.putExtra("billno",mPbillno);
                i.putExtra("option",a);
                startActivity(i);

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

   */
    @Override
    protected void onResume() {
        super.onResume();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}