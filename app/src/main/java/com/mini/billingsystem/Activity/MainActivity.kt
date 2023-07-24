package com.mini.billingsystem.Activity

import android.os.Bundle
import com.mini.billingsystem.Activity.DrawerBaseActivity
import com.mini.billingsystem.databinding.ActivityMainBinding

class MainActivity : DrawerBaseActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}