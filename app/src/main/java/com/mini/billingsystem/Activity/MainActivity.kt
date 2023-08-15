package com.mini.billingsystem.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mini.billingsystem.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity :  AppCompatActivity() {

    val storage_RQ=101

    lateinit var binding: ActivityMainBinding

    var checkedItem = 0
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("arrayKey", checkedItem)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            checkedItem = savedInstanceState.getInt("arrayKey")
        }
        lodeLocale()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lan.setOnClickListener {

            ShowChangeLanguage()
        }


        binding.invoiceCard.setOnClickListener {
            val intent = Intent(this, SalesActivity::class.java)
            startActivity(intent)
        }
        binding.StockCard.setOnClickListener {
            val intent = Intent(this, StockActivity::class.java)
            startActivity(intent)
        }
        binding.addCard.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
        binding.UpdateCard.setOnClickListener {
            val intent = Intent(this, UpdateActivity::class.java)
            startActivity(intent)
        }
        binding.historyCard.setOnClickListener {
            val intent = Intent(this, HistorysActivity::class.java)
            startActivity(intent)
        }
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                //request for the permission
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        } else {
            checkForPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "storage",
                storage_RQ
            )
        }

    }

    private fun ShowChangeLanguage() {
        val lan = arrayOf("English","தமிழ்")
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Choose Language...")
        alertDialogBuilder.setSingleChoiceItems(lan, checkedItem, DialogInterface.OnClickListener { dialogInterface, i ->
            checkedItem = i
            if(i==0){
                setLocale("Eng")

                recreate()
            }
            else if(i==1){
                setLocale("ta")
              recreate()
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
       val editor: SharedPreferences.Editor=getSharedPreferences("settings", MODE_PRIVATE).edit()
        editor.putString("My_lang",lan);
        editor.apply()
    }
 fun lodeLocale() {
        val prefs:SharedPreferences=getSharedPreferences("settings",Activity.MODE_PRIVATE)
        val  language: String? =prefs.getString("My_lang","")
        if (language != null) {
            setLocale(language)
        }
    }


    private fun checkForPermission(permission: String,name: String,requstCode:Int){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(applicationContext,permission)==PackageManager.PERMISSION_GRANTED->{
                    Toast.makeText(applicationContext,"$name permission granted",Toast.LENGTH_SHORT).show()
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
}