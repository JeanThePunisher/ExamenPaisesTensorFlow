package com.jeandavid.tensorflowpaises

import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.bumptech.glide.Glide

class Map(private val context: Context, country: Country?, imgflag: ImageView, infoCont: TextView) :
    OnMapReadyCallback, OnMapClickListener {
    private var mMap: GoogleMap? = null
    private val country: Country?
    private val imgBand: ImageView
    private val info: TextView
    override fun onMapReady(googleMap: GoogleMap) {
        if (googleMap != null) {
            try {
                mMap = googleMap
                mMap!!.uiSettings.isZoomControlsEnabled = true

                //mMap.setMapType(4);
                CargarDatos()
            } catch (E: Exception) {
                E.printStackTrace()
            }
        }
    }

    override fun onMapClick(latLng: LatLng) {}
    fun getmMap(): GoogleMap? {
        return mMap
    }

    fun setmMap(mMap: GoogleMap?) {
        this.mMap = mMap
    }

    fun CargarDatos() {
        Glide.with(context).load(country?.linkban.toString()).into(imgBand)
        info.text = "Capital: ${country?.nameCapital.toString()}" +
                " Code ISO 2: ${country?.codeISO2.toString()}" +
                " Code ISO Num: ${country?.codeISONum.toString()}" +
                " Code ISO 3: ${country?.codeISO3.toString()}" +
                " Code FIPS: ${country?.codeFIPS.toString()}" +
                " Teléfono: ${country?.telPrefix.toString()}" +
                " Center: ${country?.center0.toString()} ${country?.center1.toString()}" +
                " Rectangle: O= ${country?.geoWest.toString()},E= ${country?.geoEast.toString()},N= ${country?.geoNorth.toString()},S= ${country?.geoSouth}"
        if (country != null) {
            val West: Double = country.geoWest
            val East: Double = country.geoEast
            val North: Double = country.geoNorth
            val South: Double = country.geoSouth
            val polyPais = PolylineOptions()
                .clickable(false)
                .add(
                    LatLng(North, West),  //0 0
                    LatLng(North, East),  //0 1
                    LatLng(South, East),  // 1 1
                    LatLng(South, West),  //1 0
                    LatLng(North, West)
                ) //0 0
            polyPais.color(Color.MAGENTA)
            polyPais.width(5f)
            mMap!!.addPolyline(polyPais)

            //mover camara
            val pais = LatLng(country.center0.toDouble(), country.center1.toDouble())
            val camPos = CameraPosition.Builder()
                .target(pais)
                .zoom(3f)
                .bearing(0f) //noreste arriba
                .build()
            val camUpd3 = CameraUpdateFactory.newCameraPosition(camPos)
            mMap!!.animateCamera(camUpd3)
        }
    }

    init {
        this.country = country
        imgBand = imgflag
        info = infoCont
    }
}