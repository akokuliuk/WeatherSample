package com.example.weathersample.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathersample.BuildConfig
import com.example.weathersample.model.openweather.OpenWeatherApiConfig
import com.example.weathersample.service.GeocodingService
import com.example.weathersample.service.SavedLocationsService
import com.example.weathersample.service.WeatherApiService
import com.example.weathersample.ui.favouriteLocations.FavouriteLocationsScreen
import com.example.weathersample.ui.map.MapScreen
import com.example.weathersample.ui.map.NewLocationViewModel
import com.example.weathersample.ui.theme.WeatherSampleTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            val geocodingService = remember {
                GeocodingService(this)
            }

            val weatherApiService = remember {
                WeatherApiService(
                    OpenWeatherApiConfig(
                        baseUrl = "https://api.openweathermap.org",
                        apiKey = BuildConfig.openweather_api_key
                    )
                )
            }

            val locationsService = remember {
                SavedLocationsService(this, weatherApiService)
            }


            val navHost = rememberNavController()

            WeatherSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navHost, startDestination = "favourite") {
                        composable("favourite") {
                            FavouriteLocationsScreen(navHost = navHost, locationsService)
                        }

                        composable("add_new") {
                            MapScreen(
                                NewLocationViewModel(
                                    geocodingService,
                                    locationsService,
                                ),
                                navHost
                            )

                        }
                    }
                }
            }
        }
    }
}
