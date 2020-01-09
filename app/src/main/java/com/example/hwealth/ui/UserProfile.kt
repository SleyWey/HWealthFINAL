package com.example.hwealth.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.hwealth.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserProfile : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var mStorage: FirebaseStorage? = null
    private var mStorageReference: StorageReference? = null
    private var imagepath: Uri? = null
    private var imagecam: Uri? = null

    private var name: TextView? = null
    private var stepgoal: TextView? = null
    private var b: TextView? = null
    private var gender: EditText? = null
    private var birthday: EditText? = null
    private var g: TextView? = null
    private var selectphotobtn: Button? = null
    private var profilepicture: ImageView? = null
    private var logoutbtn: Button? = null
    private var cal = Calendar.getInstance()
    private val GALLERY = 1
    private val CAMERA = 2

    private var genderg: String? = null
    private var birthdayb: String? = null
    private var propic: String? = null
    private var n: String? = null
    private var sg: String? = null
    val TAG = "UserProfile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        initialise()
    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mStorageReference = mStorage!!.reference
        name = findViewById<View>(R.id.editText3) as TextView
        stepgoal = findViewById<View>(R.id.editText11) as TextView
        birthday = findViewById<View>(R.id.editText10) as EditText
        selectphotobtn = findViewById<View>(R.id.button6) as Button
        profilepicture = findViewById<View>(R.id.imageView) as ImageView
        gender = findViewById<View>(R.id.editText6) as EditText
        g = findViewById<View>(R.id.editText6) as TextView
        b = findViewById<View>(R.id.editText10) as TextView
        logoutbtn = findViewById<View>(R.id.button3) as Button
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }
        birthday!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@UserProfile, android.R.style.Theme_Holo_Light_Dialog, dateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })
        selectphotobtn!!.setOnClickListener { showPictureDialog() }
        gender!!.setOnClickListener{ showGenderDialog() }
        logoutbtn!!.setOnClickListener { logout() }

        name!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm: InputMethodManager =
                    v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                n = name?.text.toString()
                sg = stepgoal?.text.toString()
                val userId = mAuth!!.currentUser!!.uid
                val currentUserDb = mDatabaseReference!!.child(userId)
                currentUserDb.child("weight").setValue(n)
                currentUserDb.child("height").setValue(sg)
                true
            } else {
                false
            }
        }

    }

    private fun showGenderDialog(){
        val listItems = arrayOf("Male", "Female", "Others")
        val mBuilder = AlertDialog.Builder(this@UserProfile)
        //mBuilder.setTitle("Select Gender")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
            g!!.text = listItems[i]
        }
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        mBuilder.setPositiveButton("OK") {dialog, which ->
            genderg = gender?.text.toString()

            val userId = mAuth!!.currentUser!!.uid
            val currentUserDb = mDatabaseReference!!.child(userId)
            currentUserDb.child("gender").setValue(genderg)
            Toast.makeText(this@UserProfile, "Gender update successfully.",
                Toast.LENGTH_SHORT).show()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //galleryIntent.setType("image/*")
            startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                imagepath = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagepath)

                    profilepicture!!.setImageBitmap(bitmap)
                    Toast.makeText(this@UserProfile, "Profile Picture update successfully!", Toast.LENGTH_SHORT).show()
                    selectphotobtn!!.alpha = 0f
                    uploadImage(bitmap)
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@UserProfile, "Failed!", Toast.LENGTH_SHORT).show()

                }
            }
        }
        if (requestCode == CAMERA){
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                profilepicture!!.setImageBitmap(thumbnail)
                Toast.makeText(this@UserProfile, "Profile Picture update successfully!", Toast.LENGTH_SHORT).show()
                selectphotobtn!!.alpha = 0f
                uploadCamImage(thumbnail)
        }
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                propic = snapshot.child("image").value as String
                name!!.text = snapshot.child("name").value as String
                g!!.text = snapshot.child("gender").value as String
                b!!.text = snapshot.child("birthday").value as String
                Picasso
                    .get()
                    .load(propic)
                    .fit()
                    .centerCrop()
                    .into(profilepicture)
                    selectphotobtn!!.alpha = 0f
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun uploadImage(bitmap: Bitmap) {
        if (imagepath != null) {
            val filename = UUID.randomUUID().toString()
            val baos = ByteArrayOutputStream()
            val camref = FirebaseStorage.getInstance()
                .reference
                .child("/images/"+filename)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val camim = baos.toByteArray()
            val up = camref.putBytes(camim)

            up.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                if (task.isSuccessful) {
                    camref.downloadUrl.addOnCompleteListener {
                        it.result?.let {
                            imagecam = it
                            val userId = mAuth!!.currentUser!!.uid
                            val currentUserDb = mDatabaseReference!!.child(userId)
                            currentUserDb.child("image").setValue(it.toString())
                        }
                    }
                }
            }
        }
    }

    private fun uploadCamImage(bitmap: Bitmap){
        val filename = UUID.randomUUID().toString()
        val baos = ByteArrayOutputStream()
        val camref = FirebaseStorage.getInstance()
            .reference
            .child("/images/"+filename)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val camim = baos.toByteArray()
        val up = camref.putBytes(camim)

        up.addOnCompleteListener{task: Task<UploadTask.TaskSnapshot> ->  
            if(task.isSuccessful){
                camref.downloadUrl.addOnCompleteListener{
                    it.result?.let {
                        imagecam = it
                        val userId = mAuth!!.currentUser!!.uid
                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("image").setValue(it.toString())
                    }
                }
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd MMM yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        this.b!!.text = sdf.format(cal.time)
        birthdayb = birthday?.text.toString()
        val userId = mAuth!!.currentUser!!.uid
        val currentUserDb = mDatabaseReference!!.child(userId)
        currentUserDb.child("birthday").setValue(birthdayb)

    }

    private fun logout() {
        mAuth!!.signOut()
        val intent = Intent(this@UserProfile, MainActivity::class.java)
        startActivity(intent)
    }
}
