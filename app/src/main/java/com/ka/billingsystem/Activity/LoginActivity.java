package com.ka.billingsystem.Activity;

import static com.ka.billingsystem.java.ImageEncodeAndDecode.encodeToBase64;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ka.billingsystem.R;
import com.ka.billingsystem.DataBase.DataBaseHandler;

import java.util.Locale;


public class LoginActivity extends AppCompatActivity {

    private DataBaseHandler db = new DataBaseHandler(this);
    EditText user_name;
    EditText password;
    LinearLayout Language;
    Button login;
    TextView ClickSignUP;
    TextView ForgotPassword;

    String SHARED_PREFS = "shared_prefs";
    String USER_KEY = "user_key";
    String PASSWORD_KEY = "password_key";
    String ADMIN_LOGIN = "admin_login";
    String SPuser;
    String SPpass;
    int invalid_login=0;
    private static final String SHARED_PREFS_KEY = "signature";
    private static final String SHARED_PREFS_Logo = "logo";
    String SPIS_FIRST_TIME;
    int checkedItem = 0;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        if (sharedpreferences.contains("checkeItem")) {
            String SPcheckedItem = sharedpreferences.getString("checkeItem", null);
            if (SPcheckedItem != null) {
                checkedItem = Integer.parseInt(SPcheckedItem);
                System.out.println(checkedItem);
            }
            lodeLocale();
        }


        setContentView(R.layout.activity_login);


        user_name = (EditText) findViewById(R.id.txtUserName);
        password = (EditText) findViewById(R.id.txtPassword);
        login = (Button) findViewById(R.id.btnLogin);
        ClickSignUP = (TextView) findViewById(R.id.txtClickSignUP);
        ForgotPassword=findViewById(R.id.ForgotPassword);
        Language=findViewById(R.id.language);
        Language.setVisibility(View.GONE);


        Drawable d = getResources().getDrawable(R.drawable.logo);
        Bitmap mBitmap = ((BitmapDrawable) d).getBitmap();
        String logo = encodeToBase64(mBitmap, Bitmap.CompressFormat.PNG, 100);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SHARED_PREFS_Logo, logo);
        editor.apply();

        SPuser = sharedpreferences.getString(USER_KEY, null);
        SPpass = sharedpreferences.getString(PASSWORD_KEY, null);
        SPIS_FIRST_TIME = sharedpreferences.getString(SHARED_PREFS_KEY, null);
        ClickSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        Language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowChangeLanguage();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = user_name.getText().toString();
                String pass = password.getText().toString();
                if (username.equals("") || pass.equals("")) {
                    Toast.makeText(LoginActivity.this,R.string.please_enter_all_the_fields, Toast.LENGTH_SHORT).show();
                } else {


              String qurry = "Select * from user where user_id=" + '"' + username + '"' + "and password=" + '"' + pass + '"';
                    System.out.println(qurry);
                    boolean checkuserpass;
                    Cursor c1 = db.get_value(qurry);
                    if       (c1.getCount() > 0)
                        checkuserpass = true;
                    else
                        checkuserpass = false;

                    if (checkuserpass) {
                        if (username.equals("admin")) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString(USER_KEY, username);

                            editor.putString(ADMIN_LOGIN,"true");
                            editor.apply();
                            Toast.makeText(LoginActivity.this,R.string.login_successfull, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),UserSelectionActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString(USER_KEY, username);
                            editor.putString(ADMIN_LOGIN,"false");
                            editor.apply();
                            Toast.makeText(LoginActivity.this,R.string.login_successfull, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        user_name.setText("");
                        password.setText("");
                        invalid_login++;

                        Toast.makeText(LoginActivity.this,R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
    public void onForgotPasswordClick(View view) {
        Intent i=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
        startActivity(i);

    }
    private void ShowChangeLanguage() {
        String[] lan = {"English", "தமிழ்"};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Choose Language...");

        alertDialogBuilder.setSingleChoiceItems(lan, checkedItem, (dialogInterface, i) -> {
            checkedItem = i;
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("checkeItem", String.valueOf(checkedItem));
            editor.apply();
            if (i == 0) {
                setLocale("Eng");
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else if (i == 1) {
                setLocale("ta");

                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            dialogInterface.dismiss();
        });
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
        });
        AlertDialog customAlertDialog = alertDialogBuilder.create();
        customAlertDialog.show();
    }
    public void lodeLocale() {

        String language = sharedpreferences.getString("My_lang", null);

        if (language != null) {
            System.out.println(language);
            setLocale(language);
        }
    }
    private void setLocale(String lan) {
        Locale local = new Locale(lan);
        Locale.setDefault(local);
        Configuration config = new Configuration();
        config.locale = local;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("My_lang", lan);
        editor.apply();

    }


}