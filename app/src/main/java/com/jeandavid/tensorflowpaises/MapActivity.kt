package com.jeandavid.tensorflowpaises

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import java.io.Serializable


class MapActivity : AppCompatActivity(), Serializable {
    var country: Country? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val i = intent
        country = i.getSerializableExtra("countryData") as Country?
        cargarMap()
    }

    private fun cargarMap() {
        try {
            val imgflag = findViewById<View>(R.id.imagenBandera) as ImageView
            val textInfo = findViewById<View>(R.id.content) as TextView
            val mapManager = Map(this@MapActivity, country, imgflag, textInfo)
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapView) as SupportMapFragment?
            mapFragment!!.getMapAsync(mapManager)
        } catch (E: Exception) {
            E.printStackTrace()
        }
    }
}