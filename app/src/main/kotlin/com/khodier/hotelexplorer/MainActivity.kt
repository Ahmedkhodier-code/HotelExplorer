package com.khodier.hotelexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.khodier.hotelexplorer.core.designsystem.theme.HotelExplorerTheme
import com.khodier.hotelexplorer.navigation.HotelExplorerNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HotelExplorerTheme {
                HotelExplorerNavHost()
            }
        }
    }
}

