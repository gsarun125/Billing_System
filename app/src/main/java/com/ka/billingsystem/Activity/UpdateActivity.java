package com.ka.billingsystem.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ka.billingsystem.R;
import com.ka.billingsystem.databinding.ActivityUpdateBinding;
import com.ka.billingsystem.DataBase.DataBaseHandler;

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity  {

    private DataBaseHandler db=new DataBaseHandler(this);
    private ActivityUpdateBinding binding;
    private List<String> mSpinner = new ArrayList();
    Spinner spinner;
    String Product_Name;
    int NetQty;
    int UnitCost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mSpinner.add(getString(R.string.select));
        Spinner_value();
       // getSupportActionBar().setTitle("Update");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSpinner);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );


        spinner = (Spinner) findViewById(R.id.PSpinner);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner.setSelection(i);
                String spinnerSelectedValue = adapterView.getItemAtPosition(i).toString();

                System.out.println(spinnerSelectedValue);
                Product_Name=spinnerSelectedValue;
                if (spinnerSelectedValue!=getString(R.string.select)) {
                    Cursor c1 = db.get_value("SELECT quantity,cost FROM Stock WHERE Product_Name="+"'"+spinnerSelectedValue+"'");
                    if (c1.moveToFirst()) {
                        do {


                            @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("quantity"));
                            @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("cost"));


                            if (!data1.equals("0")) {
                                String available = "Available QTY ";
                                binding.QuantityEdit.setHint(available + data1);

                            }else {
                                binding.QuantityEdit.setHint("No Stock Available ");
                            }
                            binding.CostEdit.setHint(data2);

                        } while (c1.moveToNext());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {






                if (CheckAllFields()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);

                    builder.setMessage("Do you want to Update the Product");
                    builder.setTitle("Alert...!");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NetQty= Integer.parseInt(binding.QuantityEdit.getText().toString());
                            UnitCost= Integer.parseInt(binding.CostEdit.getText().toString());

                            db.update(Product_Name, NetQty, UnitCost);

                            binding.QuantityEdit.setText("");
                            binding.CostEdit.setText("");
                        }
                    });
                  builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            binding.QuantityEdit.setText("");
                            binding.CostEdit.setText("");

                            dialog.cancel();

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    private boolean CheckAllFields() {

        System.out.println(Product_Name);
        if (Product_Name.equals("Select")) {
            System.out.println("fjfjf");
            TextView errorText = (TextView)spinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select Product Name");
            binding.PSpinner.setFocusable(true);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.PSpinner, InputMethodManager.SHOW_IMPLICIT);

            return false;
        }

        if (binding.QuantityEdit.getText().toString().length() == 0) {
            binding.QuantityEdit.setError("Net Quantity is required");
            binding.QuantityEdit.setFocusable(true);
            binding.QuantityEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.QuantityEdit, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        // after all validation return true.
        return true;
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
                Intent i = new Intent(UpdateActivity.this, AddProductActivity.class);
                startActivity(i);
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public  void getInput(){

    }
}