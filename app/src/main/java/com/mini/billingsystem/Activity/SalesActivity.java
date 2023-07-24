package com.mini.billingsystem.Activity;



import android.os.Bundle;

import com.mini.billingsystem.Activity.DrawerBaseActivity;
import com.mini.billingsystem.databinding.ActivitySalesBinding;

public class SalesActivity extends DrawerBaseActivity {
    ActivitySalesBinding activitySalesBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySalesBinding = ActivitySalesBinding.inflate(getLayoutInflater());
        setContentView(activitySalesBinding.getRoot());
    }
}