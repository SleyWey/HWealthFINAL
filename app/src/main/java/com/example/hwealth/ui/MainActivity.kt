package com.example.hwealth.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.hwealth.R

class MainActivity : AppCompatActivity() {
    //private lateinit var drawerLayout: DrawerLayout
    private var loginbtn : Button? = null
    private var signupbtn : Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginbtn = findViewById<View>(R.id.button) as Button
        signupbtn = findViewById<View>(R.id.button2) as Button

        loginbtn!!.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUp::class.java)
            startActivity(intent)
        }

        signupbtn!!.setOnClickListener {
            val intent = Intent(this@MainActivity, SignIn::class.java)
            startActivity(intent)
        }

    }

}
