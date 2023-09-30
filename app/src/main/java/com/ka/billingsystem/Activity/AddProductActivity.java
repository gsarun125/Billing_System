package com.ka.billingsystem.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.databinding.ActivityAddProductBinding;


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

                        int Quantity1 = Integer.parseInt(Quantity);
                        int Cost1 = Integer.parseInt(Cost);
                        db.insertData(Product_Name, Quantity1, Cost1);

                        Binding.ProductEdit.setText("");
                        Binding.QuantityEdit.setText("");
                        Binding.CostEdit.setText("");

                        System.out.println(Product_Name);
                        System.out.println(Quantity);
                        System.out.println(Cost);
                    }
                }
            });


        }
    private boolean CheckAllFields() {

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




        String qurry2="Select Product_Name from Stock Where Product_Name="+"'"+Binding.ProductEdit.getText().toString()+"'";
        Cursor c2=db.get_value(qurry2);
        if (c2.getCount()>0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("The product Name already available!");
                    builder.setTitle("Alert !");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                        Binding.ProductEdit.setText("");
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    Binding.ProductEdit.setError("Enter different Product Code ");
                    Binding.ProductEdit.setFocusable(true);
                    Binding.ProductEdit.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(Binding.ProductEdit,InputMethodManager.SHOW_IMPLICIT);
                    return false;
            }


        return true;
    }


}
