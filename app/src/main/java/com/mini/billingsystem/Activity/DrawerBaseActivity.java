package com.mini.billingsystem.Activity;

import static com.mini.billingsystem.R.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;
import com.mini.billingsystem.R;


public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private int id;
    @Override
    public void setContentView(View view) {
        drawerLayout=(DrawerLayout)getLayoutInflater().inflate(layout.activity_drawer_base,null);
        FrameLayout comtainer=drawerLayout.findViewById(R.id.activiyContainer);
        comtainer.addView(view);
        super.setContentView(drawerLayout);
        Toolbar toolbar=drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        NavigationView navigationView=drawerLayout.findViewById(R.id.nav_View);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar, string.menu_drawer_open, string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
       id=item.getItemId();
        System.out.println(id);
            if( R.id.sales==id) {
                Intent i = new Intent(this, SalesActivity.class);
                overridePendingTransition(0, 0);
                startActivity(i);
            }
            else if (R.id.Add==id){
                Intent i1=new Intent(this, AddProductActivity.class);
               overridePendingTransition(0,0);
                startActivity(i1);
            } else if (R.id.Update==id) {
                Intent i2=new Intent(this, UpdateActivity.class);
                overridePendingTransition(0,0);
                startActivity(i2);

            } else if (R.id.Stock==id) {

                Intent i3=new Intent(this ,StockActivity.class);
                overridePendingTransition(0,0);
                startActivity(i3);
            }

        return true;
    }
    protected  void  ActivityTitle(String title){
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(title);
        }
    }
}