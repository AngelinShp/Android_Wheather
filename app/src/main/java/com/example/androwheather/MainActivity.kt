package com.example.androwheather

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text


import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.volley.toolbox.Volley
import com.example.androwheather.ui.theme.AppForWheatherTheme
const val API_KEY = "9b29ac3510be8ad82ba5cacf1b2fe91e"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppForWheatherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("SPB", this)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, context: Context) {
    val state = remember {
        mutableStateOf("Unknown")
    }
    Column(
        modifier = Modifier.fillMaxSize()
    )
    {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Temperature in $name: ${state.value} Cº")
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(onClick = {

            }, modifier = Modifier.fillMaxWidth().padding(5.dp)) {
                Text(text = "Refresh")
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppForWheatherTheme {

    }
}

fun getData(name: String, context: Context, mState: MutableState<String>) {
    val lot = 45
    val lon = 54
    val url = "api.openweathermap.org/data/2.5/forecast" +
            "?lat=${lot}&" +
            "lon=${lon}&" +
            "appid=${API_KEY}"

    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        url,
        {
                response->
            val obj = JSONObject(response)
            val temp = obj.getJSONObject("current")
            mState.value = temp.getString("temp_c")
            Log.d("MyLog","Response: ${temp.getString("temp_c")}")
        },
        {
            Log.d("MyLog","Volley error: $it")
        }
    )
    queue.add(stringRequest)
}
}