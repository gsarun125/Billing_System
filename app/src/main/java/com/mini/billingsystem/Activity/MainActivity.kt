package com.mini.billingsystem.Activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mini.billingsystem.databinding.ActivityMainBinding


class MainActivity : DrawerBaseActivity() {

    val storage_RQ=101

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
            //request for the permission
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }

        else {
            checkForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, "storage", storage_RQ)
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