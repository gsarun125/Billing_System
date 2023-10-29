package com.ka.billingsystem.Activity;

import static com.ka.billingsystem.Activity.SalesActivity.Bill_NO;
import static com.ka.billingsystem.Activity.SalesActivity.Customer_Name;
import static com.ka.billingsystem.Activity.SalesActivity.Net_AMT;
import static com.ka.billingsystem.Activity.SalesActivity.PHone_NO;
import static com.ka.billingsystem.Activity.SalesActivity.count;
import static com.ka.billingsystem.Activity.SalesActivity.cusEdit;
import static com.ka.billingsystem.Activity.SalesActivity.mCost;
import static com.ka.billingsystem.Activity.SalesActivity.mProduct_name;
import static com.ka.billingsystem.Activity.SalesActivity.mQty;
import static com.ka.billingsystem.Activity.SalesActivity.mTotal;
import static com.ka.billingsystem.Activity.SalesActivity.phoneEdit;
import static com.ka.billingsystem.Activity.SalesActivity.removeView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.java.invoice1;
import com.ka.billingsystem.java.invoice2;
import java.io.File;
import java.util.List;

public class PdfviewActivity extends AppCompatActivity {


    static PDFView pdfView;
    private DataBaseHandler db = new DataBaseHandler(this);
    ImageButton share;
    ImageButton textButton;
    String SHARED_PREFS_KEY = "signature";
    String SHARED_PREFS = "shared_prefs";
    String SHARED_PREFS_Logo = "logo";
    SharedPreferences sharedpreferences;
    String SPIS_FIRST_TIME;
    String SPIS_FIRST_logo;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        share = (ImageButton) findViewById(R.id.share);
        textButton = findViewById(R.id.textButton);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SPIS_FIRST_TIME = sharedpreferences.getString(SHARED_PREFS_KEY, null);
        SPIS_FIRST_logo=sharedpreferences.getString(SHARED_PREFS_Logo,null);
        fileName = "Invoice" + Bill_NO + ".pdf";
        File  dir= new File(Environment.getExternalStorageDirectory(),"DATA");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file= new File(dir,fileName);
        db.filePath(Bill_NO,file.getAbsolutePath());


        new PdfGenerationTask(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME,SPIS_FIRST_logo, file).execute();

//        File file = invoice1.PDF1(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, fileName, 0, db);
       // pdfView.fromFile(file).load();

        LinearLayout linearLayout = findViewById(R.id.backbutton);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   onBackPressed();
            }
        });


        textButton.setOnClickListener(new View.OnClickListener() {
            boolean isMethod1 = true;

            @Override
            public void onClick(View v) {
                if (isMethod1) {
                    pdfView.recycle();
                    new PdfGenerationTask2(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME,SPIS_FIRST_logo, file).execute();
                   // File file1 = invoice2.PDF2(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, fileName, db);
                  //  pdfView.fromFile(file1).load();
                } else {
                    pdfView.recycle();
                    new PdfGenerationTask(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME,SPIS_FIRST_logo, file).execute();
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
                    Toast.makeText(PdfviewActivity.this,R.string.file_not_found, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        removeView();
        cusEdit.setText("");
        phoneEdit.setText("");
        mQty.clear();
        mTotal.clear();
        mProduct_name.clear();
        mCost.clear();
        Net_AMT = 0;
        finish();
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
    private class PdfGenerationTask extends AsyncTask<Void, Void, File> {

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


        public PdfGenerationTask(int count, long netAmt, int billNo, String customerName, String phoneNo,
                                 List<String> mQty, List<Long> mCost, List<Long> mTotal, List<String> mProductName,
                                 String spIsFirstTime, String spIsFirstLogo, File file) {
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

            this.spIsFirstLogo=spIsFirstLogo;
        }
        private AlertDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = new ProgressBar(PdfviewActivity.this, null, android.R.attr.progressBarStyleLarge);
            progressBar.setIndeterminate(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            progressBar.setLayoutParams(params);

            AlertDialog.Builder builder = new AlertDialog.Builder(PdfviewActivity.this);
            builder.setView(progressBar);
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();

        }
        @Override
        protected File doInBackground(Void... voids) {
            return invoice1.PDF1(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime,spIsFirstLogo, file, 0);
        }

        @Override
        protected void onPostExecute(File result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            pdfView.fromFile(result).load();
        }
    }
    private class PdfGenerationTask2 extends AsyncTask<Void, Void, File> {

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
        private File file;
        private String spIsFirstLogo;

        private AlertDialog progressDialog;
        public PdfGenerationTask2(int count, long netAmt, int billNo, String customerName, String phoneNo,
                                 List<String> mQty, List<Long> mCost, List<Long> mTotal, List<String> mProductName,
                                 String spIsFirstTime, String spIsFirstLogo, File file) {
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
            this.spIsFirstLogo=spIsFirstLogo;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = new ProgressBar(PdfviewActivity.this, null, android.R.attr.progressBarStyleLarge);
            progressBar.setIndeterminate(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            progressBar.setLayoutParams(params);

            AlertDialog.Builder builder = new AlertDialog.Builder(PdfviewActivity.this);
            builder.setView(progressBar);
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();
        }

        @Override
        protected File doInBackground(Void... voids) {
            return invoice2.PDF2(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime,spIsFirstLogo,file);
        }

        @Override
        protected void onPostExecute(File result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            pdfView.fromFile(result).load();
        }
    }
}


