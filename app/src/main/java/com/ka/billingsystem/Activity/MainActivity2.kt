package com.ka.billingsystem.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.text.format.DateUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ka.billingsystem.DataBase.DataBaseHandler
import com.ka.billingsystem.R
import com.ka.billingsystem.Services.LogoutService
import com.ka.billingsystem.databinding.ActivityMain2Binding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity2 : AppCompatActivity() {
    private val db = DataBaseHandler(this)
    val storage_RQ=101
    //arungssssa
    lateinit var binding: ActivityMain2Binding
    var checkedItem = 0

    var SHARED_PREFS = "shared_prefs"
    private lateinit var sharedpreferences: SharedPreferences
    var SPuser: String? = null
    var USER_KEY = "user_key"
    var LastLogout: Long? = null
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        SPuser = sharedpreferences.getString(USER_KEY, null)
            //  if(sharedpreferences.contains("checkeItem")) {
            //var SPcheckedItem = sharedpreferences.getString("checkeItem", null)
            //if (SPcheckedItem != null) {
             //   checkedItem = Integer.parseInt(SPcheckedItem)
            //}
            //lodeLocale()
        //}
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler()

        Lastdateset()
        handler!!.postDelayed(runnableCode, 1000 * 60);

        startService(Intent(this, LogoutService::class.java))

        binding.btnlogout.setOnClickListener {

            val popupMenu = PopupMenu(this@MainActivity2, it)
            popupMenu.menuInflater.inflate(R.menu.threedot, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val ch = menuItem.itemId
               if(ch==R.id.EditSignature2){
                   val intent =Intent(this, EditSignature::class.java)
                   startActivity(intent)
               }
               else if (ch== R.id.Logout){
                    logOut()
                }
                true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            };
            popupMenu.show()

        }

        binding.invoiceCard.setOnClickListener {
            if(checkStoragePermission()){
            val intent = Intent(this, SalesActivity::class.java)
            startActivity(intent)}
            else{        Toast.makeText(this@MainActivity2, "Storage permission denied", Toast.LENGTH_LONG).show()
                Permission()
            }
        }
       
        binding.historyCard.setOnClickListener {
            if (checkStoragePermission()){
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)}
            else{
                Toast.makeText(this@MainActivity2, "Storage permission denied", Toast.LENGTH_LONG).show()
                Permission()
            }
        }


        binding.recentinvoice.setOnClickListener {
            if (checkStoragePermission()){
            val intent =Intent(this, RecentInvoiceActivity::class.java)
            startActivity(intent)}
            else{        Toast.makeText(this@MainActivity2, "Storage permission denied", Toast.LENGTH_LONG).show()
                Permission()
            }
        }
        binding.deleteCard.setOnClickListener {
            if (checkStoragePermission()){
            val intent =Intent(this, DeletedInvoice::class.java)
            startActivity(intent)}
            else{
                Toast.makeText(this@MainActivity2, "Storage permission denied", Toast.LENGTH_LONG).show()
                Permission()
            }
        }
        Permission()

    }
    private fun Permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
    private  fun logOut(){
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setMessage(R.string.press_ok_to_logout)
        builder.setTitle(R.string.alert)
        builder.setCancelable(true)
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this,R.string.you_press_cancel_button,Toast.LENGTH_SHORT).show()

        }
        builder.setPositiveButton("Ok",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                val time = System.currentTimeMillis()
                db.LastLogout(SPuser,time)
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
        alertDialogBuilder.setTitle(R.string.choose_language)
        alertDialogBuilder.setSingleChoiceItems(lan, checkedItem, DialogInterface.OnClickListener { dialogInterface, i ->
            checkedItem = i

            val editor = sharedpreferences.edit()

            editor.putString("checkeItem", checkedItem.toString())

            editor.apply()

            if(i==0){
                setLocale("Eng")

                val intent = Intent(this, MainActivity2::class.java)
                finish()
                startActivity(intent)
            }
            else if(i==1){
                setLocale("ta")
                val intent = Intent(this, MainActivity2::class.java)
                finish()
                startActivity(intent)
            }
            dialogInterface.dismiss()
        })
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which -> }
        val customAlertDialog: AlertDialog = alertDialogBuilder.create()
        customAlertDialog.show()
    }

    private fun setLocale(lan: String) {
        val  local= Locale(lan)
        Locale.setDefault(local)
        val config= Configuration()
        config.locale=local
        baseContext.resources.updateConfiguration(config,baseContext.resources.displayMetrics)
        val editor = sharedpreferences.edit()
        editor.putString("My_lang",lan);
        editor.apply()
    }
    fun lodeLocale() {

        val  language: String? =sharedpreferences.getString("My_lang","")
        if (language != null) {
            setLocale(language)
        }
    }


    private fun checkForPermission(permission: String,name: String,requstCode:Int){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(applicationContext,permission)== PackageManager.PERMISSION_GRANTED->{
                  }
                shouldShowRequestPermissionRationale(permission)->showDialog(permission,name,requstCode)
                else-> ActivityCompat.requestPermissions(this, arrayOf(permission),requstCode)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name Permission refused", Toast.LENGTH_SHORT)
                    .show()

            }
        }
        when(requestCode){
            storage_RQ->innerCheck("storage")

        }
    }
    private fun showDialog(permission: String,name: String,requstCode: Int)
    {
        val builder= AlertDialog.Builder(this)
        builder.apply {
            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("Ok"){dialog,which->
                ActivityCompat.requestPermissions(this@MainActivity2, arrayOf(permission),requstCode)
            }
        }
        val dialog=builder.create()
        dialog.show()
    }
    override fun onBackPressed() {
        logOut()
    }
    private fun Lastdateset(){
        val c1: Cursor
        c1 = db.get_value("SELECT * FROM user WHERE user_id ='"+SPuser+"'")!!
        if (c1.moveToFirst()) {
            @SuppressLint("Range") val data1: Long = c1.getLong(c1.getColumnIndex("Last_Logout"))
           println(data1);
            LastLogout=data1
        }
        if (LastLogout!=0L) {
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
        }else {
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
}