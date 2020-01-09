package com.example.hwealth.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.hwealth.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private var getemail: EditText? = null
    private var resetbtn: Button? = null
    private var mAuth: FirebaseAuth? = null
    private val TAG = "ForgotPassword"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        initialise()
    }

    private fun initialise() {
        getemail = findViewById<View>(R.id.editText9) as EditText
        resetbtn = findViewById<View>(R.id.button8) as Button
        mAuth = FirebaseAuth.getInstance()
        resetbtn!!.setOnClickListener { sendPasswordResetEmail() }
    }

    private fun sendPasswordResetEmail() {
        val email = getemail?.text.toString()
        if (!TextUtils.isEmpty(email)) {
            mAuth!!
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val message = "Email sent."
                        Log.d(TAG, message)
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        updateUI()
                    } else {
                        Log.w(TAG, task.exception!!.message)
                        Toast.makeText(this, "No user found with this email.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } else {
            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this@ForgotPassword, SignIn::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}
