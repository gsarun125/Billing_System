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
import android.util.Log;
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
import com.ka.billingsystem.java.Export;
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
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_deleted_invoice);
            sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            SPuser = sharedpreferences.getString(USER_KEY, null);

            LinearLayout backbutton = findViewById(R.id.backbutton_Deleted);
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });


            displayPdf();
        } catch (Exception e) {
            Log.e("onCreate", "An exception occurred", e);
        }
    }

    /**
     * Display PDFs from the database.
     */
    private void displayPdf() {
        try {
            recyclerView = findViewById(R.id.delrecycler_view);
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
            pdfAdapter = new DeleteAdapter(this, pdfList, this, mPbillno, tempbillno, mPtamount, mPDate, mPusername, mPtime, mPcusname, mPcusPhoneno, image);
            recyclerView.setAdapter(pdfAdapter);
        } catch (Exception e) {
            Log.e("displayPdf", "An exception occurred", e);
        }
    }

    /**
     * Handle the selection of a PDF file.
     */
    @Override
    public void onpdfSelected(File file, String mPbillno, String filename) {
        try {
            if (file.exists()) {
                String a = "1";
                Intent i = new Intent(this, DocmentActivity.class);
                //i.putExtra("path", file.getAbsolutePath());
                i.putExtra("billno", mPbillno);
                i.putExtra("option", a);
                startActivity(i);

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.file_is_removed_form_internal_storage_do_you_want_to_generate_again);
                builder.setTitle(R.string.alert);
                builder.setCancelable(true);
                builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.dismiss();
                    String a = "1";
                    Intent i = new Intent(this, DocmentActivity.class);
                    //i.putExtra("path", file.getAbsolutePath());
                    i.putExtra("billno", mPbillno);
                    i.putExtra("option", a);
                    startActivity(i);

                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        } catch (Exception e) {
            Log.e("onpdfSelected", "An exception occurred", e);
        }
    }

    /**
     * Display an image from Base64-encoded string.
     */
    @Override
    public void image(String image) {
        try {
            Bitmap Printerimg = ImageEncodeAndDecode.decodeBase64ToBitmap(image);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.image_dialog_layout, null);
            dialogBuilder.setView(dialogView);

            ImageView imageView = dialogView.findViewById(R.id.dialogImageView);
            imageView.setImageBitmap(Printerimg);

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.e("image", "An exception occurred", e);
        }
    }

    /**
     * Undo the deletion of an invoice.
     */
    @Override
    public void Undo(String mPbillno) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.do_you_want_to_delete);
            builder.setTitle(R.string.alert);
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                db.undoMoveDataFromTable2ToTable5(mPbillno);
                displayPdf();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.e("Undo", "An exception occurred", e);
        }

    }

}