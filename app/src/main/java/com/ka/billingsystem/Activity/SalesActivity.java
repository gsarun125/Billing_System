package com.ka.billingsystem.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.databinding.ActivitySalesBinding;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public class SalesActivity extends AppCompatActivity  {
    private DataBaseHandler db = new DataBaseHandler(this);
    private static ActivitySalesBinding activitySalesBinding;

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
    public static EditText cusEdit;
    public static EditText phoneEdit;
    public static List<Long> mTotal = new ArrayList();


    public static int  count;
    public static long Net_AMT = 0;
    public static List<Long> mCost= new ArrayList();
    String SHARED_PREFS = "shared_prefs";
    String USER_KEY = "user_key";

    String SPuser;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySalesBinding = ActivitySalesBinding.inflate(getLayoutInflater());
       //getSupportActionBar().setTitle("Sales");
        setContentView(activitySalesBinding.getRoot());


        activitySalesBinding.button.setVisibility(View.GONE);
        activitySalesBinding.buttonAdd.setVisibility(View.GONE);

        cusEdit=(EditText) findViewById(R.id.cusName);

        sharedpreferences= getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        SPuser = sharedpreferences.getString(USER_KEY,null);
    //    SPIS_FIRST_TIME=sharedpreferences.getString(SHARED_PREFS_KEY,null);
        phoneEdit=(EditText) findViewById(R.id.PhoneNo);

        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkIfBothFieldsHaveText();
            }
        });
        cusEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkIfBothFieldsHaveText();
            }
        });

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

                       saveData();
                   }else{
                       Toast.makeText(SalesActivity.this,getString(R.string.add_printer_information),Toast.LENGTH_SHORT).show();
                   }
               }
            }
        });
    }
    private void checkIfBothFieldsHaveText() {
        String phone = phoneEdit.getText().toString().trim();
        String cusName = cusEdit.getText().toString().trim();
        if (!cusName.isEmpty()){
            cusEdit.setBackgroundResource(R.drawable.edit_text_green_bg);
        }
        else {
            cusEdit.setBackgroundResource(R.drawable.edit_text_bg);
        }
        if (phone.length()==10){
            activitySalesBinding.phonenoLayout.setBackgroundResource(R.drawable.edit_text_green_bg);
        }else {
            activitySalesBinding.phonenoLayout.setBackgroundResource(R.drawable.edit_text_bg);
        }
        if (!phone.isEmpty() && !cusName.isEmpty()) {
            if (phone.length()==10){

                activitySalesBinding.button.setVisibility(View.VISIBLE);
                activitySalesBinding.buttonAdd.setVisibility(View.VISIBLE);
                System.out.println();
                if (add_count==0){
                    addNewView();
                    add_count++;
                }
            }
        }
        else {
            activitySalesBinding.button.setVisibility(View.GONE);
        }
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
        Cost.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private DecimalFormat indianCurrencyFormat = new DecimalFormat("#,###");

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("e");
                if (!s.toString().equals(current)) {
                    Cost.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.\\s]", indianCurrencyFormat.getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    double parsed = 0;
                    try {
                        parsed = Double.parseDouble(cleanString);
                        String formatted = indianCurrencyFormat.format(parsed);

                        current = formatted;
                        Cost.setText(formatted);

                        Cost.setSelection(formatted.length());

                    } catch (NumberFormatException e) {
                        Cost.setText("");
                    }

                    Cost.addTextChangedListener(this);
                }
            }
        });


    }
    public  boolean check_all_value(int i){
        View v = null;
            v = this.activitySalesBinding.parentLinearLayout.getChildAt(i);
            qty=(EditText) v.findViewById(R.id.et_Qty);
            Printer_Type=(EditText) v.findViewById(R.id.et_Typep);
            Cost=(EditText) v.findViewById(R.id.et_Cost) ;

        if (Printer_Type.getText().toString().length() == 0) {
            Printer_Type.setError(getString(R.string.printer_type_is_required1));
            Printer_Type.setFocusable(true);
            Printer_Type.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Printer_Type, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (Cost.getText().toString().length() == 0) {
            Cost.setError(getString(R.string.cost_is_required));
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

                Printer_Type.setError(getString(R.string.printer_type_is_required1));
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
            Long cost;
            String costString = Cost.getText().toString().replaceAll(",", "");
            if (!costString.isEmpty()) {
                cost = Long.valueOf(costString);
            } else {
                cost = 0L;
            }

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

    String SHARED_PREFS_KEY = "signature";

    String SPIS_FIRST_TIME=sharedpreferences.getString(SHARED_PREFS_KEY,null);

    if (SPIS_FIRST_TIME!=null){
        Intent intent = new Intent(SalesActivity.this, PdfviewActivity.class);
        startActivity(intent);
    }
    else {

        Intent intent=new Intent(SalesActivity.this,Signature.class);
        startActivity(intent);
    }

    }
    }





    public static void removeView(){

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
        if (phoneEdit.length() != 10) {
            phoneEdit.setError(getString(R.string.mobile_number_must_be_10_digits));
            phoneEdit.setFocusable(true);
            phoneEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(phoneEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        return true;
    }




}