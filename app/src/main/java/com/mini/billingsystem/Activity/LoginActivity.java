package com.mini.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mini.billingsystem.DataBase.DataBaseHandler;
import com.mini.billingsystem.R;

public class LoginActivity extends AppCompatActivity {

    private DataBaseHandler db = new DataBaseHandler(this);
    EditText user_name;
    EditText password;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_name=(EditText) findViewById(R.id.txtUserName);
        password=(EditText) findViewById(R.id.txtPassword);
        login=(Button) findViewById(R.id.btnLogin);
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
                         Toast.makeText(LoginActivity.this, "Login successfull", Toast.LENGTH_SHORT).show();
                         Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                         startActivity(intent);
                     } else {
                         Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                     }
                 }

                }
        });
    }
}