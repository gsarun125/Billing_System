package com.ka.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.java.Export;
import com.ka.billingsystem.java.Import;
import com.ka.billingsystem.model.OnPdfFileSelectListener;
import com.ka.billingsystem.model.UserseclectionAdapter;
import com.ka.billingsystem.model.selectionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserSelectionActivity extends AppCompatActivity implements selectionListener {
    private DataBaseHandler db = new DataBaseHandler(this);

    private RecyclerView recyclerView;
    private UserseclectionAdapter userseclectionAdapter;
    private List<String> mUsername = new ArrayList();
    private List<String> mUserid = new ArrayList();
    private List<String> mGen_Date = new ArrayList();
    SharedPreferences sharedpreferences;
    String USER_KEY = "user_key";
    String SHARED_PREFS = "shared_prefs";
    ImageButton threedot;
    int checkedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
        recyclerView=findViewById(R.id.userSelection_RV);
        sharedpreferences= getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        displayPdf();
    }

    private void displayPdf(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mGen_Date.clear();
        mUserid.clear();
        mUsername.clear();
        Cursor c1;
        c1 = db.get_value("SELECT * FROM user WHERE user_id <> 'admin'");
        if (c1.moveToFirst()) {
            do {
                @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("user_name"));
                @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("user_id"));
                @SuppressLint("Range") Long data3 = c1.getLong(c1.getColumnIndex("created_date"));
                SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                Date res = new Date(data3);
                mUsername.add(data1);
                mUserid.add(data2);
                mGen_Date.add(formatter1.format(res));
            }while (c1.moveToNext());
         }
        userseclectionAdapter=new UserseclectionAdapter(this,this,mUsername,mUserid,mGen_Date);
        recyclerView.setAdapter(userseclectionAdapter);

    }

    public void lodeLocale() {
        SharedPreferences prefs = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_lang", "");
        if (language != null) {
            setLocale(language);
        }
    }

    @Override
    public void onpdfSelected(String userid) {
        SharedPreferences sharedpreferences;
        String USER_KEY = "user_key";
        String SHARED_PREFS = "shared_prefs";
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USER_KEY, userid);
        editor.apply();
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }

    private void ShowChangeLanguage() {
        String[] lan = {"English", "தமிழ்"};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.choose_language));
        System.out.println(checkedItem);
        alertDialogBuilder.setSingleChoiceItems(lan, checkedItem, (dialogInterface, i) -> {
            checkedItem = i;
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("checkeItem", String.valueOf(checkedItem));
            editor.apply();
            if (i == 0) {
                setLocale("Eng");
                Intent intent = new Intent(this, UserSelectionActivity.class);
                finish();
                startActivity(intent);
            } else if (i == 1) {
                setLocale("ta");

                Intent intent = new Intent(this, UserSelectionActivity.class);
                finish();
                startActivity(intent);
            }
            dialogInterface.dismiss();
        });
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
        });
        AlertDialog customAlertDialog = alertDialogBuilder.create();
        customAlertDialog.show();
    }

    private void setLocale(String lan) {
        Locale local = new Locale(lan);
        Locale.setDefault(local);
        Configuration config = new Configuration();
        config.locale = local;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("My_lang", lan);
        editor.apply();
    }

    @Override
    public void onBackPressed(){
        logOut();
    }

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.press_ok_to_logout);
        builder.setTitle(R.string.alert);
        builder.setCancelable(true);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.you_press_cancel_button, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove("user_key");
                editor.remove("password_key");
                editor.apply();

                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.locale != null) {
            Locale locale = new Locale(newConfig.locale.getLanguage());
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        }
    }

}
