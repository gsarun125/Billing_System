package com.mini.billingsystem.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.mini.billingsystem.DataBase.DataBaseHandler;
import com.mini.billingsystem.databinding.ActivityAddProductBinding;


public class AddProductActivity extends DrawerBaseActivity {

    int Product_id;
    private DataBaseHandler db = new DataBaseHandler(this);
    private ActivityAddProductBinding Binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = ActivityAddProductBinding.inflate(getLayoutInflater());


        setContentView(Binding.getRoot());
        getSupportActionBar().setTitle("Add Product");

        Cursor cursor = db.get_value("select max(Product_Id) from Stock");
        if (cursor != null) {
            cursor.moveToFirst();
            int id= cursor.getInt(0);
            Product_id=id+1;
            System.out.println(id);
        }

        Binding.IdText.setText(""+Product_id);

            Binding.get.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String Product_Name = Binding.ProductEdit.getText().toString();
                    String Quantity = Binding.QuantityEdit.getText().toString();
                    String Cost = Binding.CostEdit.getText().toString();

                    int Product_Name_len = Binding.ProductEdit.getText().toString().length();

                    int Quantity_len = Binding.QuantityEdit.getText().toString().length();
                    int Cost_len = Binding.CostEdit.getText().toString().length();


                    Binding.ProductEdit.setText("");
                    Binding.QuantityEdit.setText("");
                    Binding.CostEdit.setText("");

                    if (Product_Name_len != 0 && Quantity_len != 0 && Cost_len != 0) {

                        int Quantity1 = Integer.parseInt(Quantity);
                        int Cost1 = Integer.parseInt(Cost);
                        db.insertData(Product_Name, Quantity1, Cost1);
                        Product_id++;
                        Binding.IdText.setText(""+Product_id);
                        System.out.println(Product_Name);
                        System.out.println(Quantity);
                        System.out.println(Cost);
                        finish();
                    }
                    else {
                        Toast.makeText(AddProductActivity.this, "Enter value ", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

}
