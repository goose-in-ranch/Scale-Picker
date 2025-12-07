package com.mitchell.scalepicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
class about : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        //WindowCompat.enableEdgeToEdge(window)
        //WindowCompat.setDecorFitsSystemWindows(window, false)

        //allows link textviews to be clickable
        findViewById<TextView>(R.id.github_link_textview).movementMethod = LinkMovementMethod.getInstance()
        findViewById<TextView>(R.id.gpl_link_textview).movementMethod = LinkMovementMethod.getInstance()
    }
}