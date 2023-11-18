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
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocmentActivity extends AppCompatActivity {
    private DataBaseHandler db = new DataBaseHandler(this);
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    String filePath = "";
    ImageButton share;
    File file;
    ImageButton delete;
    ImageButton Undo;
    ImageButton ChangePdf;
    String billno;
    String option;
    String SHARED_PREFS_Logo = "logo";
    String ADMIN_LOGIN = "admin_login";
    String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    PDFView pdfView;
    ImageButton camera;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_docment);

            sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            String Admin_login = sharedpreferences.getString(ADMIN_LOGIN, null);

            pdfView = (PDFView) findViewById(R.id.pdfView3);
            share = (ImageButton) findViewById(R.id.share1);
            delete = (ImageButton) findViewById(R.id.delete);
            Undo = (ImageButton) findViewById(R.id.Undo1);
            ChangePdf = (ImageButton) findViewById(R.id.ChangePdf);
            camera=findViewById(R.id.Dcamera);


            option = getIntent().getStringExtra("option");
            billno = getIntent().getStringExtra("billno");


            file = new File(filePath);

            invoice1();

            if (option.equals("0") || option.equals("2")) {

                Undo.setVisibility(View.GONE);
            }

            if (Admin_login.equals("true")) {
                Undo.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }
            LinearLayout backbutton = findViewById(R.id.backbutton_document);
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });

            Undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Undo(billno);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Share();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (option.equals("0") || option.equals("2")) {

                        Delete(billno);
                    }else {
                        PermanetDeletion(billno);
                    }

                }
            });

            ChangePdf.setOnClickListener(new View.OnClickListener() {
                boolean isMethod1 = true;

                @Override
                public void onClick(View view) {

                    if (isMethod1) {
                        pdfView.recycle();
                        invoice2();

                    } else {
                        pdfView.recycle();
                        invoice1();
                    }
                    isMethod1 = !isMethod1;
                }
            });

            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openCamera();
                }
            });

        } catch (Exception e) {
            Log.e("onCreate", "An exception occurred", e);
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
                db.PrinterImage(Integer.parseInt(billno), imageencoded);
                Toast.makeText(this, "Printer Image is saved!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void invoice1() {
        String cusname = null;
        String phoneno = null;
        Long time = 0l;
        List<String> mQty = new ArrayList();
        List<String> mProduct_name = new ArrayList();
        List<Long> mTotal = new ArrayList();
        List<Long> mCost = new ArrayList();
        int count = 0;
        long Net_AMT = 0;

        String SPIS_FIRST_TIME = null;
        Long GST=null;
        Cursor c1;
        if (option.equals("1")) {
            c1 = db.get_value("SELECT * FROM Deleted INNER JOIN customer ON  Deleted.cus_id= customer.cus_id WHERE Bill_No ='" + billno + "'");

        } else {
            c1 = db.get_value("SELECT * FROM Transation INNER JOIN customer ON  Transation.cus_id= customer.cus_id WHERE Bill_No ='" + billno + "'");
        }
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
                @SuppressLint("Range") Long data9 = c1.getLong(c1.getColumnIndex("gst"));

                mQty.add(data1);
                mCost.add(data2);
                mTotal.add(data3);
                mProduct_name.add(data4);
                if (cusname == null || phoneno == null || time == 0l) {
                    cusname = data5;
                    phoneno = data6;
                    SPIS_FIRST_TIME = data8;
                    time = data7;
                    GST=data9;
                }
                count++;
            } while (c1.moveToNext());
        }
        for (Long total : mTotal) {

            Net_AMT = Net_AMT + total;

        }
        String fileName = "Invoice" + billno + ".pdf";
        String SPIS_FIRST_logo = sharedpreferences.getString(SHARED_PREFS_Logo, null);
        File dir = new File(this.getFilesDir(), "DATA");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file2 = new File(dir, fileName);
        new DocmentActivity.PdfGenerationTask(count, Net_AMT, Integer.parseInt(billno), cusname, phoneno, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, SPIS_FIRST_logo, file2,GST).execute();

    }

    private void invoice2() {
        String cusname = null;
        String phoneno = null;
        Long time = 0l;
        List<String> mQty = new ArrayList();
        List<String> mProduct_name = new ArrayList();
        List<Long> mTotal = new ArrayList();
        List<Long> mCost = new ArrayList();
        int count = 0;
        long Net_AMT = 0;

        String SPIS_FIRST_TIME = null;
        Long GST=null;
        Cursor c1;
        if (option.equals("1")) {
            c1 = db.get_value("SELECT * FROM Deleted INNER JOIN customer ON  Deleted.cus_id= customer.cus_id WHERE Bill_No ='" + billno + "'");

        } else {
            c1 = db.get_value("SELECT * FROM Transation INNER JOIN customer ON  Transation.cus_id= customer.cus_id WHERE Bill_No ='" + billno + "'");
        }
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
                @SuppressLint("Range") Long data9 = c1.getLong(c1.getColumnIndex("gst"));

                mQty.add(data1);
                mCost.add(data2);
                mTotal.add(data3);
                mProduct_name.add(data4);
                if (cusname == null || phoneno == null || time == 0l) {
                    cusname = data5;
                    phoneno = data6;
                    SPIS_FIRST_TIME = data8;
                    time = data7;
                    GST=data9;
                }
                count++;
            } while (c1.moveToNext());
        }
        for (Long total : mTotal) {

            Net_AMT = Net_AMT + total;

        }
        String fileName = "Invoice" + billno + ".pdf";
        String SPIS_FIRST_logo = sharedpreferences.getString(SHARED_PREFS_Logo, null);
        File dir = new File(this.getFilesDir(), "DATA");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file2 = new File(dir, fileName);
        ///   = invoice1.PDF1(count,Net_AMT, Integer.parseInt(mPbillno),cusname,phoneno,mQty,mCost,mTotal,mProduct_name,SPIS_FIRST_TIME,SPIS_FIRST_logo,file2,time);
        //       new DocmentActivity.PdfREGenerationTask(count,Net_AMT, Integer.parseInt(mPbillno),cusname,phoneno,mQty,mCost,mTotal,mProduct_name,SPIS_FIRST_TIME,SPIS_FIRST_logo,file2,time).execute();
        new DocmentActivity.PdfGenerationTask2(count, Net_AMT, Integer.parseInt(billno), cusname, phoneno, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, SPIS_FIRST_logo, file2,GST).execute();

    }


    /**
     * Handle the sharing of the PDF file.
     */
    private void Share() {
        try {
            File outputFile;
            outputFile = file;

            if (outputFile.exists()) {
                Uri uri = FileProvider.getUriForFile(DocmentActivity.this, DocmentActivity.this.getPackageName() + ".provider", outputFile);


                Intent share = new Intent();
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.setAction(Intent.ACTION_SEND);
                share.setAction(Intent.ACTION_SEND);
                share.setDataAndType(uri, "application/zip");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share"));
            } else {
                Toast.makeText(DocmentActivity.this, R.string.file_not_found, Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Log.e("Share", "An exception occurred", e);
        }

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
                if (option.equals("0")) {
                    Intent i = new Intent(DocmentActivity.this, RecentInvoiceActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                } else {
                    Intent i = new Intent(DocmentActivity.this, HistoryActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.e("Delete", "An exception occurred", e);
        }

    }

    /**
     * Handle the permanet deletion of an invoice.
     */
    private  void PermanetDeletion(String billno){
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to Delete permanently..?");
            builder.setTitle(R.string.alert);


            SpannableString spannableMessage = new SpannableString(getString(R.string.do_you_want_to_delete));
            spannableMessage.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString spannableTitle = new SpannableString(getString(R.string.alert));
            spannableTitle.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.setMessage(spannableMessage);
            builder.setTitle(spannableTitle);

            builder.setCancelable(true);
            builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
            });


            SpannableString spannableYes = new SpannableString(getString(R.string.yes));
            spannableYes.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableYes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.setPositiveButton(spannableYes, (DialogInterface.OnClickListener) (dialog, which) -> {
                db.PermanetDelete(billno);
                Intent i = new Intent(DocmentActivity.this, DeletedInvoice.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } catch (Exception e) {
            Log.e("PermanetDeletion", "An exception occurred", e);
        }

    }
    /**
     * Handle the undo action for an invoice.
     */
    public void Undo(String mPbillno) {
        try {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.do_you_want_to_undo);
            builder.setTitle(R.string.alert);
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                db.undoMoveDataFromTable2ToTable5(mPbillno);
                //   super.finish();

                Intent i = new Intent(DocmentActivity.this, DeletedInvoice.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            });
            androidx.appcompat.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.e("Undo", "An exception occurred", e);
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
        private  Long GST;

        public PdfGenerationTask2(int count, long netAmt, int billNo, String customerName, String phoneNo,
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
            this.spIsFirstLogo = spIsFirstLogo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = new ProgressBar(DocmentActivity.this, null, android.R.attr.progressBarStyleLarge);
            progressBar.setIndeterminate(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            progressBar.setLayoutParams(params);

            AlertDialog.Builder builder = new AlertDialog.Builder(DocmentActivity.this);
            builder.setView(progressBar);
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();
        }

        @Override
        protected File doInBackground(Void... voids) {
            return invoice2.PDF2(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime, spIsFirstLogo, file,GST);
        }

        @Override
        protected void onPostExecute(File result) {

            pdfView.fromFile(result).load();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

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

        private Long GST;

        public PdfGenerationTask(int count, long netAmt, int billNo, String customerName, String phoneNo,
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
            this.spIsFirstLogo = spIsFirstLogo;
        }

        private AlertDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = new ProgressBar(DocmentActivity.this, null, android.R.attr.progressBarStyleLarge);
            progressBar.setIndeterminate(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            progressBar.setLayoutParams(params);

            AlertDialog.Builder builder = new AlertDialog.Builder(DocmentActivity.this);
            builder.setView(progressBar);
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();

        }

        @Override
        protected File doInBackground(Void... voids) {
            Export.ExportData(getPackageName(), "Kirthana_backup.zip", create("Kirthana_backup.zip"));
            return invoice1.PDF1(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime, spIsFirstLogo, file, 0,GST);
        }

        public File create(String Backup_filename) {
            File dir = new File(Environment.getExternalStorageDirectory(), "KIRTHANA AGENCIES");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File subdir = new File(dir, "Backup");
            if (!subdir.exists()) {
                subdir.mkdirs();
            }

            File zipFile = new File(subdir, Backup_filename);
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

}
