package com.ka.billingsystem.Activity;

import static com.ka.billingsystem.Activity.SalesActivity.Net_AMT;
import static com.ka.billingsystem.Activity.SalesActivity.mCost;
import static com.ka.billingsystem.Activity.SalesActivity.mProduct_name;
import static com.ka.billingsystem.Activity.SalesActivity.mQty;
import static com.ka.billingsystem.Activity.SalesActivity.mTotal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;


import java.io.File;

public class DocmentActivity extends AppCompatActivity {
    private DataBaseHandler db = new DataBaseHandler(this);
    String filePath="";
    ImageButton share;
    ImageButton delete;
    ImageButton Undo;
    String billno;
    String option;
    String ADMIN_LOGIN = "admin_login";
    String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docment);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String Admin_login = sharedpreferences.getString(ADMIN_LOGIN, null);

        PDFView pdfView=(PDFView) findViewById(R.id.pdfView3);
        share = (ImageButton) findViewById(R.id.share1);
        delete=(ImageButton)findViewById(R.id.delete);
        Undo=(ImageButton)findViewById(R.id.Undo1) ;

       option= getIntent().getStringExtra("option");
        billno=getIntent().getStringExtra("billno");
        filePath=getIntent().getStringExtra("path");
        System.out.println(option);
        File file=new File(filePath);
        if(option.equals("0")||option.equals("2")){

           Undo.setVisibility(View.GONE);
        }else {
            delete.setVisibility(View.GONE);
        }

        if (Admin_login.equals("true"))
        {
            Undo.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }
        Uri path=Uri.fromFile(file);
        pdfView.fromUri(path).load();

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
                File outputFile;
                outputFile = file;

                if (outputFile.exists()) {
                    Uri uri = FileProvider.getUriForFile(DocmentActivity.this, DocmentActivity.this.getPackageName() + ".provider", outputFile);

                    System.out.println(DocmentActivity.this.getPackageName());
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
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Delete(billno);
            }
        });
    }

    public void Delete(String mPbillno) {
        System.out.println(mPbillno);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.do_you_want_to_delete);
        builder.setTitle(R.string.alert);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.no,(DialogInterface.OnClickListener)(dialog, which) ->{
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

    }
    public void Undo(String mPbillno) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.do_you_want_to_undo);
        builder.setTitle(R.string.alert);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.no,(DialogInterface.OnClickListener)(dialog, which) ->{
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

    }

}
