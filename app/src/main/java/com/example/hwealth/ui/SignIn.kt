package com.example.hwealth.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.hwealth.R
import com.google.firebase.auth.FirebaseAuth

class SignIn : AppCompatActivity() {

    private var getEmail: EditText? = null
    private var getPassword: EditText? = null
    private var btnSignIn: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var forgotPass: TextView? = null

    private var email: String? = null
    private var password: String? = null
    private val TAG = "SignIn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        //@Suppress("UNUSED_VARIABLE")
        //val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_sign_in)
        initialise()
    }

    private fun initialise(){
        getEmail = findViewById<View>(R.id.editText) as EditText
        getPassword = findViewById<View>(R.id.editText2) as EditText
        btnSignIn = findViewById<View>(R.id.button5) as Button
        mAuth = FirebaseAuth.getInstance()
        forgotPass = findViewById<View>(R.id.textView12) as TextView
        btnSignIn!!.setOnClickListener { signInAccount() }
        forgotPass!!.setOnClickListener { forgotPassword() }
    }

    private fun forgotPassword(){
        val intent = Intent(this@SignIn, ForgotPassword::class.java)
        startActivity(intent)
    }

    private fun signInAccount(){
        email = getEmail?.text.toString()
        password = getPassword?.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            Log.d(TAG, "Welcome Back.")
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        updateUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@SignIn, "Invalid Email or Password.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this@SignIn, Testing::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}

