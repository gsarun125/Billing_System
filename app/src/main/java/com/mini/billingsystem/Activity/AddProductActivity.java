package com.mini.billingsystem.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mini.billingsystem.DataBase.DataBaseHandler;
import com.mini.billingsystem.databinding.ActivityAddProductBinding;


public class AddProductActivity extends AppCompatActivity {


    private DataBaseHandler db = new DataBaseHandler(this);
    private ActivityAddProductBinding Binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = ActivityAddProductBinding.inflate(getLayoutInflater());


        setContentView(Binding.getRoot());
        //getSupportActionBar().setTitle("Add Product");

      /*  Cursor cursor = db.get_value("select max(Product_Id) from Stock");
        if (cursor != null) {
            cursor.moveToFirst();
            int id= cursor.getInt(0);
            Product_id=id+1;
            System.out.println(id);
        }

       */



            Binding.get.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (CheckAllFields()) {

                        String Product_Name = Binding.ProductEdit.getText().toString();
                        String Quantity = Binding.QuantityEdit.getText().toString();
                        String Cost = Binding.CostEdit.getText().toString();
                        String Product_Code=Binding.codeEdit.getText().toString();

                        int Quantity1 = Integer.parseInt(Quantity);
                        int Cost1 = Integer.parseInt(Cost);
                        db.insertData(Product_Code,Product_Name, Quantity1, Cost1);

                        Binding.ProductEdit.setText("");
                        Binding.QuantityEdit.setText("");
                        Binding.CostEdit.setText("");
                        Binding.codeEdit.setText("");
                        System.out.println(Product_Name);
                        System.out.println(Quantity);
                        System.out.println(Cost);
                    }
                }
            });


        }
    private boolean CheckAllFields() {
        if (Binding.codeEdit.length() == 0) {
            Binding.codeEdit.setError("Product Code is required");
            Binding.codeEdit.setFocusable(true);
            Binding.codeEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Binding.codeEdit,InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (Binding.ProductEdit.length() == 0) {
            Binding.ProductEdit.setError("Product Name is required");
            Binding.ProductEdit.setFocusable(true);
            Binding.ProductEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Binding.ProductEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (Binding.QuantityEdit.length() ==0) {
            Binding.QuantityEdit.setError("Quantity is required");
            Binding.QuantityEdit.setFocusable(true);
            Binding.QuantityEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Binding.QuantityEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (Binding.CostEdit.length() == 0) {
            Binding.CostEdit.setError("Re-type password is required");
            Binding.CostEdit.setFocusable(true);
            Binding.CostEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Binding.CostEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        String qurry="Select Product_Code from Stock Where Product_Code="+Binding.codeEdit.getText().toString();
        System.out.println(qurry);
        boolean checkuserpass;
        Cursor c1=db.get_value(qurry);
        if(c1.getCount()>0) {
            System.out.println("6");
            checkuserpass = true;
        }
        else
            checkuserpass= false;

        if (checkuserpass){
            System.out.println("7");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The product code already available!");
            builder.setTitle("Alert !");
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                Binding.codeEdit.setText("");
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Binding.codeEdit.setError("Enter different Product Code ");
            Binding.codeEdit.setFocusable(true);
            Binding.codeEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Binding.codeEdit,InputMethodManager.SHOW_IMPLICIT);


            return false;
        }

        // after all validation return true.
        return true;
    }


}
