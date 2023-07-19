package com.example.androweather

import android.Manifest
import android.provider.Settings
import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androweather.dataclasses.DataModel

import com.example.androweather.ui.theme.AppForWheatherTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONObject

const val API_KEY = "d541c71a030e4f9e90a221843231707"


class MainActivity : ComponentActivity() {
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var fLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppForWheatherTheme {

                val daysList = remember {
                    mutableStateOf(listOf<DataModel>())
            }
                val dialogState = remember {
                    mutableStateOf(false)
                }
            val currentDay = remember {
                mutableStateOf(DataModel(
                    "",
                    "",
                    "0.0",
                    "",
                    "",
                    "0.0",
                    "0.0",
                    ""
                )
                )
            }
                if (dialogState.value) {
                    DialogSearch(dialogState, onSubmit = {
                        getData(it, this, daysList, currentDay)
                    })
                }
                else {
                getData("Saint-Petersburg", this, daysList, currentDay)}
                Image(
                    painter = painterResource(
                        id = R.drawable.background
                    ),
                    contentDescription = "im1",
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.5f),
                    contentScale = ContentScale.FillBounds
                )
                Column{
                    MainCard(daysList, currentDay, onClickSync = {
                        getData("Unknown", this@MainActivity, daysList, currentDay)
                    }, onClickSearch = {
                        dialogState.value = true
                    }
                    )
                }
            }
        }
        permissionListener()
        checkPermission()
    }
    override fun onResume() {
        super.onResume()
        checkLocation()
    }
    private fun checkLocation(){
        if(isLocationEnabled()){
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(this, object : DialogManager.Listener{
                override fun onClick() {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }
    private fun isLocationEnabled(): Boolean{
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation(){
        val daysList = mutableStateOf(listOf<DataModel>())

        val currentDay =  mutableStateOf(DataModel(
                "",
                "",
                "0.0",
                "",
                "",
                "0.0",
                "0.0",
                ""
            )
            )

        if(!isLocationEnabled()){return}
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient
            .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener{
                getData("${it.result.latitude},${it.result.longitude}", this, daysList, currentDay)
            }
    }
    fun isPermissionGranted(p: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, p) == PackageManager.PERMISSION_GRANTED
    }
    private fun checkPermission(){
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private fun permissionListener(){
        fLocationClient = LocationServices.getFusedLocationProviderClient(this)
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) {
        if(it){
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show()

        }
    }
  }

}
private fun getData(    city: String, context: Context,
                        daysList: MutableState<List<DataModel>>,
                        currentDay: MutableState<DataModel>){
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
            "&q=$city" +
            "&days=" +
            "3" +
            "&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val list = getWeatherByDays(response)
            currentDay.value = list[0]
            daysList.value = list
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}


private fun getWeatherByDays(response: String): List<DataModel>{
    if (response.isEmpty()) return listOf()
    val list = ArrayList<DataModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()){
        val item = days[i] as JSONObject
        list.add(
            DataModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                item.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()

            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c"),
    )
    return list
}








