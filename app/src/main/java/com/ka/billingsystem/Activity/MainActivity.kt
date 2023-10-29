package com.ka.billingsystem.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ka.billingsystem.LogoutService
import com.ka.billingsystem.R
import com.ka.billingsystem.databinding.ActivityMainBinding
import com.ka.billingsystem.java.Export
import com.ka.billingsystem.java.Import
import java.util.Locale

class MainActivity :  AppCompatActivity() {

    val storage_RQ=101

    lateinit var binding: ActivityMainBinding
    var checkedItem = 0
    var SHARED_PREFS = "shared_prefs"
    private lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)



        if(sharedpreferences.contains("checkeItem")) {
                var SPcheckedItem = sharedpreferences.getString("checkeItem", null)
                if (SPcheckedItem != null) {
                    checkedItem = Integer.parseInt(SPcheckedItem)
                    println(checkedItem)
                }
            lodeLocale()
        }




        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startService(Intent(this, LogoutService::class.java))


        binding.btnlogout.setOnClickListener {
            val popupMenu = PopupMenu(this@MainActivity, it)
            popupMenu.menuInflater.inflate(R.menu.threedotadmin, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val ch = menuItem.itemId
                if (ch==R.id.lan){
                    ShowChangeLanguage()
                }
                else if(ch==R.id.EditSignature){
                    val intent =Intent(this, EditSignature::class.java)
                    startActivity(intent)
                }
                else if (ch==R.id.Logout){
                    logOut()
                }
                else if (ch==R.id.Export){
                    val packagesname:String= packageName
                     val status :String = Export.ExportData(packagesname);
                    Toast.makeText(applicationContext,status,Toast.LENGTH_SHORT).show()
                }
                else if (ch==R.id.Import){
                    val packagesname:String= packageName
                    val status1 :String = Import.ImportData(packagesname);
                    Toast.makeText(applicationContext,status1,Toast.LENGTH_SHORT).show()
                }
                true
            }
            if (SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            };
            popupMenu.show()

        }

        binding.invoiceCard.setOnClickListener {
            val intent = Intent(this, SalesActivity::class.java)
            startActivity(intent)
        }

        binding.historyCard.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.recentinvoice.setOnClickListener {
            val intent =Intent(this, RecentInvoiceActivity::class.java)
            startActivity(intent)
        }
        binding.deleteCard.setOnClickListener {
            val intent =Intent(this, DeletedInvoice::class.java)
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
    override fun onBackPressed() {
        logOut()
    }

}