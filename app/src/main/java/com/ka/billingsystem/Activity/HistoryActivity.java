package com.ka.billingsystem.Activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;

import com.ka.billingsystem.databinding.ActivityHistoryBinding;

import com.ka.billingsystem.java.Export;
import com.ka.billingsystem.java.ImageEncodeAndDecode;
import com.ka.billingsystem.java.invoice1;
import com.ka.billingsystem.model.OnPdfFileSelectListener;
import com.ka.billingsystem.model.PdfAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements OnPdfFileSelectListener {
    private ActivityHistoryBinding activityHistoryBinding;
    private List<String> mPbillno = new ArrayList();
    private List<String> tempbillno = new ArrayList();
    private List<String> mPtamount = new ArrayList();
    private List<String> mPDate = new ArrayList();
    private List<String> mPtime = new ArrayList();
    private List<String> mPusername = new ArrayList();
    private List<String> mPcusname = new ArrayList();
    private List<String> mPcusPhoneno = new ArrayList();
    private List<String> image = new ArrayList();
    private PdfAdapter pdfAdapter;
    private List<File> pdfList;
    Long FromDate=0l;
    Long ToDate=0l;
    private RecyclerView recyclerView;
    private HashSet<String> Bill_no_Auto=new HashSet<>();
    private HashSet<String> Phone_no_Auto=new HashSet<>();
    private HashSet<String> Customer_Name_Auto=new HashSet<>();
    EditText editTextDatePickerFrom;
    EditText editTextDatePickerTo;
    Calendar calendar;
    SharedPreferences sharedpreferences;
    String SHARED_PREFS = "shared_prefs";
    String USER_KEY = "user_key";
    String SHARED_PREFS_KEY = "signature";
    String SHARED_PREFS_Logo = "logo";
    String SPuser;
    Cursor c1;

    private boolean fromDateSelected = false;
    private boolean toDateSelected = false;
    private DataBaseHandler db = new DataBaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHistoryBinding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(activityHistoryBinding.getRoot());
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SPuser = sharedpreferences.getString(USER_KEY, null);
        System.out.println(SPuser);
        c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation WHERE sales_user='" + SPuser + "' GROUP BY cus_id) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC");
        displayPdf(c1);
        editTextDatePickerFrom = findViewById(R.id.editTextDatePickrFrom);
        editTextDatePickerTo = findViewById(R.id.editTextDatePickerTO);
        calendar = Calendar.getInstance();

        Autocompition();

        editTextDatePickerFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkDatesAndExecuteMethod();
            }
        });

        editTextDatePickerTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkDatesAndExecuteMethod();
            }
        });

        ArrayAdapter<String> Bill_no_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(Bill_no_Auto));
        activityHistoryBinding.Hbillno.setAdapter(Bill_no_adapter);
        activityHistoryBinding.Hbillno.setThreshold(1);
        activityHistoryBinding.Hbillno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search();
            }
        });

        ArrayAdapter<String> Cus_Name_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(Customer_Name_Auto));
        activityHistoryBinding.Hcusname.setAdapter(Cus_Name_adapter);
        activityHistoryBinding.Hcusname.setThreshold(1);

        activityHistoryBinding.Hcusname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search();
            }
        });

        ArrayAdapter<String> Pnone_no_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(Phone_no_Auto));
        activityHistoryBinding.HPhoneno.setAdapter(Pnone_no_adapter);
        activityHistoryBinding.HPhoneno.setThreshold(1);
        activityHistoryBinding.HPhoneno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search();
            }
        });

        activityHistoryBinding.backbuttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void checkDatesAndExecuteMethod() {
        String fromDate = editTextDatePickerFrom.getText().toString().trim();
        String toDate = editTextDatePickerTo.getText().toString().trim();
        System.out.println("jhhjhghj");
        System.out.println(FromDate);
        System.out.println(ToDate);
    if (!fromDate.isEmpty() && !toDate.isEmpty()) {
            System.out.println("hghfggfyyf");
            search();
        }
    }

    public void search(){
        if (FromDate!=0l&&ToDate!=0l){

            if(FromDate<=ToDate) {
                System.out.println(FromDate);
                System.out.println(ToDate);

                filterWithDate();
            }else {
                editTextDatePickerFrom.setText("");
                editTextDatePickerFrom.setFocusable(true);
                editTextDatePickerFrom.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextDatePickerFrom, InputMethodManager.SHOW_IMPLICIT);
                Toast.makeText(HistoryActivity.this,R.string.invalid_date,Toast.LENGTH_LONG).show();
                editTextDatePickerTo.setText("");
            }
        }
        else {
            filter();
        }
    }
    private DatePickerDialog createDatePickerDialog(boolean isFromDate) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(selectedYear, selectedMonth, selectedDay, isFromDate ? 0 : 23, isFromDate ? 0 : 59);

            Calendar currentDate = Calendar.getInstance();
            if (calendar1.after(currentDate)) {
                calendar1 = currentDate;
            }

            if (isFromDate) {
                FromDate = calendar1.getTimeInMillis();
                editTextDatePickerFrom.setText(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear);

            } else {
                ToDate = calendar1.getTimeInMillis();
                editTextDatePickerTo.setText(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear);

            }
        }, year, month, day);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                if (isFromDate) {
                    editTextDatePickerFrom.setText("");
                    FromDate = 0L;
                } else {
                    editTextDatePickerTo.setText("");
                    ToDate = 0L;
                }
            }
        });

        if (isFromDate && ToDate != 0L) {
            datePickerDialog.getDatePicker().setMaxDate(ToDate);
        } else if (!isFromDate && FromDate != 0L) {
            datePickerDialog.getDatePicker().setMinDate(FromDate);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        } else {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        return datePickerDialog;
    }

    public void showDatePickerDialogFrom(View v) {
        createDatePickerDialog(true).show();
    }

    public void showDatePickerDialogTo(View v) {
        createDatePickerDialog(false).show();
    }


    private void  filter(){
        if (activityHistoryBinding.Hbillno.getText().toString().length() !=0 & activityHistoryBinding.Hcusname.getText().toString().length() !=0){
            Cursor c1 = db.get_value("SELECT DISTINCT * FROM ( SELECT *  FROM Transation WHERE sales_user='" + SPuser + "' AND cus_id LIKE '"+activityHistoryBinding.Hbillno.getText().toString()+"%'  GROUP BY cus_id  UNION ALL SELECT * FROM Transation GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id WHERE customer.cus_name LIKE '"+activityHistoryBinding.Hcusname.getText().toString()+"%' AND sorted.cus_id LIKE '"+activityHistoryBinding.Hbillno.getText().toString()+"%' ORDER BY sorted.time DESC");
            displayPdf(c1);
        } else if (activityHistoryBinding.Hcusname.getText().toString().length() !=0&&activityHistoryBinding.HPhoneno.getText().toString().length() !=0) {
            Cursor c1 = db.get_value("SELECT * FROM ( SELECT * FROM Transation WHERE sales_user='" + SPuser + "' GROUP BY cus_id ) AS sorted JOIN customer  ON sorted.cus_id = customer.cus_id WHERE customer.cus_name LIKE '"+activityHistoryBinding.Hcusname.getText().toString()+"%' AND customer.cus_Phone LIKE '"+activityHistoryBinding.HPhoneno.getText().toString()+"%' ORDER BY sorted.time DESC");
            displayPdf(c1);
        } else if (activityHistoryBinding.Hbillno.getText().toString().length() !=0 ){
            Cursor c1 = db.get_value("SELECT *  FROM (SELECT * FROM Transation WHERE sales_user='" + SPuser + "' AND cus_id LIKE '"+activityHistoryBinding.Hbillno.getText().toString()+"%' GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC");
            displayPdf(c1);
        }
        else if (activityHistoryBinding.Hcusname.getText().toString().length() !=0 ){
            Cursor c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation WHERE sales_user='" + SPuser + "' GROUP BY cus_id) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id WHERE customer.cus_name LIKE '"+activityHistoryBinding.Hcusname.getText().toString()+"%' ORDER BY sorted.time DESC");
            displayPdf(c1);
        } else if (activityHistoryBinding.HPhoneno.getText().toString().length()!=0) {
            Cursor c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation WHERE sales_user='" + SPuser + "' GROUP BY cus_id) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id WHERE customer.cus_Phone LIKE '"+activityHistoryBinding.HPhoneno.getText().toString()+"%' ORDER BY sorted.time DESC");
            displayPdf(c1);
        }else {
           Cursor c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation WHERE sales_user='" + SPuser + "' GROUP BY cus_id) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC");
            displayPdf(c1);

        }
    }

    private void  filterWithDate(){
        if (activityHistoryBinding.Hbillno.getText().toString().length() !=0 & activityHistoryBinding.Hcusname.getText().toString().length() !=0){
            Cursor c1 = db.get_value("SELECT DISTINCT * FROM ( SELECT *  FROM Transation WHERE cus_id LIKE '"+activityHistoryBinding.Hbillno.getText().toString()+"%' GROUP BY cus_id  UNION ALL SELECT * FROM Transation GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id WHERE customer.cus_name LIKE '"+activityHistoryBinding.Hcusname.getText().toString()+"%' AND sorted.cus_id LIKE '"+activityHistoryBinding.Hbillno.getText().toString()+"%' AND sorted.time BETWEEN '"+FromDate+"' AND '" +ToDate+"' ORDER BY sorted.time DESC");
            displayPdf(c1);
        } else if (activityHistoryBinding.Hcusname.getText().toString().length() !=0&&activityHistoryBinding.HPhoneno.getText().toString().length() !=0) {
            Cursor c1 = db.get_value("SELECT * FROM ( SELECT * FROM Transation WHERE sales_user='" + SPuser + "' GROUP BY cus_id ) AS sorted JOIN customer  ON sorted.cus_id = customer.cus_id WHERE customer.cus_name LIKE '"+activityHistoryBinding.Hcusname.getText().toString()+"%' AND customer.cus_Phone LIKE '"+activityHistoryBinding.HPhoneno.getText().toString()+"%' AND sorted.time BETWEEN '"+FromDate+"' AND '" +ToDate+"' ORDER BY sorted.time DESC");
            displayPdf(c1);
        } else if (activityHistoryBinding.Hbillno.getText().toString().length() !=0 ){
            Cursor c1 = db.get_value("SELECT *  FROM (SELECT * FROM Transation WHERE sales_user='" + SPuser + "' AND cus_id LIKE '"+activityHistoryBinding.Hbillno.getText().toString()+"%' GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id AND sorted.time BETWEEN '"+FromDate+"' AND '" +ToDate+"' ORDER BY sorted.time DESC");
            displayPdf(c1);
        }
        else if (activityHistoryBinding.Hcusname.getText().toString().length() !=0 ){
            Cursor c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation WHERE sales_user='" + SPuser + "' GROUP BY cus_id) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id WHERE customer.cus_name LIKE '"+activityHistoryBinding.Hcusname.getText().toString()+"%' AND sorted.time BETWEEN '"+FromDate+"' AND '" +ToDate+"' ORDER BY sorted.time DESC");
            displayPdf(c1);
        } else if (activityHistoryBinding.HPhoneno.getText().toString().length()!=0) {
            Cursor c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation WHERE sales_user='" + SPuser + "' GROUP BY cus_id) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id WHERE customer.cus_Phone LIKE '"+activityHistoryBinding.HPhoneno.getText().toString()+"%'AND sorted.time BETWEEN '"+FromDate+"' AND '" +ToDate+"' ORDER BY sorted.time DESC");
            displayPdf(c1);
        }
        else {
            Cursor c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation WHERE sales_user='" + SPuser + "' GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id AND sorted.time BETWEEN '"+FromDate+"' AND '" +ToDate+"' ORDER BY sorted.time DESC");
            displayPdf(c1);
        }
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
        image.clear();

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

                    image.add(data7);
                    Date res = new Date(data3);
                      tempbillno.add(data1);

                    mPbillno.add("Bill No: "+data1);
                    mPtamount.add("Total Amount: "+data2+" Rs.");
                    mPtime.add("Time:"+formatter.format(res));
                    mPDate.add("Generated Date : "+formatter1.format(res));
                    mPusername.add("Generated BY: "+data4);
                    mPcusname.add("Name: "+data5);
                    mPcusPhoneno.add("Mobile no: "+data6);
                    pdfList.add(file);

            } while (c1.moveToNext());
        }

        pdfAdapter=new PdfAdapter(this,pdfList,this,mPbillno,tempbillno,mPtamount,mPDate,mPusername,mPtime,mPcusname,mPcusPhoneno,image);
        recyclerView.setAdapter(pdfAdapter);

    }
    private void Autocompition(){
        Cursor c1 = db.get_value("select Bill_No from transation");
        if (c1.moveToFirst()) {
            do {
                @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("Bill_No"));
                 Bill_no_Auto.add(data1);
            } while (c1.moveToNext());
        }
        Cursor c2 = db.get_value("select cus_name,cus_Phone from customer");
        if (c2.moveToFirst()) {
            do {
                @SuppressLint("Range") String data1 = c2.getString(c2.getColumnIndex("cus_name"));
                @SuppressLint("Range") String data2 = c2.getString(c2.getColumnIndex("cus_Phone"));
               Customer_Name_Auto.add(data1);
               Phone_no_Auto.add(data2);
            } while (c2.moveToNext());
        }
    }






    @Override
    public void onpdfSelected(File file, String mPbillno, String filename) {

        if (file.exists()){
            String a="2";
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
                System.out.println(SPIS_FIRST_TIME);
                String SPIS_FIRST_logo=sharedpreferences.getString(SHARED_PREFS_Logo,null);
                File dir = new File(this.getFilesDir(), "DATA");

                if (!dir.exists()) {
                    dir.mkdir();
                }

                File file2= new File(dir,fileName);
               // File file1 = invoice1.PDF1(count,Net_AMT, Integer.parseInt(mPbillno),cusname,phoneno,mQty,mCost,mTotal,mProduct_name,SPIS_FIRST_TIME,SPIS_FIRST_logo,file2,time);
                new HistoryActivity.PdfREGenerationTask(count,Net_AMT, Integer.parseInt(mPbillno),cusname,phoneno,mQty,mCost,mTotal,mProduct_name,SPIS_FIRST_TIME,SPIS_FIRST_logo,file2,time).execute();

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




    /*
    @Override
    public void Share(File file) {
        if(file.exists()){
            Uri uri = FileProvider.getUriForFile(HistoryActivity.this, HistoryActivity.this.getPackageName() + ".provider", file);

            Intent share = new Intent();
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setAction(Intent.ACTION_SEND);
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share"));
        }
        else {
            Toast.makeText(HistoryActivity.this,"file not found",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void Delete(String mPbillno) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to Delete..? ");
        builder.setTitle("Alert !");
        builder.setCancelable(true);
        builder.setNegativeButton("No",(DialogInterface.OnClickListener)(dialog, which) ->{
            dialog.dismiss();
        });
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            db.moveDataFromTable2ToTable5(mPbillno);
            Cursor  c1 = db.get_value("SELECT * FROM (SELECT * FROM Transation GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC");
            displayPdf(c1);
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void Download(File file) {

    }

     */


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
            ProgressBar progressBar = new ProgressBar(HistoryActivity.this, null, android.R.attr.progressBarStyleLarge);
            progressBar.setIndeterminate(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            progressBar.setLayoutParams(params);

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HistoryActivity.this);
            builder.setView(progressBar);
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();

        }
        @Override
        protected File doInBackground(Void... voids) {
            Export.ExportData(getPackageName());
            return invoice1.PDF1(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime,spIsFirstLogo, file, time);
        }

        @Override
        protected void onPostExecute(File result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }


            String a="2";
            Intent i=new Intent(HistoryActivity.this,DocmentActivity.class);

            i.putExtra("path",result.getAbsolutePath());
            i.putExtra("billno",billNo);
            i.putExtra("option",a);
            startActivity(i);

        }
    }
}