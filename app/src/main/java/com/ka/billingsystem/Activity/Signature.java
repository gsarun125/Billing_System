package com.ka.billingsystem.Activity;

import static com.ka.billingsystem.java.ImageEncodeAndDecode.encodeToBase64;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.databinding.ActivitySignatureBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class Signature extends AppCompatActivity {
    private static final String TAG = "Signature";
    ActivitySignatureBinding binding;
    private DataBaseHandler db = new DataBaseHandler(this);
    private SharedPreferences sharedPreferences;
    String SHARED_PREFS = "shared_prefs";
    String SPuser;
    String SPpass;
    String SPIS_FIRST_TIME;
    String USER_KEY = "user_key";
    String PASSWORD_KEY = "password_key";
    private static final int PICK_FILE_REQUEST_CODE = 100;
    private static final String SHARED_PREFS_KEY = "signature";
    private static final String SHARED_PREFS_Logo = "logo";
    Bitmap sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignatureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SPuser = sharedPreferences.getString(USER_KEY, null);
        SPpass = sharedPreferences.getString(PASSWORD_KEY, null);
        SPIS_FIRST_TIME = sharedPreferences.getString(SHARED_PREFS_KEY, null);

        binding.clear.setOnClickListener(v -> {
            binding.signatureView.clearCanvas();
        });

        binding.gen.setOnClickListener(v -> {
            try {
                sign = binding.signatureView.getSignatureBitmap();

                if (sign != null) {
                    showCustomDialog(sign);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error generating signature", e);
            }
        });

        binding.sigUpload.setOnClickListener(v -> Uploadsignature());
        binding.imageButtonUpload.setOnClickListener(v -> Uploadsignature());

    }

    private void saveToSharedPreferences(Bitmap bitmap) {
        try {
            String encodedString = encodeToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
            db.ADD_Sgin(encodedString);
            Intent intent = new Intent(this, PdfviewActivity.class);
            startActivity(intent);
            super.finish();
        } catch (Exception e) {
            Log.e(TAG, "Error saving signature to SharedPreferences", e);
        }
    }

    public void showCustomDialog(Bitmap sign) {
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Error showing custom dialog", e);
        }
    }
    private void Uploadsignature() {
        Log.i("selectFile", "selectFile()-Start");
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            Log.i("selectFile", "selectFile()-Line2");

            try {
                Log.i("selectFile", "selectFile()-Line3");
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e("selectFile() inner try", "selectFile()-exception occurred", ex);
            }
        } catch (Exception e) {
            Log.e("selectFile() outer try", "selectFile()-An exception occurred", e);
        }
    }

    private void startCropActivity(Uri sourceUri) {
        CropImage.activity(sourceUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(350, 110) // Set the initial aspect ratio
                .setMinCropResultSize(450, 200) // Set the fixed size
                //.setMaxCropResultSize(450, 200) // Set the fixed size
                .setFixAspectRatio(true)
                .start(this);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                // Start the image cropping activity
                startCropActivity(Objects.requireNonNull(uri));
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                // Handle crop result
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    Bitmap croppedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                    showCustomDialog(croppedBitmap);
                    Toast.makeText(getApplicationContext(), "Image cropped successfully", Toast.LENGTH_SHORT).show();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    // Handle cropping error
                    Toast.makeText(getApplicationContext(), "Error cropping image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);

        } catch (Exception e) {
            Log.e("onActivityResult()", "onActivityResult()-An exception occurred", e);
        }
    }
}
