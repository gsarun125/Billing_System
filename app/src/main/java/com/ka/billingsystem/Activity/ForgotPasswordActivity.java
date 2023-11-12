package com.ka.billingsystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;


public class ForgotPasswordActivity extends AppCompatActivity {
    private DataBaseHandler db = new DataBaseHandler(this);
    EditText User_Id;
    EditText New_Password;
    EditText RetypePassword;
    Button Submit;
    String UserId;
    String NewPassword;
    String ReTypePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        User_Id=findViewById(R.id.UserName);
        New_Password=findViewById(R.id.NewPassword);

        Submit=findViewById(R.id.btnSubmit);
        RetypePassword=findViewById(R.id.RePassword);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserId=User_Id.getText().toString();
                NewPassword=New_Password.getText().toString();
                ReTypePassword=RetypePassword.getText().toString();

                if (CheckAllFields()){

                    db.ForgotPasswordChange(UserId,NewPassword);
                    Toast.makeText(ForgotPasswordActivity.this,"Your password has been changed", Toast.LENGTH_SHORT).show();
                   onBackPressed();
                }
            }
        });
    }
    private boolean CheckAllFields() {
        if (User_Id.length() == 0) {
            User_Id.setError("User Id is required");
            User_Id.setFocusable(true);
            User_Id.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(User_Id, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        String useridcheck=User_Id.getText().toString();
        String qurry = "Select * from user where user_id='"+ useridcheck + "'";
        boolean checkuserpass;
        Cursor c1 = db.get_value(qurry);
        if  (c1.getCount() > 0)
            checkuserpass = true;
        else
            checkuserpass = false;

        if (!checkuserpass){
            User_Id.setText("");
            New_Password.setText("");
       //     retypePassword.setText("");
            User_Id.setError("User id Not found");
            User_Id.setFocusable(true);
            User_Id.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(User_Id, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }


        if (NewPassword.length() == 0) {
            New_Password.setError("New Password is required");
            New_Password.setFocusable(true);
            New_Password.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(New_Password, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (NewPassword.length() < 8) {
            New_Password.setText("");
            New_Password.setText("");
            New_Password.setError(getString(R.string.new_password_must_8_letters));
            New_Password.setFocusable(true);
            New_Password.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(New_Password, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        if (ReTypePassword.length() == 0) {

            RetypePassword.setError(getString(R.string.re_type_password_is_required));
            RetypePassword.setFocusable(true);
            RetypePassword.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(RetypePassword, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (!New_Password.getText().toString().equals(RetypePassword.getText().toString())){
            RetypePassword.setText("");
            New_Password.setText("");
            New_Password.setError(getString(R.string.new_password_and_re_type_password_doesn_t_match));
            New_Password.setFocusable(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(New_Password, InputMethodManager.SHOW_IMPLICIT);

            return false;
        }




        return true;
    }

}