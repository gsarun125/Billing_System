package com.ka.billingsystem.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ka.billingsystem.DataBase.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.java.Export;
import com.ka.billingsystem.java.Import;
import com.ka.billingsystem.model.OnPdfFileSelectListener;
import com.ka.billingsystem.model.UserseclectionAdapter;
import com.ka.billingsystem.model.selectionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserSelectionActivity extends AppCompatActivity implements selectionListener {
    private DataBaseHandler db = new DataBaseHandler(this);

    private ProgressDialog progressDialog;
    private int progressStatus = 0;
    private Handler handler;

    private RecyclerView recyclerView;
    int storage_RQ=101;
    private UserseclectionAdapter userseclectionAdapter;
    private List<String> mUsername = new ArrayList();
    private List<String> mUserid = new ArrayList();
    private List<String> mGen_Date = new ArrayList();
    private static final int PICK_FILE_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;

    SharedPreferences sharedpreferences;
    String USER_KEY = "user_key";
    String SHARED_PREFS = "shared_prefs";

    int checkedItem = 0;
    ImageButton menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
        recyclerView=findViewById(R.id.userSelection_RV);
        sharedpreferences= getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        menu=findViewById(R.id.btnmenu);
        displayPdf();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                // Request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        } else {
            checkForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, "storage", storage_RQ);
        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(UserSelectionActivity.this,view);
                popupMenu.getMenuInflater().inflate(R.menu.threedotadmin, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int ch = menuItem.getItemId();
                        if (ch == R.id.lan) {
                            ShowChangeLanguage();
                        } else if (ch == R.id.EditSignature) {
                            Intent intent = new Intent(UserSelectionActivity.this, EditSignature.class);
                            startActivity(intent);
                        } else if (ch == R.id.Logout) {
                            logOut();
                        } else if (ch == R.id.Export) {
                            String packagesname = getPackageName();
                            String status = Export.ExportData(packagesname);
                            share();
                            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                        } else if (ch == R.id.Import) {
                            selectFile();
                            }
                        return true;
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true);
                }
                popupMenu.show();

            }
        });
    }
    private void checkForPermission(String permission, String name, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
            } else if (shouldShowRequestPermissionRationale(permission)) {
                showDialog(permission, name, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        }
    }

    private void showDialog(String permission, String name, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Permission to access your " + name + " is required to use this app");
        builder.setTitle("Permission required");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(UserSelectionActivity.this, new String[]{permission}, requestCode);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");

        try {
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Handle the exception
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String filePath = getFilePathFromUri(uri);
            System.out.println(uri);

            String packagesname = getPackageName();


            String status1 = Import.ImportData(packagesname,filePath);
            Toast.makeText(getApplicationContext(), status1, Toast.LENGTH_SHORT).show();

            showProgressDialog();
           // Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
            //finish();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void showProgressDialog() {
        progressStatus = 0; // Reset the progressStatus to 0
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Importing your data...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();

        handler = new Handler();
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 1;

                // Update the progress bar
                handler.post(() -> progressDialog.setProgress(progressStatus));

                try {
                    // Sleep for 100 milliseconds to simulate progress in the background
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (progressStatus == 100) {
                    progressDialog.dismiss();
                    Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }


    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    File file = new File(getCacheDir(), "temp");
                    try (OutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, read);
                        }
                        outputStream.flush();
                    }
                    filePath = file.getAbsolutePath();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            filePath = uri.getPath();
        }
        System.out.println(filePath);
        return filePath;
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
        String language = sharedpreferences.getString("My_lang", "");
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else if (i == 1) {
                setLocale("ta");

                Intent intent = new Intent(this, UserSelectionActivity.class);
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
    private void share() {
        String zipFilePath = Environment.getExternalStorageDirectory().toString() + "/KIRTHANA AGENCIES/Backup/kirthana_agencies_backup.zip";
        File zipFile = new File(zipFilePath);

        if (zipFile.exists()) {
            Uri uri = FileProvider.getUriForFile(UserSelectionActivity.this, UserSelectionActivity.this.getPackageName() + ".provider", zipFile);
            //System.out.println(MainActivity.this.getPackageName());
            Intent share = new Intent();
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/zip");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share"));
        } else {
            Toast.makeText(UserSelectionActivity.this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
        }
    }


}
