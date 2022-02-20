package com.luciano.maps

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    private lateinit var map:GoogleMap
    private lateinit var points:ArrayList<LatLng>
    private var hayMarcador = false

    companion object {
        const val REQUEST_CODE_LOCATION = 0
        //lateinit var list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()
        points = arrayListOf()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //createMarker()
        createPolylines()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        map.setOnMapLongClickListener(this)
        enableMyLocation()

    }

    private fun createPolylines(){
        val polylineOptions = PolylineOptions()
            .add(LatLng(40.419173113350965,-3.705976009368897))
            .add(LatLng( 40.4150807746539, -3.706072568893432))
            .add(LatLng( 40.41517062907432, -3.7012016773223873))
            .add(LatLng( 40.41713105928677, -3.7037122249603267))
            .add(LatLng( 40.41926296230622,  -3.701287508010864))
            .add(LatLng( 40.419173113350965, -3.7048280239105225))
            .add(LatLng(40.419173113350965,-3.705976009368897))
            .width(15f)
            .color(ContextCompat.getColor(this, R.color.kotlin))


        val polyline = map.addPolyline(polylineOptions)

        polyline.startCap = RoundCap()
        polyline.endCap = RoundCap()
        //polyline.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.hexagram))

        val pattern = listOf(
            Dash(50F), Gap(10F)
        )
        polyline.pattern = pattern

        polyline.isClickable = true

        map.setOnPolylineClickListener { polyline -> changeColor(polyline) }
    }

    fun changeColor(polyline: Polyline){
        val color = (0..3).random()
        when(color){
            0 -> polyline.color = ContextCompat.getColor(this, R.color.red)
            1 -> polyline.color = ContextCompat.getColor(this, R.color.yellow)
            2 -> polyline.color = ContextCompat.getColor(this, R.color.green)
            3 -> polyline.color = ContextCompat.getColor(this, R.color.blue)
        }
    }

    private fun createMarker() {
        val coordinates = LatLng(28.043893, -16.539329)
        val marker = MarkerOptions().position(coordinates).title("Mi marker!")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            4000,
            null
        )
    }

    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isPermissionsGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
               android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) { return }
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if(!isPermissionsGranted()){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Boton pulsado", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estás en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }

    override fun onMapLongClick(point: LatLng) {
        val marker = MarkerOptions().position(point).title("Marcador Custom!")
        marker.snippet("Latitude:"+point.latitude+","+"Longitude:"+point.longitude)
        val polylineOptions = PolylineOptions()
            .add(LatLng(point.latitude,point.longitude))
            .width(15f)
            .color(ContextCompat.getColor(this, R.color.kotlin))
            //Esto es para asignar un maximo de marcadores
            //.width(3F)
        val custompolyline = map.addPolyline(polylineOptions)
        custompolyline.startCap = RoundCap()
        custompolyline.endCap = RoundCap()
        val pattern = listOf(
            Dash(50F), Gap(10F)
        )
        custompolyline.pattern = pattern

        custompolyline.isClickable = true

        //map.setOnPolylineClickListener { polyline -> changeColor(polyline) }

        map.setOnPolygonClickListener { map.clear();points.clear() }

        points.add(point);
        polylineOptions.addAll(points);
        map.addPolyline(polylineOptions);
        map.addMarker(marker);



    }

    override fun onMapClick(p0: LatLng) {
        // Clearing the markers and polylines in the google map
        map.clear()

        // Empty the array list
        points.clear()
    }

}