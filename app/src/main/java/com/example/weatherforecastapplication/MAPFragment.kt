package com.example.weatherforecastapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherforecastapplication.databinding.FragmentHomeBinding
import com.example.weatherforecastapplication.favorite.view.FavoriteFragmentDirections
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.math.log

class MAPFragment : Fragment(), OnMapReadyCallback {
    private lateinit var gMap: GoogleMap
    private lateinit var btnSave: Button
    private lateinit var tvSearch: EditText
    private lateinit var placesClient: PlacesClient
    private var selectedLatLng: LatLng? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_m_a_p, container, false)
    }
    override fun onStart() {
        super.onStart()
        val homeActivity = requireActivity() as MainActivity
        homeActivity.binding.navView.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        val homeActivity = requireActivity() as MainActivity
        homeActivity.binding.navView.visibility = View.VISIBLE
    }
    @SuppressLint("LogNotTimber")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiKey = getString(R.string.google_maps_api_key)
        Places.initialize(requireContext(), apiKey)
        placesClient = Places.createClient(requireContext())
        btnSave = view.findViewById(R.id.btnSave)
        tvSearch = view.findViewById(R.id.tvSearch)


        btnSave.setOnClickListener {
            selectedLatLng?.let {
                Log.i("TAG", "onViewCreated: map (latitude= ${it.latitude},longitude= ${it.longitude})")
                navigateToHomeFragment(it)
            }
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        tvSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateMapMarker(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        gMap.setOnMapClickListener { latLng ->
            gMap.clear()
            gMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            selectedLatLng = latLng
        }
        val defaultLocation = LatLng(30.0444196, 31.2357116)
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))
    }

    private fun navigateToHomeFragment(selectedLatLng: LatLng) {

        if(selectedLatLng != null){
            lifecycleScope.launch (Dispatchers.IO){
                if(arguments?.getInt("id") == 1){
                    withContext(Dispatchers.Main) {
                        val navController = findNavController()
                        val action = MAPFragmentDirections.actionNavigationMapToNavigationHome()
                        action.lat =selectedLatLng.latitude.toString()
                        action.log=selectedLatLng.longitude.toString()

                        navController.navigate(action)
                    }
                }
               else if(arguments?.getInt("id") == 2){

                    withContext(Dispatchers.Main) {
                        val navController = findNavController()
                        val action = MAPFragmentDirections.actionNavigationMapToNavigationFavorite()
                        action.lat =selectedLatLng.latitude.toString()
                        action.log=selectedLatLng.longitude.toString()
                        action.id = 2
                        navController.navigate(action)
                    }
                }
                else if(arguments?.getInt("id") == 3){

                    withContext(Dispatchers.Main) {
                        val navController = findNavController()
                        val action = MAPFragmentDirections.actionNavigationMapToNavigationAlerts()
                        action.lat =selectedLatLng.latitude.toString()
                        action.log=selectedLatLng.longitude.toString()
                        navController.navigate(action)
                    }
                }
                else{
                    Log.i("TAG", "Latlag = null")
                }
            }

        }
    }


    private fun updateMapMarker(addressStr: String) {
        val location = getLocationFromAddress(requireContext(), addressStr)
        location?.let {
            gMap.clear()
            gMap.addMarker(MarkerOptions().position(it).title("Selected Location"))
            gMap.moveCamera(CameraUpdateFactory.newLatLng(it))
            selectedLatLng = it
        }
    }

    private fun getLocationFromAddress(context: Context, addressStr: String): LatLng? {
        val geocoder = Geocoder(context)
        try {
            val addressList: List<Address>? = geocoder.getFromLocationName(addressStr, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                return LatLng(address.latitude, address.longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}
