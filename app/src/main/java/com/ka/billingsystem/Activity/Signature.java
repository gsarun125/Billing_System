package com.ka.billingsystem.Activity;

import static com.ka.billingsystem.java.ImageEncodeAndDecode.encodeToBase64;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.databinding.ActivitySignatureBinding;

import java.io.ByteArrayOutputStream;

public class Signature extends AppCompatActivity {
    ActivitySignatureBinding binding;
    private DataBaseHandler db = new DataBaseHandler(this);
    private SharedPreferences sharedPreferences;
    String SHARED_PREFS = "shared_prefs";
    String SPuser;
    String SPpass;
    String SPIS_FIRST_TIME;
    String USER_KEY = "user_key";
    String PASSWORD_KEY = "password_key";

    private static final String SHARED_PREFS_KEY = "signature";
    private static final String SHARED_PREFS_Logo = "logo";
    Bitmap sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignatureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences= getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SPuser = sharedPreferences.getString(USER_KEY, null);
        SPpass = sharedPreferences.getString(PASSWORD_KEY, null);
        SPIS_FIRST_TIME = sharedPreferences.getString(SHARED_PREFS_KEY, null);


        binding.clear.setOnClickListener(v ->{
            binding.signatureView.clearCanvas();
        } );

        binding.gen.setOnClickListener(v -> {
             sign=binding.signatureView.getSignatureBitmap();

            if (sign!=null){

                showCustomDialog();

            }
        });
    }
    private void saveToSharedPreferences(Bitmap bitmap) {

        String encodedString = encodeToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
        db.ADD_Sgin(encodedString);
        Intent intent=new Intent(this,PdfviewActivity.class);
        startActivity(intent);
        super.finish();

    }


    public void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_signature, null);
        builder.setView(dialogView);

        ImageView imageView = dialogView.findViewById(R.id.dialog_image_view);
        CheckBox agreeCheckbox = dialogView.findViewById(R.id.agree_checkbox);

        imageView.setImageBitmap(sign);
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (agreeCheckbox.isChecked()) {
                saveToSharedPreferences(sign);
            } else {
                agreeCheckbox.setError(getString(R.string.please_agree_to_continue));
            }


        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}