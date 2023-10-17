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
import android.os.Build;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.databinding.ActivitySalesBinding;
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
    private List<String> mSpinner = new ArrayList();
    private List<Integer> mAvailable_qty = new ArrayList();

   int pageWidth=1200;
    int add_count=0;
    EditText qty;
    TextView Pcode;
   public String Customer_Name="";
   public String PHone_NO="";
   int Bill_NO;
   int end_item=500;

    int Customer_Id;

    public List<String> mQty = new ArrayList();

    public List<String> mProduct_name = new ArrayList();

    EditText cusEdit;
    EditText phoneEdit;
    public List<Long> mTotal = new ArrayList();


    public List<String> mProduct_Code = new ArrayList();

    public List<Long> mCost= new ArrayList();
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
                if(add_count<10 ) {
                    if (add_count!=0){
                        if (checkQTY(add_count-1)){
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

                qty = (EditText) inflater.findViewById(R.id.et_Qty);

                System.out.println(spinnerSelectedValue);
                if (!spinnerSelectedValue.equals("Select")) {
                    qty.setEnabled(true);
                } else {
                    qty.setHint("Qty");
                    qty.setEnabled(false);
                }
                if (spinnerSelectedValue!=getString(R.string.select)) {
                    Cursor c1 = db.get_value("SELECT  quantity FROM Stock WHERE Product_Name="+"'"+spinnerSelectedValue+"'");
                    if (c1.moveToFirst()) {
                        do {

                            @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("quantity"));

                            mAvailable_qty.add(layout.getChildCount()-1,Integer.valueOf(data2));
                          System.out.println();
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
    public  boolean checkQTY(int i){
        View v = null;
            v = this.activitySalesBinding.parentLinearLayout.getChildAt(i);
            qty=(EditText) v.findViewById(R.id.et_Qty);
            String QTY= qty.getText().toString();

        if (qty.getText().toString().length() == 0) {
            qty.setError(getString(R.string.net_quantity_is_required));
            qty.setFocusable(true);
            qty.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(qty, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (QTY.length()!=0) {
                int tempQTY = Integer.parseInt(qty.getText().toString());
                System.out.println(tempQTY);
                System.out.println(mAvailable_qty.get(i));
                if (mAvailable_qty.get(i) >= tempQTY && tempQTY!=0) {
                  //  mQty.add(QTY);
                } else {
                    qty.setError("invalid QTY");
                    qty.setText("");
                    return false;
                }
            }
    return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    void saveData() {
        LinearLayout layout = activitySalesBinding.parentLinearLayout;
       int count= layout.getChildCount();
       View v = null;
       for (int i=0;i<count;i++){

           v = this.activitySalesBinding.parentLinearLayout.getChildAt(i);

           qty=(EditText) v.findViewById(R.id.et_Qty);

           String QTY= qty.getText().toString();
           if (qty.getText().toString().length() == 0) {
               qty.setError(getString(R.string.net_quantity_is_required));
               qty.setFocusable(true);
               qty.requestFocus();
               InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.showSoftInput(qty, InputMethodManager.SHOW_IMPLICIT);
               return;
           }
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

                    Cursor c1 = db.get_value("SELECT  cost,Product_Id FROM Stock WHERE Product_Name="+"'"+ Position+"'");
                    if (c1.moveToFirst()) {
                        do {
                            @SuppressLint("Range") long data1 = c1.getLong(c1.getColumnIndex("cost"));
                            @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("Product_Id"));
                            mProduct_Code.add(data2);

                            mCost.add(data1);
                            Long addTotal = data1 * Long.parseLong(QTY);
                            mTotal.add(addTotal);
                        } while (c1.moveToNext());
                    }
                }
                else {
                    Toast.makeText(this, R.string.enter_all_the_value,Toast.LENGTH_SHORT).show();
                    return;
                }
           }
        PDF2();


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
        // after all validation return true.
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void PDF2(){
        DecimalFormat chosenFormat = new DecimalFormat("#,###");
        Customer_Name=cusEdit.getText().toString();
        PHone_NO=phoneEdit.getText().toString();
        mAvailable_qty.clear();
        add_count=0;

        PdfDocument document=new PdfDocument();
        Paint myPaint=new Paint();
        Paint titlePaint=new Paint();
        PdfDocument.PageInfo myPageInfo1=new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        PdfDocument.Page page=document.startPage(myPageInfo1);
        Canvas canvas=page.getCanvas();


        titlePaint.setTextAlign(Paint.Align.RIGHT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));

        titlePaint.setTextSize(110);


        myPaint.setColor(Color.rgb(132, 187, 60));

        titlePaint.setColor(Color.WHITE);

        canvas.drawRect(10,0,pageWidth-10,300,myPaint);

        myPaint.setColor(Color.rgb(0, 26, 35));

        canvas.drawRoundRect(new RectF(-450, 0, pageWidth/2-20, 300), 200, 200, myPaint);

        canvas.drawRoundRect(new RectF(-450, 1900, pageWidth/2+200, 2010), 100, 150, myPaint);

        canvas.drawText("INVOICE",pageWidth-30,200,titlePaint);


        //title
        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
        titlePaint.setTextSize(50);
        canvas.drawText("KIRTHANA AGENCIES",20,100,titlePaint);

        //Address
        String Address="#6,Alikhan Street, Alandur, Chennai-600 016,";
        String Adddress1="Tamil Nadu,India.";
        String Tel="+91 44 2231 4628";
        String Email="kirthana.agencics@outlook.com";
        String GSTIN="33AEFPJ5208Q1ZB";

        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        titlePaint.setTextSize(25);
        canvas.drawText(Address,20,150,titlePaint);
        canvas.drawText(Adddress1,20,175,titlePaint);
        canvas.drawText("Tel: "+Tel,20,1975,titlePaint);
        canvas.drawText("Email: "+Email,300,1975,titlePaint);
        canvas.drawText("GSTIN: "+GSTIN,20,250,titlePaint);



        myPaint.setColor(Color.rgb(0, 26, 35));
        //canvas.drawRect(20,420,pageWidth-20,1260,myPaint);

        canvas.drawRect(20,420,pageWidth-20,470,myPaint);
        myPaint.setColor(Color.rgb(132, 187, 60));

        canvas.drawRect(20,420,pageWidth/2+70,470,myPaint);


        //canvas.drawRect(20,420,100,1260,myPaint);
        //canvas.drawRect(100,420,650,1260,myPaint);
        //canvas.drawRect(650,420,825,1260,myPaint);
        //canvas.drawRect(1000,420,pageWidth-20,1460,myPaint);

        //canvas.drawRect(20,1260,pageWidth-20,1400,myPaint);
        //canvas.drawRect(20,1400,pageWidth-20,1460,myPaint);
        // canvas.drawLine(650,1260,650,1460,myPaint);
        //canvas.drawLine(600,1260,600,1460,myPaint);


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
        Date date = new Date();

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





        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setColor(Color.WHITE);
        myPaint.setTextSize(30f);

        canvas.drawText("S.No.",pageWidth-1168,460,myPaint);
        canvas.drawText("Description",pageWidth-900,460,myPaint);
        canvas.drawText("Quantity",pageWidth-515,460,myPaint);
        canvas.drawText("Rate",850,460,myPaint);
        canvas.drawText("Amount",1020,460,myPaint);


        int start_item1=50;
        int start_item2=200;
        int start_item3=150;
        int start_item4=700;
        int start_item5=850;
        int start_item6=1020;


        long Net_AMT = 0;
        // max 12
        for (Long total :mTotal){

            Net_AMT = Net_AMT+total;

        }

        myPaint.setColor(Color.BLACK);
        myPaint.setTextSize(25f);
        long IGST= (long) (Net_AMT*0.18);
        long TotalAmount=Net_AMT+IGST;
        LinearLayout layout = activitySalesBinding.parentLinearLayout;
        int count= layout.getChildCount();
        long time= System.currentTimeMillis();
        for (int i=0;i<count;i++){

            update_stock(mProduct_Code.get(i), Integer.parseInt(mQty.get(i)));

            canvas.drawText(String.valueOf(i + 1), start_item1, end_item, myPaint);
            //  canvas.drawText(mProduct_name.get(i), start_item3, end_item, myPaint);

            canvas.drawText(mQty.get(i), start_item4, end_item, myPaint);
            canvas.drawText(chosenFormat.format(mCost.get(i)), start_item5, end_item, myPaint);
            canvas.drawText(chosenFormat.format(mTotal.get(i)), start_item6, end_item, myPaint);
            print_next_line(canvas,myPaint,start_item3,end_item,500,mProduct_name.get(i));

            end_item = end_item + 70;


            db.insertData_to_trancation(Customer_Id,Bill_NO,mProduct_Code.get(i),mProduct_name.get(i),mQty.get(i),mCost.get(i),mTotal.get(i),Net_AMT,time,SPuser);

        }
        db.insertData_to_Customer(Customer_Id,Customer_Name,PHone_NO);


        canvas.drawText("Sub-Total",830,1300,myPaint);

        canvas.drawText(chosenFormat.format(Net_AMT),1010,1300,myPaint);

        canvas.drawText("18% IGST",750,1390,myPaint);
        canvas.drawText(chosenFormat.format(IGST),1010,1390,myPaint);
        myPaint.setColor(Color.rgb(132, 187, 60));
        canvas.drawRoundRect(new RectF(830, 1410, 1150, 1450), 6, 6, myPaint);
        myPaint.setColor(Color.WHITE);
        canvas.drawText("Total Amount",830,1440,myPaint);
        canvas.drawText(chosenFormat.format(TotalAmount),1010,1440,myPaint);

        myPaint.setColor(Color.BLACK);

        //canvas.drawText(numbertoword.convert((int) TotalAmount)+" Only",50,1300,myPaint);

        print_next_line(canvas,myPaint,50,1300,600,numbertoword.convert((int) TotalAmount)+" Only");

        canvas.drawText("DC No. : "+Bill_NO,50,1440,myPaint);
        canvas.drawText("DC Date : "+formatter1.format(day),350,1440,myPaint);



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
        myPaint.setTextSize(20f);
        myPaint.setColor(Color.rgb(132, 187, 60));
        canvas.drawText("Terms & Conditions",20,1660,myPaint);

        myPaint.setTextSize(15f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Goods once sold, cannot be taken back or exchanged",20,1680,myPaint);
        canvas.drawText("'Subject to Chennai jurisdiction only'",20,1700,myPaint);
        canvas.drawText("Our responsibility ceases after goods left our premises.",20,1720,myPaint);
        canvas.drawText("Buyer has to do transit insurance on their own.",20,1740,myPaint);

        document.finishPage(page);

        cusEdit.setText("");
        phoneEdit.setText("");

        mQty.clear();
        mTotal.clear();
        mProduct_name.clear();
        mCost.clear();
        end_item=500;


        String fileName="Invoice"+Bill_NO+".pdf";
        File file;
        try {
            File  dir= new File(Environment.getExternalStorageDirectory(),"DATA");
            if (!dir.exists()) {
                dir.mkdir();
            }
            file= new File(dir,fileName);

            //   System.out.println("Path"+file.getAbsolutePath());
            db.filePath(Bill_NO,file.getAbsolutePath());
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

   public void PDF1(){
        DecimalFormat chosenFormat = new DecimalFormat("#,###");
        Customer_Name=cusEdit.getText().toString();
        PHone_NO=phoneEdit.getText().toString();
        mAvailable_qty.clear();
        add_count=0;

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

        //canvas.drawRect(800,100,pageWidth-20,350,myPaint);

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
        canvas.drawText("Rate",pageWidth-310,460,myPaint);
        canvas.drawText("Amount",pageWidth-150,460,myPaint);


        int start_item1=50;
        int start_item2=200;
        int start_item3=150;
        int start_item4=700;
        int start_item5=850;
        int start_item6=1020;


        long Net_AMT = 0;
        // max 12
        for (Long total :mTotal){

            Net_AMT = Net_AMT+total;

        }

        long IGST= (long) (Net_AMT*0.18);
        long TotalAmount=Net_AMT+IGST;
        LinearLayout layout = activitySalesBinding.parentLinearLayout;
        int count= layout.getChildCount();
        long time= System.currentTimeMillis();
        for (int i=0;i<count;i++){

                update_stock(mProduct_Code.get(i), Integer.parseInt(mQty.get(i)));

                canvas.drawText(String.valueOf(i + 1), start_item1, end_item, myPaint);
              //  canvas.drawText(mProduct_name.get(i), start_item3, end_item, myPaint);

                canvas.drawText(mQty.get(i), start_item4, end_item, myPaint);
                canvas.drawText(chosenFormat.format(mCost.get(i)), start_item5, end_item, myPaint);
                canvas.drawText(chosenFormat.format(mTotal.get(i)), start_item6, end_item, myPaint);
                print_next_line(canvas,myPaint,start_item3,end_item,500,mProduct_name.get(i));

                end_item = end_item + 70;


               db.insertData_to_trancation(Customer_Id,Bill_NO,mProduct_Code.get(i),mProduct_name.get(i),mQty.get(i),mCost.get(i),mTotal.get(i),Net_AMT,time,SPuser);

        }
        db.insertData_to_Customer(Customer_Id,Customer_Name,PHone_NO);


        canvas.drawText("Sub-Total",830,1300,myPaint);

        canvas.drawText(chosenFormat.format(Net_AMT),1010,1300,myPaint);

        canvas.drawText("18% IGST",750,1390,myPaint);
        canvas.drawText(chosenFormat.format(IGST),1010,1390,myPaint);
        canvas.drawText("Total Amount",830,1440,myPaint);
        canvas.drawText(chosenFormat.format(TotalAmount),1010,1440,myPaint);

        //canvas.drawText(numbertoword.convert((int) TotalAmount)+" Only",50,1300,myPaint);

        print_next_line(canvas,myPaint,50,1300,600,numbertoword.convert((int) TotalAmount)+" Only");

        canvas.drawText("DC No. : "+Bill_NO,50,1440,myPaint);
        canvas.drawText("DC Date : "+formatter1.format(day),350,1440,myPaint);



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
        myPaint.setTextSize(15f);
        canvas.drawText("Goods once sold, cannot be taken back or exchanged",20,1680,myPaint);
        canvas.drawText("'Subject to Chennai jurisdiction only'",20,1700,myPaint);
        canvas.drawText("Our responsibility ceases after goods left our premises.",20,1720,myPaint);
        canvas.drawText("Buyer has to do transit insurance on their own.",20,1740,myPaint);

        document.finishPage(page);

        cusEdit.setText("");
        phoneEdit.setText("");

        mQty.clear();
        mTotal.clear();
        mProduct_name.clear();
        mCost.clear();
        end_item=500;


        String fileName="Invoice"+Bill_NO+".pdf";
        File file;
        try {
          File  dir= new File(Environment.getExternalStorageDirectory(),"DATA");
          if (!dir.exists()) {
          dir.mkdir();
          }
            file= new File(dir,fileName);

       //   System.out.println("Path"+file.getAbsolutePath());
          db.filePath(Bill_NO,file.getAbsolutePath());
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
                builder.setMessage(R.string.press_ok_to_add_printer_type);
                builder.setTitle(R.string.no_printer_type_added);
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                    Intent i = new Intent(SalesActivity.this, AddProductActivity.class);
                    startActivity(i);
                    super.finish();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
    }


    private void update_stock(String mProductCode, int mQty) {
        Cursor c1 = db.get_value("SELECT  quantity FROM Stock WHERE Product_Id="+mProductCode);
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

    void  print_next_line(Canvas canvas,Paint paint,float x,float y,float maxWidth,String multiLineText){


        StringBuilder currentLine = new StringBuilder();

        for (char c : multiLineText.toCharArray()) {
            float textWidth = paint.measureText(currentLine.toString() + c);

            if (textWidth < maxWidth) {
                // Add the character to the current line
                currentLine.append(c);
            } else {
                // Draw the current line and move to the next line
                canvas.drawText(currentLine.toString(), x, y, paint);
                y += paint.getTextSize() * 1.5f;
                end_item= (int) (end_item+paint.getTextSize() * 1.5);// Adjust line spacing as needed
                currentLine = new StringBuilder(String.valueOf(c));
            }
        }

        // Draw the remaining part of the text
        canvas.drawText("-"+currentLine.toString(), x, y, paint);

    }
}