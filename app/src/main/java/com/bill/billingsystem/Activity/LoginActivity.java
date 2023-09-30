package com.bill.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bill.billingsystem.R;
import com.bill.billingsystem.DataBase.DataBaseHandler;


public class LoginActivity extends AppCompatActivity {

    private DataBaseHandler db = new DataBaseHandler(this);
    EditText user_name;
    EditText password;
    Button login;
    TextView ClickSignUP;

    String SHARED_PREFS = "shared_prefs";
    String USER_KEY = "user_key";
    String PASSWORD_KEY = "password_key";
    String SPuser;
    String SPpass;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_name=(EditText) findViewById(R.id.txtUserName);
        password=(EditText) findViewById(R.id.txtPassword);
        login=(Button) findViewById(R.id.btnLogin);
        ClickSignUP=(TextView)findViewById(R.id.txtClickSignUP);

        sharedpreferences= getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        SPuser = sharedpreferences.getString(USER_KEY,null);
        SPpass = sharedpreferences.getString(PASSWORD_KEY, null);

        ClickSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=user_name.getText().toString();
                String pass=password.getText().toString();
                if (username.equals("")||pass.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                }
                else {


                     String qurry="Select * from user where user_name="+'"'+username+'"'+"and password="+'"'+pass+'"';
                   System.out.println(qurry);
                     boolean checkuserpass;
                     Cursor c1=db.get_value(qurry);
                     if(c1.getCount()>0)
                         checkuserpass=true;
                     else
                         checkuserpass= false;

                     if (checkuserpass) {
                         if(username.equals("admin")){
                             SharedPreferences.Editor editor= sharedpreferences.edit();

                             editor.putString(USER_KEY,username);
                             editor.putString(PASSWORD_KEY,pass);

                             editor.apply();
                         Toast.makeText(LoginActivity.this, "Login successfull", Toast.LENGTH_SHORT).show();
                         Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                         startActivity(intent);
                         finish();
                         }
                         else {
                             SharedPreferences.Editor editor = sharedpreferences.edit();

                             editor.putString(USER_KEY, username);
                             editor.putString(PASSWORD_KEY, pass);

                             editor.apply();
                             Toast.makeText(LoginActivity.this, "Login successfull", Toast.LENGTH_SHORT).show();
                             Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                             startActivity(intent);
                             finish();
                         }
                     } else {
                         Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                     }
                 }

                }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("hjhj");
        if (SPuser != null && SPpass != null) {
            if(SPuser.equals("admin")){
             Intent i = new Intent(this, MainActivity.class);
             startActivity(i);
             finish();
            }
            else {
                Intent i=new Intent(this,MainActivity2.class);
                startActivity(i);
                finish();
            }

        }

    }
}