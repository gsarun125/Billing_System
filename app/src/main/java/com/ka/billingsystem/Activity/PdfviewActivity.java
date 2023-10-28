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
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.java.invoice1;
import com.ka.billingsystem.java.invoice2;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PdfviewActivity extends AppCompatActivity {


    PDFView pdfView;
    private DataBaseHandler db = new DataBaseHandler(this);
    ImageButton share;
    ImageButton textButton;
    String SHARED_PREFS_KEY = "signature";
    String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String SPIS_FIRST_TIME;

    String fileName;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        share = (ImageButton) findViewById(R.id.share);
        textButton = findViewById(R.id.textButton);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SPIS_FIRST_TIME = sharedpreferences.getString(SHARED_PREFS_KEY, null);

        fileName = "Invoice" + Bill_NO + ".pdf";


        File dir = new File(Environment.getExternalStorageDirectory(), "DATA");

        File file = invoice1.PDF1(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, fileName, 0, db);
        pdfView.fromFile(file).load();

        textButton.setOnClickListener(new View.OnClickListener() {
            boolean isMethod1 = true;

            @Override
            public void onClick(View v) {
                if (isMethod1) {
                    pdfView.recycle();
                    File file1 = invoice2.PDF2(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, fileName, db);
                    pdfView.fromFile(file1).load();
                } else {
                    pdfView.recycle();
                    File file = invoice1.PDF1(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, fileName, 0, db);
                    pdfView.fromFile(file).load();
                }
                isMethod1 = !isMethod1;
            }
        });


   /*     save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(PdfviewActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Copying file...");
                progressDialog.setMax(100);
                progressDialog.show();
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File from = new File(dir, fileName);
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
                                    Toast.makeText(PdfviewActivity.this, "File saved", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(PdfviewActivity.this, "Failed to save file", Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    */


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputFile;
                outputFile = new File(dir, fileName);

                if (outputFile.exists()) {
                    Uri uri = FileProvider.getUriForFile(PdfviewActivity.this, PdfviewActivity.this.getPackageName() + ".provider", outputFile);

                    System.out.println(PdfviewActivity.this.getPackageName());
                    Intent share = new Intent();
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.setAction(Intent.ACTION_SEND);
                    share.setAction(Intent.ACTION_SEND);
                    share.setType("application/pdf");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share"));
                } else {
                    Toast.makeText(PdfviewActivity.this, "file not found", Toast.LENGTH_SHORT).show();
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
        Net_AMT = 0;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    /*private void showNotification(File savedFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(PdfviewActivity.this, getApplicationContext().getPackageName() + ".provider", savedFile);
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

}