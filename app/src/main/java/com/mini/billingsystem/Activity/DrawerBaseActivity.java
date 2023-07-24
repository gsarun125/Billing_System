package com.mini.billingsystem.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;
import com.mini.billingsystem.R;
import com.mini.billingsystem.UpdateActivity;


public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private int id;

    @Override
    public void setContentView(View view) {
        drawerLayout=(DrawerLayout)getLayoutInflater().inflate(R.layout.activity_drawer_base,null);
        FrameLayout comtainer=drawerLayout.findViewById(R.id.activiyContainer);
        comtainer.addView(view);
        super.setContentView(drawerLayout);
        Toolbar toolbar=drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        NavigationView navigationView=drawerLayout.findViewById(R.id.nav_View);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.menu_drawer_open,R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        id=item.getItemId();
        System.out.println(id);
        switch (id){
            case 2131231065:
                Intent i=new Intent(this, SalesActivity.class);
                overridePendingTransition(0,0);
                startActivity(i);
                break;

            case 2131230721:
                Intent i1=new Intent(this, AddProductActivity.class);
               overridePendingTransition(0,0);
                startActivity(i1);
                break;

            case 2131231219:
            Intent i2=new Intent(this, UpdateActivity.class);
            overridePendingTransition(0,0);
            startActivity(i2);
            break;

            case 2131231220:
                Intent i3=new Intent(this ,StockActivity.class);
                overridePendingTransition(0,0);
                startActivity(i3);
                break;
        }
        return true;
    }
    protected  void  ActivityTitle(String title){
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(title);
        }
    }
}