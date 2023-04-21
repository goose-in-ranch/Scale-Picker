package com.mitchell.scalepicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class about : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}