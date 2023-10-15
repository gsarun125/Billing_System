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
import com.ka.billingsystem.R;
import com.ka.billingsystem.databinding.ActivityAddProductBinding;


public class AddProductActivity extends AppCompatActivity {


    private DataBaseHandler db = new DataBaseHandler(this);
    private ActivityAddProductBinding Binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = ActivityAddProductBinding.inflate(getLayoutInflater());


        setContentView(Binding.getRoot());

        Binding.ProductEdit.setFocusable(true);
        Binding.ProductEdit.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(Binding.ProductEdit, InputMethodManager.SHOW_IMPLICIT);



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
                        Binding.QuantityEdit.setText("1");
                        Binding.CostEdit.setText("");
                        Binding.ProductEdit.setFocusable(true);
                        Binding.ProductEdit.requestFocus();


                    }
                }
            });


        }
    private boolean CheckAllFields() {

        if (Binding.ProductEdit.length() == 0) {
            Binding.ProductEdit.setError(getString(R.string.printer_type_is_required));
            Binding.ProductEdit.setFocusable(true);
            Binding.ProductEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Binding.ProductEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (Binding.QuantityEdit.length() ==0) {
            Binding.QuantityEdit.setError(getString(R.string.quantity_is_required));
            Binding.QuantityEdit.setFocusable(true);
            Binding.QuantityEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Binding.QuantityEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (Binding.CostEdit.length() == 0) {
            Binding.CostEdit.setError(getString(R.string.cost_is_required));
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
                    builder.setMessage(R.string.the_printer_type_already_available);
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
