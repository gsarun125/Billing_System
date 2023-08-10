package com.mini.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.mini.billingsystem.R;

import java.io.File;

public class HisPdfviewActivity extends AppCompatActivity {

    Long to_time;
    Long from_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_pdfview);
        Intent i=getIntent();
        to_time=i.getExtras().getLong("to_time");
        from_time=i.getExtras().getLong("from");
        System.out.println(to_time);
        System.out.println(from_time);



    }
}