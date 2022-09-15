package com.jeandavid.tensorflowpaises

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jeandavid.tensorflowpaises.ml.Model
import org.json.JSONException
import org.tensorflow.lite.support.image.TensorImage
import java.io.IOException


class MainActivity : AppCompatActivity() {
    lateinit var Result: TextView
    lateinit var camara: Button
    lateinit var galeria: Button
    lateinit var imagenVi: ImageView
    var imageSize = 224
    private lateinit var requestQueue: RequestQueue
    lateinit var country: Country
    lateinit var buttonMap: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        country = Country()
        Result = findViewById(R.id.Name)
        camara = findViewById(R.id.btn_TomarFoto)
        galeria = findViewById(R.id.button2)
        imagenVi = findViewById(R.id.image)
        buttonMap = findViewById<View>(R.id.map) as Button
        buttonMap!!.visibility = View.GONE
        camara.setOnClickListener(View.OnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val camaraInten = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(camaraInten, 3)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        })
        galeria.setOnClickListener(View.OnClickListener {
            val camaraInten =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(camaraInten, 1)
        })
        buttonMap!!.setOnClickListener { Map(country!!) }
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                var imagen = data!!.extras!!["data"] as Bitmap?
                val dimension = Math.min(imagen!!.width, imagen.height)
                imagen = ThumbnailUtils.extractThumbnail(imagen, dimension, dimension)
                imagenVi!!.setImageBitmap(imagen)
                imagen = Bitmap.createScaledBitmap(imagen, imageSize, imageSize, false)
                classifyImage(imagen)
            } else {
                val dat = data!!.data
                var imagen: Bitmap? = null
                try {
                    imagen = MediaStore.Images.Media.getBitmap(this.contentResolver, dat)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                imagenVi!!.setImageBitmap(imagen)
                imagen = Bitmap.createScaledBitmap(imagen!!, imageSize, imageSize, false)
                classifyImage(imagen)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun classifyImage(images: Bitmap?) {
        try {
            val model: Model = Model.newInstance(applicationContext)

            // Creates inputs for reference.
            val image = TensorImage.fromBitmap(images)

            // Runs model inference and gets result.
            val outputs: Model.Outputs = model.process(image)
            //List<Category> probability = outputs.getProbabilityAsCategoryList();
            val probability: FloatArray = outputs.getProbabilityAsTensorBuffer().getFloatArray()
            var maxPos = 0
            var maxPosibility = 0f
            for (i in probability.indices) {
                if (probability[i] > maxPosibility) {
                    maxPosibility = probability[i]
                    maxPos = i
                }
            }
            val classe = arrayOf(
                "AR",
                "BE",
                "BR",
                "CO",
                "CR",
                "EC",
                "ES",
                "FR",
                "GB",
                "JP",
                "MX",
                "PT",
                "SE",
                "UY"
            )
            stringRequest(classe[maxPos])

            // Releases model resources if no longer used.
            model.close()
        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    fun stringRequest(inicial: String) {
        Log.d("name", "si ingresa$inicial")
        val url = "http://www.geognos.com/api/en/countries/info/all.json"
        val lstDatos = ArrayList<String>()
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonArray = response.getJSONObject("Results")
                    val jsonArray2 = jsonArray.getJSONObject(inicial)
                    country!!.name = jsonArray2.getString("Name")
                    country!!.telPrefix = jsonArray2.getString("TelPref")
                    val jsonArrayGtp = jsonArray2.getJSONArray("GeoPt")
                    country!!.center0 = jsonArrayGtp.getString(0)
                    country!!.center1 = jsonArrayGtp.getString(1)
                    val countryCodes = jsonArray2.getJSONObject("CountryCodes")
                    country!!.codeISO2 = countryCodes.getString("iso2")
                    country!!.codeISO3 = countryCodes.getString("iso3")
                    country!!.codeISONum = countryCodes.getString("isoN")
                    country!!.codeFIPS = countryCodes.getString("fips")
                    val capital = jsonArray2.getJSONObject("Capital")
                    country!!.nameCapital = capital.getString("Name")
                    val geoRectangle = jsonArray2.getJSONObject("GeoRectangle")
                    country!!.geoWest = geoRectangle.getString("West").toDouble()
                    country!!.geoEast = geoRectangle.getString("East").toDouble()
                    country!!.geoNorth = geoRectangle.getString("North").toDouble()
                    country!!.geoSouth = geoRectangle.getString("South").toDouble()
                    country!!.linkban = "http://www.geognos.com/api/en/countries/flag/$inicial.png"
                    Result!!.keyListener = null
                    Result!!.text = "Nombre del Pais: " + country!!.name
                    buttonMap!!.visibility = View.VISIBLE
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error -> error.printStackTrace() }
        requestQueue!!.add(request)
    }

    private fun Map(countryObj: Country) {
        try {
            val intent = Intent(this@MainActivity, MapActivity::class.java)
            intent.putExtra("countryData", countryObj)
            startActivity(intent)
        } catch (E: Exception) {
            E.printStackTrace()
        }
    }
}