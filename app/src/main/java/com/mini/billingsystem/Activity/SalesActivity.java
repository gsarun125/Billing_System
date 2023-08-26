package com.mini.billingsystem.Activity;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.mini.billingsystem.DataBase.DataBaseHandler;
import com.mini.billingsystem.R;
import com.mini.billingsystem.databinding.ActivitySalesBinding;

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
    public  ActivitySalesBinding activitySalesBinding;
    private List<String> mSpinner = new ArrayList();
    private List<Integer> mAvailable_qty = new ArrayList();

    int pageWidth=1200;
    int add_count=0;
    EditText qty;
   public String Customer_Name="";
   public String PHone_NO="";
   public String Customer_Id="";
   int Bill_NO;
   private TextView  p_name;
    public List<String> mQty = new ArrayList();

    public List<String> mProduct_name = new ArrayList();

    EditText cusEdit;
    EditText phoneEdit;
    EditText cusidEdit;
    public List<Float> mTotal = new ArrayList();


    public List<String> mProduct_Code = new ArrayList();

    public List<String> mCost= new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySalesBinding = ActivitySalesBinding.inflate(getLayoutInflater());

        //getSupportActionBar().setTitle("Sales");
        setContentView(activitySalesBinding.getRoot());

        mAvailable_qty.clear();
        cusEdit=(EditText) findViewById(R.id.cusName);
        cusidEdit = (EditText) findViewById(R.id.cusid);
        phoneEdit=(EditText) findViewById(R.id.PhoneNo);

        mSpinner.add(getString(R.string.select));

        Spinner_value();
           activitySalesBinding.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(add_count<10) {
                    mQty.clear();
                    mProduct_Code.clear();
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
                    Cursor c1 = db.get_value("SELECT  Product_Name,quantity FROM Stock WHERE Product_Code=" + spinnerSelectedValue);
                    if (c1.moveToFirst()) {
                        do {
                            p_name = (TextView) inflater.findViewById(R.id.productname);
                            p_name.setText("");

                            qty = (EditText) inflater.findViewById(R.id.et_Qty);

                            @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("Product_Name"));

                            @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("quantity"));
                            p_name.setText(data1);
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


           p_name=(TextView) v.findViewById(R.id.productname);
           mProduct_name.add(p_name.getText().toString());


           String QTY= qty.getText().toString();
           if (QTY.length()!=0) {
               int tempQTY = Integer.parseInt(qty.getText().toString());
               System.out.println(tempQTY);
               System.out.println(mAvailable_qty.get(i));
               if (mAvailable_qty.get(i) > tempQTY) {
                   mQty.add(QTY);
               } else {
                   qty.setError("invalid QTY");
                   return;
               }
           }


           Spinner spinner = (Spinner) v.findViewById(R.id.sproductid);
           String Position = spinner.getSelectedItem().toString();

                if(Position.length()!=0& QTY.length()!=0) {

                    mProduct_Code.add(Position);
                    Cursor c1 = db.get_value("SELECT  cost FROM Stock WHERE Product_Code=" + Position);
                    if (c1.moveToFirst()) {
                        do {
                            @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("cost"));
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

        if (cusidEdit.length() == 0) {
            cusidEdit.setError("Customer ID is required");
            cusidEdit.setFocusable(true);
            cusidEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(cusidEdit, InputMethodManager.SHOW_IMPLICIT);
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
        Customer_Id=cusidEdit.getText().toString();
        PHone_NO=phoneEdit.getText().toString();
        mAvailable_qty.clear();
        PdfDocument document=new PdfDocument();
        Paint myPaint=new Paint();
        Paint titlePaint=new Paint();

        PdfDocument.PageInfo myPageInfo1=new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        PdfDocument.Page page=document.startPage(myPageInfo1);
        Canvas canvas=page.getCanvas();

        //title
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setTextSize(70);
        canvas.drawText("Shopping Center",pageWidth/2,270,titlePaint);

        myPaint.setColor(Color.rgb(0,133,200));
        myPaint.setTextSize(30);
        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Call:9443528960",1160,40,myPaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
        titlePaint.setTextSize(70);
        canvas.drawText("--------------------------------------------------",pageWidth/2,400,titlePaint);


        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
        titlePaint.setTextSize(70);
        canvas.drawText("Invoice",pageWidth/2,500,titlePaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Customer Id: "+Customer_Id,20,590,myPaint);
        canvas.drawText("Customer Name: "+Customer_Name,20,650,myPaint);
        canvas.drawText("Phone No: "+PHone_NO,20,710,myPaint);


        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        java.util.Date date = new Date();

        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
        Date day = new Date();

        Cursor cursor = db.get_value("select max(Bill_No) from Transation");
        if (cursor != null) {
            cursor.moveToFirst();
            int id= cursor.getInt(0);
            Bill_NO = id + 1;
        }




        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Bill No : "+Bill_NO,pageWidth-350,590,myPaint);
        canvas.drawText("Date : "+formatter1.format(day),pageWidth-350,650,myPaint);
        canvas.drawText("Time : "+formatter.format(date),pageWidth-350,710,myPaint);

        canvas.drawText("--------------------------------------------------",pageWidth/2,780,titlePaint);

        //box
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        canvas.drawRect(20,800,pageWidth-20,860,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("Sl.No.",pageWidth-1168,850,myPaint);
        canvas.drawText("Pid",pageWidth-1000,850,myPaint);
        canvas.drawText("Particulars",pageWidth-792,850,myPaint);
        canvas.drawText("Qty",pageWidth-500,850,myPaint);
        canvas.drawText("Rate",pageWidth-376,850,myPaint);
        canvas.drawText("Amount",pageWidth-198,850,myPaint);

        canvas.drawLine(pageWidth-1168+120,800,pageWidth-1168+120,860,myPaint);
        canvas.drawLine(pageWidth-970+100,800,pageWidth-970+100,860,myPaint);
        canvas.drawLine(pageWidth-792+248,800,pageWidth-792+248,860,myPaint);
        canvas.drawLine(pageWidth-500+100,800,pageWidth-500+100,860,myPaint);
        canvas.drawLine(pageWidth-376+148,800,pageWidth-376+148,860,myPaint);

        int start_item1=50;
        int start_item2=200;
        int start_item3=400;
        int start_item4=700;
        int start_item5=850;
        int start_item6=1000;
        int end_item=950;

        Float Net_AMT = 0f;
        // max 12
        for (Float total :mTotal){

            Net_AMT = Net_AMT+total;

        }

        LinearLayout layout = activitySalesBinding.parentLinearLayout;
        int count= layout.getChildCount();

        long time= System.currentTimeMillis();
        for (int i=0;i<count;i++){
            try {


                update_stock(mProduct_Code.get(i), Integer.parseInt(mQty.get(i)));

                canvas.drawText(String.valueOf(i + 1), start_item1, end_item, myPaint);
                canvas.drawText(mProduct_Code.get(i), start_item2, end_item, myPaint);
                canvas.drawText(mProduct_name.get(i), start_item3, end_item, myPaint);
                canvas.drawText(mQty.get(i), start_item4, end_item, myPaint);
                canvas.drawText(mCost.get(i), start_item5, end_item, myPaint);
                canvas.drawText(String.valueOf(mTotal.get(i)), start_item6, end_item, myPaint);
                end_item = end_item + 70;


                db.insertData_to_trancation(Customer_Id,Bill_NO,mProduct_Code.get(i),mProduct_name.get(i),mQty.get(i),mCost.get(i),mTotal.get(i),Net_AMT,time);

                db.insertData_to_Customer(Customer_Id,Customer_Name,PHone_NO);

            }
            catch (Exception e){
                Toast.makeText(this, R.string.enter_the_value,Toast.LENGTH_SHORT).show();
                return;
            }
        }

        canvas.drawLine(680,end_item,pageWidth-20,end_item,myPaint);
        end_item=end_item+50;

        canvas.drawText("Net Amt",730,end_item,myPaint);
        canvas.drawText(":",970,end_item,myPaint);
        canvas.drawText(String.valueOf(Net_AMT),1000,end_item,myPaint);
        end_item=end_item+30;
        canvas.drawLine(680,end_item,pageWidth-20,end_item,myPaint);

        end_item=end_item+70;
        titlePaint.setTextSize(30);
        canvas.drawText("**THANK YOUR VISIT**",pageWidth/2,end_item,titlePaint);



        document.finishPage(page);

        cusEdit.setText("");
        phoneEdit.setText("");
        cusidEdit.setText("");

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

            Cursor c1 = db.get_value("SELECT Product_Code FROM Stock");
            if (c1.getCount()>0) {
                if (c1.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("Product_Code"));
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