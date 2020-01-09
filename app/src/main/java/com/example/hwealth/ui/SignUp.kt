package com.example.hwealth.ui

import android.app.ProgressDialog
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private var getEmail: EditText? = null
    private var getName: EditText? = null
    private var getPassword: EditText? = null
    private var getHeight: EditText? = null
    private var getWeight: EditText? = null
    private var btnSignUp: Button? = null
    private var mProgressBar: ProgressDialog? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private val TAG = "SignUp"

    private var email: String? = null
    private var name: String? = null
    private var password: String? = null
    private var height: String? = null
    private var weight: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        //@Suppress("UNUSED_VARIABLE")
        //val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_sign_up)
        initialise()
    }

    private fun initialise(){
        getEmail = findViewById<View>(R.id.editText4) as EditText
        getName = findViewById<View>(R.id.editText5) as EditText
        getPassword = findViewById<View>(R.id.editText8) as EditText
        getHeight = findViewById<View>(R.id.editText12) as EditText
        getWeight= findViewById<View>(R.id.editText13) as EditText
        btnSignUp = findViewById<View>(R.id.button4) as Button
        mProgressBar = ProgressDialog(this)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        btnSignUp!!.setOnClickListener { signUpNewAccount() }
    }

    private fun signUpNewAccount(){
        email = getEmail?.text.toString()
        name = getName?.text.toString()
        password = getPassword?.text.toString()
        height = getHeight?.text.toString()
        weight = getWeight?.text.toString()

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)){

            mProgressBar!!.setMessage("Registering...")
            mProgressBar!!.show()
            mAuth!!
                .createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val userId = mAuth!!.currentUser!!.uid
                        verifyEmail()
                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("name").setValue(name)
                        currentUserDb.child("height").setValue(height)
                        currentUserDb.child("weight").setValue(weight)
                        currentUserDb.child("gender").setValue("")
                        currentUserDb.child("image").setValue("https://firebasestorage.googleapis.com/v0/b/hwealth-cafc9.appspot.com/o/images%2FOYQpnX.png?alt=media&token=300d9cad-e92c-4f42-9d86-8019fa8bddf9")
                        currentUserDb.child("birthday").setValue("")
                        currentUserDb.child("stepgoal").setValue("10000")
                        updateUserInfoAndUI()
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@SignUp, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
        }
        }

    private fun updateUserInfoAndUI() {
        val intent = Intent(this@SignUp, SignIn::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail() {
        val mUser = mAuth!!.currentUser
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@SignUp,
                        "Verification email sent to " + mUser.email,
                        Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(this@SignUp,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
