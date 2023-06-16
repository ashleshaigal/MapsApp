package com.example.mapsapp.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mapsapp.R
import com.example.mapsapp.databinding.FragmentMapsScreenBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsScreen : Fragment() {

    private lateinit var viewModel: MapsScreenViewModel
    private lateinit var binding: FragmentMapsScreenBinding

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsScreenBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[MapsScreenViewModel::class.java]
        binding.viewModel = viewModel
        binding.appBar.title = arguments?.getString("email") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        fetchLocation()

        binding.appBar.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.logout, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.title.toString() == getString(R.string.logout)) {
                    removePref()
                    findNavController().navigate(MapsScreenDirections.actionMapsScreenToLoginScreen())
                }
                return true;
            }

        })

    }

    private fun removePref() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            remove(getString(R.string.preference_email_key))
            apply()
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
            return
        }

        val locationBuilder = LocationRequest.Builder(5000)
            .setIntervalMillis(6000)
            .setMinUpdateIntervalMillis(5000)
            .setPriority(
                Priority.PRIORITY_HIGH_ACCURACY
            )

        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        currentLocation = location
//                        Toast.makeText(
//                            requireContext(), currentLocation.latitude.toString() + "" +
//                                    currentLocation.longitude, Toast.LENGTH_SHORT
//                        ).show()
                        val supportMapFragment =
                            (childFragmentManager.findFragmentById(R.id.map_fragment) as
                                    SupportMapFragment?)!!
                        supportMapFragment.getMapAsync { googleMap ->
                            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                            val markerOptions =
                                MarkerOptions().position(latLng).title("Current Location")
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
                            googleMap.addMarker(markerOptions)
                        }
                    }
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationBuilder.build(),
            mLocationCallback,
            null
        )


    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                fetchLocation()
            }
        }
    }
}