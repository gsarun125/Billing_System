package com.bill.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bill.billingsystem.DataBase.DataBaseHandler;
import com.bill.billingsystem.R;
import com.bill.billingsystem.model.UpdateAdapter;
import com.bill.billingsystem.model.UpdateListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity2 extends AppCompatActivity implements UpdateListener {
    private RecyclerView recyclerView;
    private DataBaseHandler db=new DataBaseHandler(this);
    List<String> mpname = new ArrayList();
    List<String> mqty = new ArrayList();
    List<String> mcost = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update2);
        recyclerView = findViewById(R.id.recyclerview);

        load();
    }

    private void load() {
        mpname.clear();
        mcost.clear();
        mqty.clear();
        Cursor c1 = db.get_value("SELECT * FROM Stock");
        if (c1.moveToFirst()) {
            do {

                @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("Product_Name"));
                @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("quantity"));
                @SuppressLint("Range") String data3 = c1.getString(c1.getColumnIndex("cost"));


                if (!data2.equals("0")) {
                    mqty.add(data2);
                }else {
                    mqty.add("No Stock Available ");
                }
                mpname.add(data1);
                mcost.add(data3);

            } while (c1.moveToNext());
        }
        System.out.println(mcost);

        System.out.println(mqty);

        System.out.println(mpname);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        UpdateAdapter updateAdapter=new UpdateAdapter(this,UpdateActivity2.this,mpname,mqty,mcost);
        recyclerView.setAdapter(updateAdapter);

    }


    @Override
    public void onClick(String pname, String qty, String cost) {
        System.out.println(pname);
        System.out.println(qty);
        System.out.println(cost);
        showCustomDialog(pname);
    }

    void showCustomDialog(String pname) {
        final Dialog dialog = new Dialog(UpdateActivity2.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);
        final EditText QtyEt = dialog.findViewById(R.id.qty_et);
        final EditText CostEt = dialog.findViewById(R.id.cost_et);
        final TextView pnameET = dialog.findViewById(R.id.pname_et);
        pnameET.setText(pname);

        Button submitButton = dialog.findViewById(R.id.submit_button);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String netQty = QtyEt.getText().toString();
                String unitcost = CostEt.getText().toString();

                if(CheckAllFields(QtyEt,CostEt)) {
                    db.update(pname, Integer.parseInt(netQty), Integer.parseInt(unitcost));
                    load();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }

    private boolean CheckAllFields(EditText QtyEt,EditText CostEt) {

        if (QtyEt.getText().toString().length() == 0) {
            QtyEt.setError("Net Quantity is required");
            QtyEt.setFocusable(true);
            QtyEt.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(QtyEt, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        if (CostEt.getText().toString().length() == 0) {
            CostEt.setError("Unit Cost is required");
            CostEt.setFocusable(true);
            CostEt.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(CostEt, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        return true;
    }

}