package com.ka.billingsystem.Activity;

import static com.ka.billingsystem.Activity.SalesActivity.Bill_NO;
import static com.ka.billingsystem.Activity.SalesActivity.Customer_Name;
import static com.ka.billingsystem.Activity.SalesActivity.Net_AMT;
import static com.ka.billingsystem.Activity.SalesActivity.PHone_NO;
import static com.ka.billingsystem.Activity.SalesActivity.activitySalesBinding;
import static com.ka.billingsystem.Activity.SalesActivity.count;
import static com.ka.billingsystem.Activity.SalesActivity.cusEdit;
import static com.ka.billingsystem.Activity.SalesActivity.mCost;
import static com.ka.billingsystem.Activity.SalesActivity.mProduct_name;
import static com.ka.billingsystem.Activity.SalesActivity.mQty;
import static com.ka.billingsystem.Activity.SalesActivity.mTotal;
import static com.ka.billingsystem.Activity.SalesActivity.phoneEdit;
import static com.ka.billingsystem.Activity.SalesActivity.removeView;
import static com.ka.billingsystem.java.ImageEncodeAndDecode.encodeToBase64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import com.ka.billingsystem.java.Export;
import com.ka.billingsystem.java.invoice1;
import com.ka.billingsystem.java.invoice2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class PdfviewActivity extends AppCompatActivity {

    private DataBaseHandler db = new DataBaseHandler(this);
    static PDFView pdfView;
    ImageButton share;
    ImageButton camera;
    ImageButton textButton;
    ImageButton Delete;
    String SHARED_PREFS_KEY = "signature";
    String SHARED_PREFS = "shared_prefs";
    String SHARED_PREFS_Logo = "logo";
    SharedPreferences sharedpreferences;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    String SPIS_FIRST_TIME;
    Long GST;
    String SPIS_FIRST_logo;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        share = (ImageButton) findViewById(R.id.share);
        textButton = findViewById(R.id.textButton);
        camera=findViewById(R.id.camera);
        Delete=findViewById(R.id.pdfViewdelete);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        String qurry = "Select * from user where id='1'";
        Cursor c1 = db.get_value(qurry);
        if (c1.moveToFirst()) {
            @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("signature"));
            @SuppressLint("Range") Long data2 = c1.getLong(c1.getColumnIndex("gst"));
            GST=data2;
            SPIS_FIRST_TIME =data1;
            System.out.println("GST"+GST);
        }


        SPIS_FIRST_logo=sharedpreferences.getString(SHARED_PREFS_Logo,null);

        fileName = "Invoice" + Bill_NO + ".pdf";

        File dir = new File(this.getFilesDir(), "DATA");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file= new File(dir,fileName);
        db.filePath(Bill_NO,file.getAbsolutePath(),SPIS_FIRST_TIME,GST);


        new PdfGenerationTask(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME,SPIS_FIRST_logo, file,GST).execute();

//        File file = invoice1.PDF1(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, fileName, 0, db);
       // pdfView.fromFile(file).load();

        LinearLayout backbutton = findViewById(R.id.backbutton);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   onBackPressed();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCamera();
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete(String.valueOf(Bill_NO));
            }
        });
        textButton.setOnClickListener(new View.OnClickListener() {
            boolean isMethod1 = true;

            @Override
            public void onClick(View v) {
                if (isMethod1) {
                    pdfView.recycle();
                    new PdfGenerationTask2(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME,SPIS_FIRST_logo, file,GST).execute();
                   // File file1 = invoice2.PDF2(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, fileName, db);
                  //  pdfView.fromFile(file1).load();
                } else {
                    pdfView.recycle();
                    new PdfGenerationTask(count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME,SPIS_FIRST_logo, file,GST).execute();
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
                 //   share.setType("application/pdf");
                    share.setDataAndType(uri, "application/zip");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share"));
                } else {
                    Toast.makeText(PdfviewActivity.this,R.string.file_not_found, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Handle the deletion of an invoice.
     */
    public void Delete(String mPbillno) {

        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.do_you_want_to_delete);
            builder.setTitle(R.string.alert);
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                db.moveDataFromTable2ToTable5(mPbillno);

             onBackPressed();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }catch (Exception e) {
            Log.e("Delete", "An exception occurred", e);
        }

    }


    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        } else {
            requestCameraPermission();
        }
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                String imageencoded = encodeToBase64(imageBitmap, Bitmap.CompressFormat.JPEG, 100);
                System.out.println(imageencoded.length());
                db.PrinterImage(Bill_NO, imageencoded);
                Toast.makeText(PdfviewActivity.this, "Printer Image is saved!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onBackPressed() {

        removeView();
        activitySalesBinding.buttonAdd.setVisibility(View.GONE);
        phoneEdit.setText("");
        mQty.clear();
        mTotal.clear();
        mProduct_name.clear();
        mCost.clear();
        Net_AMT = 0L;
        cusEdit.setText("");
        Intent i=new Intent(PdfviewActivity.this,MainActivity2.class);
        startActivity(i);
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
        private Long netAmt;
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
        private Long GST;


        public PdfGenerationTask(int count, Long netAmt, int billNo, String customerName, String phoneNo,
                                 List<String> mQty, List<Long> mCost, List<Long> mTotal, List<String> mProductName,
                                 String spIsFirstTime, String spIsFirstLogo, File file,Long GST) {
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
this.GST=GST;
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
            Export.ExportData(getPackageName(),"Kirthana_backup.zip",create("Kirthana_backup.zip"));
            return invoice1.PDF1(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime,spIsFirstLogo, file, 0,GST);
        }

        public  File  create(String Backup_filename){
            File dir = new File(Environment.getExternalStorageDirectory(), "KIRTHANA AGENCIES");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File subdir = new File(dir, "Backup");
            if (!subdir.exists()) {
                subdir.mkdirs();
            }

            File zipFile = new File(subdir,Backup_filename );
            return zipFile;
        }
        @Override
        protected void onPostExecute(File result) {
            pdfView.fromFile(result).load();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
    private class PdfGenerationTask2 extends AsyncTask<Void, Void, File> {

        private int count;
        private Long netAmt;
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
        private Long GST;

        private AlertDialog progressDialog;
        public PdfGenerationTask2(int count, Long netAmt, int billNo, String customerName, String phoneNo,
                                 List<String> mQty, List<Long> mCost, List<Long> mTotal, List<String> mProductName,
                                 String spIsFirstTime, String spIsFirstLogo, File file,Long GST) {
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
            this.GST=GST;
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
            return invoice2.PDF2(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime,spIsFirstLogo,file,GST);
        }

        @Override
        protected void onPostExecute(File result) {

            pdfView.fromFile(result).load();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}


