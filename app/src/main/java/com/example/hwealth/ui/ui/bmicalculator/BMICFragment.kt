package com.example.hwealth.ui.ui.bmicalculator

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.hwealth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_bmic.*


class BMICFragment : Fragment() {

    private lateinit var bmciViewModel: BmciViewModel
    private var heightInput: EditText? = null
    private var weightInput: EditText? = null
    private var setTextResult: TextView? = null
    private var setBmiResult: TextView? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var mStorage: FirebaseStorage? = null
    private var mStorageReference: StorageReference? = null

    private var h: TextView? = null
    private var hh: String? = null
    private var w: TextView? = null
    private var ww: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bmciViewModel =
            ViewModelProviders.of(this).get(BmciViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_bmic, container, false)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mStorageReference = mStorage!!.reference
        heightInput= root.findViewById<View>(R.id.editTextHeight) as EditText
        weightInput = root.findViewById<View>(R.id.editTextWeight) as EditText
        setTextResult = root.findViewById<View>(R.id.categorytextResult) as TextView
        setBmiResult = root.findViewById<View>(R.id.bmiResult) as TextView
        h = root.findViewById<View>(R.id.editTextHeight) as TextView
        w = root.findViewById<View>(R.id.editTextWeight) as TextView

         weightInput!!.setOnEditorActionListener{ v, actionId, event ->
             if(actionId == EditorInfo.IME_ACTION_DONE){
                 calculateBMI()
                 val imm: InputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                 imm.hideSoftInputFromWindow(v.windowToken, 0)
                 ww = weightInput?.text.toString()
                 hh = heightInput?.text.toString()
                 val userId = mAuth!!.currentUser!!.uid
                 val currentUserDb = mDatabaseReference!!.child(userId)
                 currentUserDb.child("weight").setValue(ww)
                 currentUserDb.child("height").setValue(hh)
                 true
             }else{
                 false
             }
         }
        return root
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                w!!.text = snapshot.child("weight").value as String
                h!!.text = snapshot.child("height").value as String
                calculateBMI()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

   private fun calculateBMI(){
        //convert cm to meters
        val heightDouble: Double = heightInput!!.text.toString().toDouble() / 100
        val weightDouble: Double = weightInput!!.text.toString().toDouble()
        val result: Double =  weightDouble / Math.pow(heightDouble, 2.0)


        if (result < 18.5){
            setTextResult!!.setTextColor(Color.parseColor("#0062FF"))
            setTextResult!!.setText("Underweight")
            indicator.animate().rotation(15f).start()
            indicator.pivotX = 150f
        } else if (result >= 18.5 && result <= 24.9) {
            setTextResult!!.setTextColor(Color.parseColor("#009900"))
            setTextResult!!.setText("Normal")
            indicator.animate().rotation(65f).start()
            indicator.pivotX = 150f
        } else if (result >= 25 && result <= 29.9){
            setTextResult!!.setTextColor(Color.parseColor("#F77F23"))
            setTextResult!!.setText("Overweight")
            indicator.animate().rotation(115f).start()
            indicator.pivotX = 150f
        } else if (result >= 30){
            setTextResult!!.setTextColor(Color.parseColor("#F31212"))
            setTextResult!!.setText("Obese")
            indicator.animate().rotation(165f).start()
            indicator.pivotX = 150f
        }
        setBmiResult!!.setText(String.format("%.2f", result))

    }


}