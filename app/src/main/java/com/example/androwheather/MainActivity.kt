package com.example.androwheather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.androwheather.ui.theme.AppForWheatherTheme

const val API_KEY = "9b29ac3510be8ad82ba5cacf1b2fe91e"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppForWheatherTheme {
                MainScreen()
            }
        }
    }
}

