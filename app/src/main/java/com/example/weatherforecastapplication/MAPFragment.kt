package com.example.weatherforecastapplication

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.weatherforecastapplication.home.HomeFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.io.IOException

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiKey = getString(R.string.google_maps_api_key)
        Places.initialize(requireContext(), apiKey)
        placesClient = Places.createClient(requireContext())
        btnSave = view.findViewById(R.id.btnSave)
        tvSearch = view.findViewById(R.id.tvSearch)

        btnSave.setOnClickListener {
            selectedLatLng?.let { navigateToHomeFragment(it) }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
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
        val defaultLocation = LatLng(26.820553, 30.802498)
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))
    }

    private fun navigateToHomeFragment(selectedLatLng: LatLng) {
        val args = Bundle().apply {
            putParcelable("selectedLatLng", selectedLatLng)
        }

        val homeFragment = HomeFragment().apply {
            arguments = args
        }
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@MAPFragment)?.commit()
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.nav_host_fragment_activity_main, homeFragment)
            addToBackStack(null)
            commit()
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
