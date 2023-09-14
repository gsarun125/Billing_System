package com.ka.billingsystem.Activity;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ka.billingsystem.R;
import com.ka.billingsystem.databinding.ActivitySalesBinding;
import com.ka.billingsystem.DataBase.DataBaseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public class SalesActivity extends AppCompatActivity {
    public DataBaseHandler db = new DataBaseHandler(this);
    public ActivitySalesBinding activitySalesBinding;
    private List<String> mSpinner = new ArrayList();
    private List<Integer> mAvailable_qty = new ArrayList();

    int pageWidth=1200;
    int add_count=0;
    EditText qty;
    TextView Pcode;
   public String Customer_Name="";
   public String PHone_NO="";
   int Bill_NO;

    int Customer_Id;

    public List<String> mQty = new ArrayList();

    public List<String> mProduct_name = new ArrayList();

    EditText cusEdit;
    EditText phoneEdit;
    public List<Float> mTotal = new ArrayList();


    public List<String> mProduct_Code = new ArrayList();

    public List<String> mCost= new ArrayList();
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

        mAvailable_qty.clear();
        cusEdit=(EditText) findViewById(R.id.cusName);

        sharedpreferences= getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        SPuser = sharedpreferences.getString(USER_KEY,null);

        phoneEdit=(EditText) findViewById(R.id.PhoneNo);

        mSpinner.add(getString(R.string.select));

        Spinner_value();
           activitySalesBinding.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(add_count<10) {
                    mQty.clear();

                    mTotal.clear();
                    mProduct_name.clear();
                    mCost.clear();

                    addNewView();
                    add_count++;



                }
                else {
                    Toast.makeText(SalesActivity.this, R.string.you_cannot_add_more_then_10_value,Toast.LENGTH_LONG).show();
                }
            }
        });
        activitySalesBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if( CheckAllFields()) {
                   LinearLayout layout = activitySalesBinding.parentLinearLayout;
                   int count = layout.getChildCount();

                   if (count != 0) {
                       saveData();
                   }else{
                       Toast.makeText(SalesActivity.this,"add product information",Toast.LENGTH_SHORT).show();
                   }
               }
            }
        });
    }

    public void onDelete(View v) {
        add_count--;
        activitySalesBinding.parentLinearLayout.removeView((View) v.getParent());
    }
    private final void addNewView() {

        View  inflater = LayoutInflater.from((Context)this).inflate(R.layout.row_add_language, null);
        LinearLayout layout = activitySalesBinding.parentLinearLayout;
        Intrinsics.checkNotNullExpressionValue(layout, "binding.parentLinearLayout");

        layout.addView(inflater, layout.getChildCount());

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSpinner);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );


        Spinner spinner = (Spinner) inflater.findViewById(R.id.sproductid);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                spinner.setSelection(position);
                String spinnerSelectedValue = parent.getItemAtPosition(position).toString();

                System.out.println(spinnerSelectedValue);
                if (spinnerSelectedValue!=getString(R.string.select)) {
                    Cursor c1 = db.get_value("SELECT  Product_Code,quantity FROM Stock WHERE Product_Name="+"'"+spinnerSelectedValue+"'");
                    if (c1.moveToFirst()) {
                        do {


                            qty = (EditText) inflater.findViewById(R.id.et_Qty);



                            @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("quantity"));

                            mAvailable_qty.add(Integer.valueOf(data2));
                            if (!data2.equals("0")) {
                                String available = "Available QTY ";
                                qty.setHint(available + data2);

                            }else {
                                qty.setHint("No Stock Available ");
                            }

                        } while (c1.moveToNext());
                    }
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }
    void saveData() {
        LinearLayout layout = activitySalesBinding.parentLinearLayout;
       int count= layout.getChildCount();
       View v = null;
       for (int i=0;i<count;i++){

           v = this.activitySalesBinding.parentLinearLayout.getChildAt(i);

           qty=(EditText) v.findViewById(R.id.et_Qty);


           String QTY= qty.getText().toString();
           if (QTY.length()!=0) {
               int tempQTY = Integer.parseInt(qty.getText().toString());
               System.out.println(tempQTY);
               System.out.println(mAvailable_qty.get(i));
               if (mAvailable_qty.get(i) >= tempQTY && tempQTY!=0) {
                   mQty.add(QTY);
               } else {
                   qty.setError("invalid QTY");
                   return;
               }
           }


           Spinner spinner = (Spinner) v.findViewById(R.id.sproductid);
           String Position = spinner.getSelectedItem().toString();

                if(Position.length()!=0& QTY.length()!=0) {

                    mProduct_name.add(Position);

                    Cursor c1 = db.get_value("SELECT  cost,Product_Code FROM Stock WHERE Product_Name="+"'"+ Position+"'");
                    if (c1.moveToFirst()) {
                        do {
                            @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("cost"));
                            @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("Product_Code"));
                            mProduct_Code.add(data2);

                            mCost.add(data1);
                            float addTotal = Float.parseFloat(data1) * Float.parseFloat(QTY);
                            mTotal.add(addTotal);
                        } while (c1.moveToNext());
                    }
                }
                else {
                    Toast.makeText(this, R.string.enter_all_the_value,Toast.LENGTH_SHORT).show();
                    return;
                }
           }

            PDF();

    }

    private boolean CheckAllFields() {
        if (cusEdit.length() == 0) {
            cusEdit.setError("Customer Name is required");
            cusEdit.setFocusable(true);
            cusEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(cusEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (phoneEdit.length() == 0) {
            phoneEdit.setError("Customer Phone No is required");
            phoneEdit.setFocusable(true);
            phoneEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(phoneEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        // after all validation return true.
        return true;
    }



    public void PDF(){
        Customer_Name=cusEdit.getText().toString();
        PHone_NO=phoneEdit.getText().toString();
        mAvailable_qty.clear();
        PdfDocument document=new PdfDocument();
        Paint myPaint=new Paint();
        Paint titlePaint=new Paint();
        PdfDocument.PageInfo myPageInfo1=new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        PdfDocument.Page page=document.startPage(myPageInfo1);
        Canvas canvas=page.getCanvas();


        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
        titlePaint.setTextSize(40);
        canvas.drawText("TAX INVOICE",pageWidth/2,50,titlePaint);


        //title
        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
        titlePaint.setTextSize(50);
        canvas.drawText("KIRTHANA AGENCIES",20,150,titlePaint);

        //Address
        String Address="#6,Alikhan Street, Alandur, Chennai-600 016,";
        String Adddress1="Tamil Nadu,India.";
        String Tel="+91 44 2231 4628";
        String Email="kirthana.agencics@outlook.com";
        String GSTIN="33AEFPJ5208Q1ZB";

        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setTextSize(20);
        canvas.drawText(Address,20,200,titlePaint);
        canvas.drawText(Adddress1,20,225,titlePaint);
        canvas.drawText("Tel: "+Tel,20,250,titlePaint);
        canvas.drawText("Email: "+Email,20,275,titlePaint);
        canvas.drawText("GSTIN: "+GSTIN,20,300,titlePaint);

        Paint paint = new Paint();
        Rect r = new Rect(800, 200, 900, 100);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(r, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRect(r, paint);


        Cursor cursor = db.get_value("select max(Bill_No) from Transation");
        if (cursor != null) {
            cursor.moveToFirst();
            int id= cursor.getInt(0);
            Customer_Id=id+1;
            Bill_NO = id + 1;
        }

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(25f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Invoice No : "+Bill_NO,20,400,myPaint);
       // canvas.drawText("Customer Id: "+Customer_Id,20,590,myPaint);
        //canvas.drawText("Customer Name: "+Customer_Name,20,650,myPaint);
        //canvas.drawText("Phone No: "+PHone_NO,20,710,myPaint);


        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        java.util.Date date = new Date();

        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
        Date day = new Date();




        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(25f);
        myPaint.setColor(Color.BLACK);

        canvas.drawText("Invoice Date : "+formatter1.format(day),pageWidth-350,400,myPaint);
        //canvas.drawText("Time : "+formatter.format(date),pageWidth-350,710,myPaint);

        //box

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        canvas.drawRect(20,420,pageWidth-20,1260,myPaint);
        canvas.drawRect(20,420,pageWidth-20,470,myPaint);
        canvas.drawRect(20,420,100,1260,myPaint);
        canvas.drawRect(100,420,650,1260,myPaint);
        canvas.drawRect(650,420,825,1260,myPaint);
        canvas.drawRect(1000,420,pageWidth-20,1460,myPaint);

        canvas.drawRect(20,1260,pageWidth-20,1400,myPaint);
        canvas.drawRect(20,1400,pageWidth-20,1460,myPaint);
        canvas.drawLine(650,1260,650,1460,myPaint);
        //canvas.drawLine(600,1260,600,1460,myPaint);




        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("S.No.",pageWidth-1168,460,myPaint);
        canvas.drawText("Description",pageWidth-900,460,myPaint);
        canvas.drawText("Quantity",pageWidth-515,460,myPaint);
        canvas.drawText("Rate",pageWidth-275,460,myPaint);
        canvas.drawText("Amount",pageWidth-150,460,myPaint);


        int start_item1=50;
        int start_item2=200;
        int start_item3=400;
        int start_item4=700;
        int start_item5=850;
        int start_item6=1000;
        int end_item=500;

        Float Net_AMT = 0f;
        // max 12
        for (Float total :mTotal){

            Net_AMT = Net_AMT+total;

        }

        LinearLayout layout = activitySalesBinding.parentLinearLayout;
        int count= layout.getChildCount();
        long time= System.currentTimeMillis();
        for (int i=0;i<count;i++){

                update_stock(mProduct_Code.get(i), Integer.parseInt(mQty.get(i)));

                canvas.drawText(String.valueOf(i + 1), start_item1, end_item, myPaint);
                canvas.drawText(mProduct_name.get(i), start_item3, end_item, myPaint);
                canvas.drawText(mQty.get(i), start_item4, end_item, myPaint);
                canvas.drawText(mCost.get(i), start_item5, end_item, myPaint);
                canvas.drawText(String.valueOf(mTotal.get(i)), start_item6, end_item, myPaint);
                end_item = end_item + 70;


                db.insertData_to_trancation(Customer_Id,Bill_NO,mProduct_Code.get(i),mProduct_name.get(i),mQty.get(i),mCost.get(i),mTotal.get(i),Net_AMT,time,SPuser);

                db.insertData_to_Customer(Customer_Id,Customer_Name,PHone_NO);

        }


        canvas.drawText("Sub-Total",830,1300,myPaint);

        canvas.drawText(String.valueOf(Net_AMT),1010,1300,myPaint);

        canvas.drawText("18% IGST",750,1390,myPaint);
        canvas.drawText("Total Amount",830,1440,myPaint);


        titlePaint.setTextSize(25);
        titlePaint.setUnderlineText(true);
        canvas.drawText("OUR BANK DETAILS:",20,1500,titlePaint);
        titlePaint.setUnderlineText(false);
        canvas.drawText("FOR KIRTHANA AGENCIES",pageWidth-350,1500,titlePaint);


        myPaint.setTextSize(20f);
        canvas.drawText("KARUR VYSYA BANK,",20,1530,myPaint);
        canvas.drawText("ALANDUR BRANCH,",20,1555,myPaint);
        canvas.drawText("A/C Holder Name: KIRTHANA AGENCIES",20,1580,myPaint);
        canvas.drawText("CA A/C No.: 1104115000010212",20,1605,myPaint);
        canvas.drawText("IFSC CODE: KVBL0001104",20,1630,myPaint);


        document.finishPage(page);

        cusEdit.setText("");
        phoneEdit.setText("");

        String fileName="Invoice"+Bill_NO+".pdf";
        File file;
        try {
          File  dir= new File(Environment.getExternalStorageDirectory(),"DATA");
          if (!dir.exists()) {
          dir.mkdir();
          }
            file= new File(dir,fileName);
            // file= File.createTempFile(fileName, null, this.getCacheDir());
            FileOutputStream fos=new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(SalesActivity.this, R.string.successfully_generated,Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        removeView();

        Intent i=new Intent(this,PdfviewActivity.class);
        i.putExtra("File",file);
        i.putExtra("Filename",fileName);
        startActivity(i);

    }



    void Spinner_value(){

            Cursor c1 = db.get_value("SELECT Product_Name FROM Stock");
            if (c1.getCount()>0) {
                if (c1.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("Product_Name"));
                        mSpinner.add(data1);
                    } while (c1.moveToNext());
                }
            }
            else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Press OK to add product!");
                builder.setTitle("No products added...!");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                    Intent i = new Intent(SalesActivity.this, AddProductActivity.class);
                    startActivity(i);
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
    }



    private void update_stock(String mProductCode, int mQty) {
        Cursor c1 = db.get_value("SELECT  quantity FROM Stock WHERE Product_Code="+mProductCode);
        if (c1.moveToFirst()) {
            do {
                @SuppressLint("Range") int data1 = c1.getInt(c1.getColumnIndex("quantity"));
                int update_qty=data1-mQty;

                db.update_stock(Integer.parseInt(mProductCode),update_qty);

            } while (c1.moveToNext());
        }

    }
    void removeView(){

        View  inflater = LayoutInflater.from((Context)this).inflate(R.layout.row_add_language, null);
        LinearLayout layout = activitySalesBinding.parentLinearLayout;
        Intrinsics.checkNotNullExpressionValue(layout, "binding.parentLinearLayout");
        layout.removeAllViews();

    }


}