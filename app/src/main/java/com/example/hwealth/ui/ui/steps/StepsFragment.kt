package com.example.hwealth.ui.ui.steps

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.hwealth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class StepsFragment : Fragment(), SensorEventListener {

    private lateinit var stepsViewModel: StepsViewModel
    var running = false
    var sensorManager: SensorManager? = null
    internal var pStatus = 0
    private val handler = Handler()
    internal lateinit var tv: TextView
    private var mProgress: ProgressBar? = null
    private var dateTv: TextView? = null
    private var distancetv: TextView? = null
    private var caloriestv: TextView? = null
    private var timetv: TextView? = null
    private var sg: TextView? = null
    private var getsg: TextView? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var mStorage: FirebaseStorage? = null
    private var mStorageReference: StorageReference? = null

    private var h:TextView? = null
    private var geth: EditText? = null
    private val convertkm = 1.60934
    private val convertcal = 55
    private val convertinch = 2.54
    private var height: Double? = null
    private var stepgoal: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        stepsViewModel =
            ViewModelProviders.of(this).get(StepsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_steps, container, false)
        tv = root.findViewById<View>(R.id.textView16) as TextView
        mProgress = root.findViewById<View>(R.id.progressBar) as ProgressBar
        sensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        dateTv = root.findViewById<View>(R.id.textView14) as TextView
        distancetv = root.findViewById<View>(R.id.textView20) as TextView
        caloriestv = root.findViewById<View>(R.id.textView24) as TextView
        timetv = root.findViewById<View>(R.id.textView34) as TextView
        h = root.findViewById<View>(R.id.textView35) as TextView
        geth = root.findViewById<View>(R.id.textView35) as EditText
        sg = root.findViewById<View>(R.id.spinner) as TextView
        getsg = root.findViewById<View>(R.id.spinner) as TextView
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mStorageReference = mStorage!!.reference
        val date_n: String = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        dateTv!!.setText(date_n)
        val stepgoal = resources.getStringArray(R.array.numbers)
                return root
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                h!!.text = snapshot.child("height").value as String
                height = geth!!.text.toString().toDouble()
                sg!!.text = snapshot.child("stepgoal").value as String
                stepgoal =  getsg!!.text.toString().toInt()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onResume() {
        super.onResume()
        running = true
        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepsSensor == null) {
            //Toast.makeText(this, "No Step Counter Sensor !", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (running){
            val res = resources
            val drawable = res.getDrawable(R.drawable.circular)

            mProgress!!.progress = 0   // Main Progress
            mProgress!!.progressDrawable = drawable
            Thread(Runnable {
                if (pStatus <= stepgoal!!) {
                    handler.post {
                        pStatus = event.values[0].toInt()
                        mProgress!!.progress = pStatus
                        tv!!.setText("" + pStatus)
                        val distance = (height!!/convertinch)*0.414*pStatus.toString().toDouble()/12/5280*convertkm
                        val caloriesburn = distance*convertcal
                        distancetv!!.setText(String.format("%.2f", distance) + "KM")
                        caloriestv!!.setText(String.format("%.0f", caloriesburn) + "KCAL")

                    }
                }
            }).start()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}



}