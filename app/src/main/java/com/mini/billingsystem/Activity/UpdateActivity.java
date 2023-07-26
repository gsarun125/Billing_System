package com.mini.billingsystem.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mini.billingsystem.DataBase.DataBaseHandler;
import com.mini.billingsystem.databinding.ActivityUpdateBinding;

public class UpdateActivity extends DrawerBaseActivity {

    private DataBaseHandler db=new DataBaseHandler(this);
    private ActivityUpdateBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Product_Name =binding.ProductEdit.getText().toString();
                int Product_ID= Integer.parseInt(binding.IdEdit.getText().toString());
                int Quantity= Integer.parseInt(binding.QuantityEdit.getText().toString());
                int Cost= Integer.parseInt(binding.CostEdit.getText().toString());

                int Product_Name_len =binding.ProductEdit.getText().toString().length();
                int  Product_ID_len= binding.IdEdit.getText().toString().length();
                int Quantity_len= binding.QuantityEdit.getText().toString().length();
                int  Cost_len= binding.CostEdit.getText().toString().length();



                binding.ProductEdit.setText("");
                binding.IdEdit.setText("");
                binding.QuantityEdit.setText("");
                binding.CostEdit.setText("");
                if (Product_Name_len!=0 && Product_ID_len!=0 && Quantity_len!=0 && Cost_len!=0) {
                    db.update(Product_ID, Product_Name, Quantity, Cost);
                    System.out.println(Product_Name);
                    System.out.println(Product_ID);
                    System.out.println(Quantity);
                    System.out.println(Cost);
                }
                else {
                    Toast.makeText(UpdateActivity.this, "Enter value ", Toast.LENGTH_LONG).show();
                }
            }
        });



    }
}