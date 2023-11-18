package com.ka.billingsystem.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.text.InputFilter
import android.text.format.DateUtils
import android.widget.CheckBox
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.ka.billingsystem.DataBase.DataBaseHandler
import com.ka.billingsystem.R
import com.ka.billingsystem.Services.LogoutService
import com.ka.billingsystem.databinding.ActivityMainBinding
import com.ka.billingsystem.java.Export.ExportData
import com.ka.billingsystem.java.Import
import com.ka.billingsystem.java.RangeFilter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity :  AppCompatActivity() {
    private val db = DataBaseHandler(this)
    val storage_RQ=101
    private var progressStatus = 0
    private var handler: Handler? = null
    private val PICK_FILE_REQUEST_CODE = 100
    lateinit var binding: ActivityMainBinding
    var checkedItem = 0
    private var progressDialog: ProgressDialog? = null
    var SHARED_PREFS = "shared_prefs"
    private lateinit var sharedpreferences: SharedPreferences
    var ADMIN_LOGIN = "admin_login"
    var SPuser: String? = null
    var USER_KEY = "user_key"
    var LastLogout: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      //  sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        SPuser = sharedpreferences.getString(USER_KEY, null)
     //   if(sharedpreferences.contains("checkeItem")) {
       ///         var SPcheckedItem = sharedpreferences.getString("checkeItem", null)
          //      if (SPcheckedItem != null) {
            //        checkedItem = Integer.parseInt(SPcheckedItem)
              //      println(checkedItem)
                //}
            //lodeLocale()
       // }




        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler()

        Lastdateset()
        handler!!.postDelayed(runnableCode, 1000 * 60);

        startService(Intent(this, LogoutService::class.java))


        binding.btnlogout.setOnClickListener {
            val popupMenu = PopupMenu(this@MainActivity, it)
            popupMenu.menuInflater.inflate(R.menu.threedotadmin, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val ch = menuItem.itemId
               if(ch==R.id.EditSignature){
                    val intent =Intent(this, EditSignature::class.java)
                    startActivity(intent)
                } else if (ch == R.id.GST) {
                   AddGst()
               } else if (ch==R.id.Logout){
                    logOut()
                }
                else if (ch==R.id.Export){
                   val qurry = "Select * from Transation"
                   val c1: Cursor = db.get_value(qurry)!!
                   if (c1.count > 1) {
                       if (checkStoragePermission()) {
                           println("arybb")
                           val packagesname = packageName
                           val sdf = SimpleDateFormat("dd-MM-yyyy-hh_mm_ssa", Locale.getDefault())
                           val currentDateAndTime = sdf.format(Date())
                           val Backup_filename = "Kirthana_backup_$currentDateAndTime.zip"

                           val result =
                               ExportData(packagesname, Backup_filename, create(Backup_filename))
                           val message = result.message
                           val file_name = result.fileName
                           Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                           if (file_name != null) {
                               share(file_name)
                           }
                       } else {
                           Toast.makeText(
                               this@MainActivity,
                               "Storage permission denied",
                               Toast.LENGTH_LONG
                           ).show()
                           Permission()
                       }
                   } else {
                       AlertDialog()
                   }
                }
                else if (ch==R.id.Import){
                   if (checkStoragePermission()) {
                       selectFile()
                   } else {
                       Toast.makeText(this@MainActivity, "Storage permission denied", Toast.LENGTH_LONG).show()
                       Permission()
                   }
                }
                true
            }
            if (SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            };
            popupMenu.show()

        }

        binding.historyCard.setOnClickListener {
            if(checkStoragePermission()){
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
            }else{
                Toast.makeText(this@MainActivity, "Storage permission denied", Toast.LENGTH_LONG).show()

                Permission()
            }
        }

        binding.recentinvoice.setOnClickListener {
            if (checkStoragePermission()){
            val intent =Intent(this, RecentInvoiceActivity::class.java)
            startActivity(intent)
            }else{
                Toast.makeText(this@MainActivity, "Storage permission denied", Toast.LENGTH_LONG).show()
                Permission()
            }
        }
        binding.deleteCard.setOnClickListener {
            if(checkStoragePermission()){
            val intent =Intent(this, DeletedInvoice::class.java)
            startActivity(intent)
            }else{
                Toast.makeText(this@MainActivity, "Storage permission denied", Toast.LENGTH_LONG).show()
                Permission()
            }

        }

        Permission()

    }
    private fun AlertDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setMessage(R.string.no_invoice_found_to_export)
        builder.setTitle(R.string.alert)
        builder.setCancelable(true)
        builder.setPositiveButton("ok",
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() })
        val alertDialog = builder.create()
        alertDialog.show()
    }
    private fun create(Backup_filename: String): File? {
        val subdir = File(this.filesDir, "Backup")
        if (!subdir.exists()) {
            subdir.mkdir()
        }
        return File(subdir, Backup_filename)
    }
    private fun checkStoragePermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.M) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                // Check for MANAGE_EXTERNAL_STORAGE permission on Android 11 and above
                if (Environment.isExternalStorageManager()) {
                    true // Permission is granted
                } else {
                    // You may need to request MANAGE_EXTERNAL_STORAGE permission here
                    // or redirect the user to the system settings to grant the permission
                    false
                }
            } else {
                // For versions below Android 11, check for WRITE_EXTERNAL_STORAGE permission
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    true // Permission is granted
                } else {
                    // You may need to request WRITE_EXTERNAL_STORAGE permission here
                    // or redirect the user to the system settings to grant the permission
                    false
                }
            }
        } else true
        // Permission is implicitly granted on versions below M
    }
    private fun Permission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        } else {
            checkForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "storage", storage_RQ)
        }
    }
    private fun selectFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/zip"
        try {
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
        } catch (ex: ActivityNotFoundException) {
            // Handle the exception
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val uri: Uri? = data.data
            val filePath: String? = getFilePathFromUri(uri)
            println(uri)

            val packagesName: String = packageName

            val status1: String = Import.ImportData(packagesName, filePath)
            Toast.makeText(applicationContext, status1, Toast.LENGTH_SHORT).show()
            val dbHelper: SQLiteOpenHelper = DataBaseHandler(this)
            val db = dbHelper.writableDatabase
            db.close()
            showProgressDialog()
            // Uncomment the following lines if you want to restart the application
            // val intent: Intent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)!!
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // startActivity(intent)
            // finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun showProgressDialog() {
        progressStatus = 0 // Reset the progressStatus to 0
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Importing your data...")
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog!!.setProgress(0)
        progressDialog!!.setMax(100)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
        handler = Handler()
        Thread {
            while (progressStatus < 100) {
                progressStatus += 1

                // Update the progress bar
                handler!!.post(Runnable { progressDialog!!.setProgress(progressStatus) })
                try {
                    // Sleep for 100 milliseconds to simulate progress in the background
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                if (progressStatus == 100) {
                    progressDialog!!.dismiss()
                    val intent = baseContext.packageManager
                        .getLaunchIntentForPackage(baseContext.packageName)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }

    private fun getFilePathFromUri(uri: Uri?): String? {
        var filePath: String? = null
        val scheme = uri?.scheme
        if (ContentResolver.SCHEME_CONTENT == scheme) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val file = File(cacheDir, "temp")
                    FileOutputStream(file).use { outputStream ->
                        val buffer =
                            ByteArray(4 * 1024) // or other buffer size
                        var read: Int
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            outputStream.write(buffer, 0, read)
                        }
                        outputStream.flush()
                    }
                    filePath = file.absolutePath
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            filePath = uri.path
        }
        println(filePath)
        return filePath
    }


    private fun logOut(){
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.press_ok_to_logout))
        builder.setTitle(R.string.alert)
        builder.setCancelable(true)
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, getString(R.string.you_press_cancel_button),Toast.LENGTH_SHORT).show()

        }
        builder.setPositiveButton("Ok",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->

                val editor = sharedpreferences.edit()

                editor.remove("user_key")
                editor.remove("password_key")

                editor.apply()

                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
            })
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun ShowChangeLanguage() {
        val lan = arrayOf("English","தமிழ்")
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.choose_language))
        println(checkedItem)
        alertDialogBuilder.setSingleChoiceItems(lan, checkedItem, DialogInterface.OnClickListener { dialogInterface, i ->

            checkedItem = i
            val editor = sharedpreferences.edit()

            editor.putString("checkeItem", checkedItem.toString())

            editor.apply()
            if(i==0){
                setLocale("Eng")

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                startActivity(intent)
               // recreate()
            }
            else if(i==1){
                setLocale("ta")
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                startActivity(intent)
            //recreate()
            }
            dialogInterface.dismiss()
        })
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which -> }
        val customAlertDialog: AlertDialog = alertDialogBuilder.create()
        customAlertDialog.show()
    }

    private fun setLocale(lan: String) {
        val  local=Locale(lan)
        Locale.setDefault(local)
        val config=Configuration()
        config.locale=local
        baseContext.resources.updateConfiguration(config,baseContext.resources.displayMetrics)
        val editor = sharedpreferences.edit()
        editor.putString("My_lang",lan);
        editor.apply()
    }
 @SuppressLint("SuspiciousIndentation")
 fun lodeLocale() {

     val  language: String? =sharedpreferences.getString("My_lang","")
        if (language != null) {
            setLocale(language)
        }
    }


    private fun checkForPermission(permission: String,name: String,requstCode:Int){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(applicationContext,permission)==PackageManager.PERMISSION_GRANTED->{

                }
                shouldShowRequestPermissionRationale(permission)->showDialog(permission,name,requstCode)
                else->ActivityCompat.requestPermissions(this, arrayOf(permission),requstCode)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name Permission refused", Toast.LENGTH_SHORT)
                    .show()

            } else {
                Toast.makeText(applicationContext, "$name Permission granted", Toast.LENGTH_SHORT)
                    .show()

            }
        }
        when(requestCode){
            storage_RQ->innerCheck("storage")

        }
    }
    private fun showDialog(permission: String,name: String,requstCode: Int)
    {
        val builder=AlertDialog.Builder(this)
        builder.apply {
            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("Ok"){dialog,which->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission),requstCode)
            }
        }
        val dialog=builder.create()
        dialog.show()
    }


    private fun share(filename: String) {
        val subdir = File(this.filesDir, "Backup")
        val zipFile = File(subdir, filename)
        if (zipFile.exists()) {
            val uri = FileProvider.getUriForFile(
                this@MainActivity,this@MainActivity.getPackageName() + ".provider",
                zipFile
            )

            //System.out.println(PdfviewActivity.this.getPackageName());
            val share = Intent()
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            share.action = Intent.ACTION_SEND
            share.action = Intent.ACTION_SEND
            // share.setType("application/zip");
            share.setDataAndType(uri, "application/zip")
            share.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(share, "Share"))
        } else {
            Toast.makeText(this@MainActivity, R.string.file_not_found, Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun Lastdateset(){
        val c1: Cursor
        c1 = db.get_value("SELECT * FROM user WHERE user_id ='"+SPuser+"'")!!
        if (c1.moveToFirst()) {
            @SuppressLint("Range") val data1: Long = c1.getLong(c1.getColumnIndex("Last_Logout"))
            LastLogout=data1
        }
       if(LastLogout!=0L){
        val lastLogoutTimestamp = LastLogout!!

        val lastLogoutDate = Date(lastLogoutTimestamp)

        val currentTimeMillis = System.currentTimeMillis()

        val timeDifference = currentTimeMillis - lastLogoutTimestamp

        val relativeTimeSpan = DateUtils.getRelativeTimeSpanString(
            lastLogoutTimestamp,
            currentTimeMillis,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        )

        if (timeDifference > DateUtils.DAY_IN_MILLIS) {
            // Customize the date format based on your requirements
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(lastLogoutDate)

            // Set the formatted date and time to the TextView
            binding.LastLogout.text = "Last Logout: $formattedDate"
        } else {
            // Set the formatted relative time span to the TextView
            binding.LastLogout.text = "Last Logout: $relativeTimeSpan"
        }
       }else{
           binding.LastLogout.text = ""
       }
        binding.welometxt.text = "Hii!.. "+SPuser

    }
    private val runnableCode: Runnable = object : Runnable {
        override fun run() {
            // Run the method periodically
            Lastdateset()
            // Schedule the method to run again after a delay
            handler!!.postDelayed(
                this,
                (1000 * 60).toLong()
            ) // Run every 60 seconds (adjust as needed)
        }
    }

    override fun onDestroy() {
        // Remove the callback when the activity is destroyed to prevent memory leaks
        handler!!.removeCallbacks(runnableCode)
        super.onDestroy()
    }
    private fun AddGst() {
        try {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog_addgst, null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            val textView = dialogView.findViewById<TextView>(R.id.currentGst)
            val qurry = "Select * from user where id='1'"
            val c1 = db.get_value(qurry)
            if (c1!!.moveToFirst()) {
                @SuppressLint("Range") val data1 = c1.getString(c1.getColumnIndex("gst"))
                textView.text = "Current GST : $data1"
            }
            val gstvalue = dialogView.findViewById<EditText>(R.id.editGst)
            val agreeCheckbox = dialogView.findViewById<CheckBox>(R.id.agree_checkbox)
            gstvalue.filters = arrayOf<InputFilter>(RangeFilter(0, 100))
            builder.setPositiveButton(
                "OK"
            ) { dialog: DialogInterface, which: Int ->
                if (agreeCheckbox.isChecked && gstvalue.text.length != 0) {
                    db.ADD_GST(gstvalue.text.toString())
                    dialog.dismiss()
                } else {
                    //Toast.makeText(this, R.string.please_agree_to_continue, Toast.LENGTH_SHORT).show();
                }
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            val alertDialog = builder.create()
            alertDialog.show()
        } catch (e: Exception) {
        }
    }
}
