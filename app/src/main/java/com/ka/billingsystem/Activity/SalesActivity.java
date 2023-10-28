package com.ka.billingsystem.Activity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.databinding.ActivitySalesBinding;
import com.ka.billingsystem.java.invoice1;
import com.ka.billingsystem.java.numbertoword;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public class SalesActivity extends AppCompatActivity {
    private DataBaseHandler db = new DataBaseHandler(this);
    private ActivitySalesBinding activitySalesBinding;

    int add_count=0;
    EditText qty;
    EditText Printer_Type;
    EditText Cost;
   public static String Customer_Name="";
   public static String PHone_NO="";

    int Customer_Id;

    public static  List<String> mQty = new ArrayList();

    public static List<String> mProduct_name = new ArrayList();
    public static int Bill_NO;
    EditText cusEdit;
    EditText phoneEdit;
    public static List<Long> mTotal = new ArrayList();


    public static int  count;
    public static long Net_AMT = 0;
    public static List<Long> mCost= new ArrayList();
    String SHARED_PREFS = "shared_prefs";
    String USER_KEY = "user_key";

    String SPuser;

    SharedPreferences sharedpreferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySalesBinding = ActivitySalesBinding.inflate(getLayoutInflater());

        //getSupportActionBar().setTitle("Sales");
        setContentView(activitySalesBinding.getRoot());



        cusEdit=(EditText) findViewById(R.id.cusName);

        sharedpreferences= getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        SPuser = sharedpreferences.getString(USER_KEY,null);
    //    SPIS_FIRST_TIME=sharedpreferences.getString(SHARED_PREFS_KEY,null);
        phoneEdit=(EditText) findViewById(R.id.PhoneNo);




           activitySalesBinding.buttonAdd.setOnClickListener(new View.OnClickListener() {


               @Override
            public void onClick(View view) {
                if(add_count<10 ) {
                    if (add_count!=0){
                        if (check_all_value(add_count-1)){
                            addNewView();
                            add_count++;
                        }
                    }else {
                    addNewView();
                    add_count++;
                    }

                }
                else {
                    Toast.makeText(SalesActivity.this, R.string.you_cannot_add_more_then_10_value,Toast.LENGTH_LONG).show();
                }
            }
        });
        activitySalesBinding.button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {

               if( CheckAllFields()) {

                   LinearLayout layout = activitySalesBinding.parentLinearLayout;
                   int count = layout.getChildCount();

                   if (count != 0) {
                       progressDialog = new ProgressDialog(SalesActivity.this);
                       progressDialog.setMessage("Loading...");
                       progressDialog.setCancelable(false);
                       progressDialog.show();
                       saveData();
                   }else{
                       Toast.makeText(SalesActivity.this,"add Printer information",Toast.LENGTH_SHORT).show();
                   }
               }
            }
        });
    }

    public void onDelete(View v) {
        add_count--;
        activitySalesBinding.parentLinearLayout.removeView((View) v.getParent());
    }

    private  void addNewView() {

        View  inflater = LayoutInflater.from((Context)this).inflate(R.layout.row_add_language, null);
        LinearLayout layout = activitySalesBinding.parentLinearLayout;
        Intrinsics.checkNotNullExpressionValue(layout, "binding.parentLinearLayout");

        layout.addView(inflater, layout.getChildCount());

        qty = (EditText) inflater.findViewById(R.id.et_Qty);
        Printer_Type=(EditText) inflater.findViewById(R.id.et_Typep);
        Cost=(EditText)inflater.findViewById(R.id.et_Cost) ;

    }
    public  boolean check_all_value(int i){
        View v = null;
            v = this.activitySalesBinding.parentLinearLayout.getChildAt(i);
            qty=(EditText) v.findViewById(R.id.et_Qty);
            Printer_Type=(EditText) v.findViewById(R.id.et_Typep);
            Cost=(EditText) v.findViewById(R.id.et_Cost) ;

        if (Printer_Type.getText().toString().length() == 0) {
            Printer_Type.setError("Printer Type is required");
            Printer_Type.setFocusable(true);
            Printer_Type.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Printer_Type, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (Cost.getText().toString().length() == 0) {
            Cost.setError(getString(R.string.net_quantity_is_required));
            Cost.setFocusable(true);
            Cost.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Cost, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (qty.getText().toString().length() == 0) {
            qty.setError(getString(R.string.net_quantity_is_required));
            qty.setFocusable(true);
            qty.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(qty, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }



    return true;
    }
    public  boolean check_all(int count){
        View v = null;
        for (int i=0;i<count;i++) {
            v = this.activitySalesBinding.parentLinearLayout.getChildAt(i);
            qty = (EditText) v.findViewById(R.id.et_Qty);
            Printer_Type = (EditText) v.findViewById(R.id.et_Typep);
            Cost = (EditText) v.findViewById(R.id.et_Cost);

            if (Printer_Type.getText().toString().length() == 0) {
                progressDialog.dismiss();
                Printer_Type.setError("Printer Type is required");
                Printer_Type.setFocusable(true);
                Printer_Type.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(Printer_Type, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (Cost.getText().toString().length() == 0) {
                progressDialog.dismiss();
                Cost.setError(getString(R.string.net_quantity_is_required));
                Cost.setFocusable(true);
                Cost.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(Cost, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (qty.getText().toString().length() == 0) {
                progressDialog.dismiss();
                qty.setError(getString(R.string.net_quantity_is_required));
                qty.setFocusable(true);
                qty.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(qty, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        }

        return true;
    }

    void saveData() {

        LinearLayout layout = activitySalesBinding.parentLinearLayout;
       count= layout.getChildCount();
       View v = null;
     if (check_all( count)) {
        for (int i = 0; i < count; i++) {

        v = this.activitySalesBinding.parentLinearLayout.getChildAt(i);
        qty = (EditText) v.findViewById(R.id.et_Qty);
        Printer_Type = (EditText) v.findViewById(R.id.et_Typep);
        Cost = (EditText) v.findViewById(R.id.et_Cost);

        String QTY = qty.getText().toString();
        String PrinterType = Printer_Type.getText().toString();
        Long cost = Long.valueOf(Cost.getText().toString());

        mProduct_name.add(PrinterType);
        mCost.add(Long.valueOf(cost));
        mQty.add(QTY);

        Long addTotal = cost * Long.parseLong(QTY);
        mTotal.add(addTotal);
        }

    Customer_Name = cusEdit.getText().toString();
    PHone_NO = phoneEdit.getText().toString();
    add_count = 0;
    Cursor cursor = db.get_value("select max(Bill_No) from Transation");
    if (cursor != null) {
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        Customer_Id = id + 1;
        Bill_NO = id + 1;
    }


    for (Long total : mTotal) {

        Net_AMT = Net_AMT + total;

    }

    long time = System.currentTimeMillis();
    for (int i = 0; i < count; i++) {
        db.insertData_to_trancation(Customer_Id, Bill_NO, mProduct_name.get(i), mQty.get(i), mCost.get(i), mTotal.get(i), Net_AMT, time, SPuser);
    }
    db.insertData_to_Customer(Customer_Id, Customer_Name, PHone_NO);

    removeView();
    cusEdit.setText("");
    phoneEdit.setText("");
    String SHARED_PREFS_KEY = "signature";

    String SPIS_FIRST_TIME=sharedpreferences.getString(SHARED_PREFS_KEY,null);

    if (SPIS_FIRST_TIME!=null){
        Intent intent = new Intent(SalesActivity.this, PdfviewActivity.class);
        startActivity(intent);
        System.out.println("second");
    }
    else {

        Intent intent=new Intent(SalesActivity.this,Signature.class);
        startActivity(intent);
        System.out.println("first");
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

    void removeView(){

        LinearLayout layout = activitySalesBinding.parentLinearLayout;
        Intrinsics.checkNotNullExpressionValue(layout, "binding.parentLinearLayout");
        layout.removeAllViews();

    }

    private boolean CheckAllFields() {
        if (cusEdit.length() == 0) {
            cusEdit.setError(getString(R.string.customer_name_is_required));
            cusEdit.setFocusable(true);
            cusEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(cusEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (phoneEdit.length() == 0) {
            phoneEdit.setError(getString(R.string.customer_phone_no_is_required));
            phoneEdit.setFocusable(true);
            phoneEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(phoneEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        return true;
    }


}