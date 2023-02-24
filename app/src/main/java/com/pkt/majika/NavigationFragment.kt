package com.pkt.majika

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pkt.majika.databinding.FragmentNavigationBinding

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var defaultAvailable = true

/**
 * A simple [Fragment] subclass.
 * Use the [NavigationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NavigationFragment : Fragment(), SensorEventListener {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentNavigationBinding? = null
    private val binding get() = _binding!!
    private var currentFragment: Fragment? = null
    private lateinit var tempText: TextView
    private lateinit var sensorManager: SensorManager
    private var temperature: Sensor? = null
    private lateinit var activity: Activity
    private var currentHeader: String = "Restaurant Menu"

    private fun changeFragment(fragment: Fragment){
        println("Change Fragment")
        if(fragment != currentFragment) {
            this.childFragmentManager.beginTransaction()
                .replace(R.id.fragmentSelectedContainerView, fragment).commit()
            currentFragment = fragment
        }
    }

    private fun changeHeader(h: String){
        binding.navigationHeader.text = h
        currentHeader = h
        binding.temperatureText.text = tempText.text
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        println("Create")
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("Create View")
        // Inflate the layout for this fragment
        _binding = FragmentNavigationBinding.inflate(inflater, container, false)
        var currView = binding.root
        tempText = currView.findViewById(R.id.temperatureText)
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        if (temperature == null) {
            tempText.text = ""
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("View Created")
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.item_twibbon -> {
                    println("Change to Twibbon")
                    changeFragment(TwibbonFragment())
                    changeHeader("Twibbon")
                    true
                }
                R.id.item_branch -> {
                    println("Change to Branch")
                    changeFragment(BranchFragment())
                    changeHeader("Restaurant Branch")
                    true
                }
                R.id.item_menu -> {
                    println("Change to Menu")
                    changeFragment(MenuFragment())
                    changeHeader("Restaurant Menu")
                    true
                }
                R.id.item_cart -> {
                    println("Change to Cart")
                    changeFragment(CartFragment())
                    changeHeader("Cart")
                    true
                }
                else -> {
                    false
                }
            }
        }
        if (defaultAvailable){
            binding.bottomNavigationView.selectedItemId = R.id.item_menu
            currentHeader = "Restaurant Menu"
            defaultAvailable = false
        }
    }

    override fun onStart() {
        super.onStart()
        defaultAvailable = true

    }

    override fun onStop() {
        defaultAvailable = true
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("Destroy View")
        defaultAvailable = true
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        defaultAvailable = true
        println("Destroy")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            activity = context
        }
    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(sensor: SensorEvent) {
        val ambientTemperature: Float = sensor.values[0]
        if(currentHeader!="Restaurant Menu") tempText.text = ""
        else tempText.text = "${String.format("%.1f", ambientTemperature)}°C"

    }

    override fun onAccuracyChanged(sensor: Sensor, a: Int) {}

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Navigation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NavigationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}