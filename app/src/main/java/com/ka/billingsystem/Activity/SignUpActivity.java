package com.ka.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.ka.billingsystem.R;
import com.ka.billingsystem.DataBase.DataBaseHandler;

public class SignUpActivity extends AppCompatActivity {

    private DataBaseHandler db = new DataBaseHandler(this);
    private EditText username;
    private EditText newpassword;
    private EditText retypePassword;
    private Button BtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username=(EditText) findViewById(R.id.txtUsername);
        newpassword=(EditText) findViewById(R.id.newpassword);
        retypePassword=(EditText) findViewById(R.id.retypepassword);
        BtnSignUp=(Button) findViewById(R.id.btnSignUp);


        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckAllFields()){
                    String Uname=username.getText().toString();
                    String password=newpassword.getText().toString();
                    long time= System.currentTimeMillis();
                    db.insertData_to_user(Uname,password,time,time);
                    Intent i=new Intent(SignUpActivity.this,LoginActivity.class);
                    startActivity(i);

                }
            }
        });


    }
    private boolean CheckAllFields() {
        if (username.length() == 0) {
            username.setError("User Name is required");
            username.setFocusable(true);
            username.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(username, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (newpassword.length() == 0) {
            newpassword.setError("New password is required");
            newpassword.setFocusable(true);
            newpassword.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(newpassword, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        if (newpassword.length() < 8) {
            newpassword.setError("New password must 8 letters");
            newpassword.setFocusable(true);
            newpassword.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(newpassword, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (retypePassword.length() == 0) {
            retypePassword.setError("Re-type password is required");
            retypePassword.setFocusable(true);
            retypePassword.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(retypePassword, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        if (!newpassword.getText().toString().equals(retypePassword.getText().toString())){
            retypePassword.setText("");
            newpassword.setText("");
            newpassword.setError("New password and Re-type password doesn't match");
            newpassword.setFocusable(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(newpassword, InputMethodManager.SHOW_IMPLICIT);

            return false;
        }
        // after all validation return true.
        return true;
    }

}