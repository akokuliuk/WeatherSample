package com.example.weathersample.service

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.weathersample.model.FavouriteLocation
import com.example.weathersample.model.openweather.OpenWeatherOneCallRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SavedLocationsService(
    private val androidContext: Context,
    private val weatherApiService: WeatherApiService
) {
    private val preferences: SharedPreferences by lazy {
        androidContext.getSharedPreferences(
            "SavedLocations",
            Context.MODE_PRIVATE
        )
    }

    private val _locations = MutableStateFlow<List<FavouriteLocation>>(emptyList())
    val locations: Flow<List<FavouriteLocation>>
        get() = _locations

    private suspend fun submitLocations(location: List<FavouriteLocation>) {
        preferences.edit {
            putString("value", Json.encodeToString(location))
        }
        loadLocations()
    }

    suspend fun saveLocation(location: FavouriteLocation) {
        loadLocations()
        _locations.emit(_locations.value + location)
        submitLocations(_locations.value)
    }

    private suspend fun loadLocations() {
        _locations.emit(
            Json.decodeFromString<List<FavouriteLocation>>(
                preferences.getString("value", "[]") ?: "[]"
            )
        )
    }

    suspend fun updateWeatherForAllSavedLocations() {
        val newLocations = _locations.value.map {
            val weather =
                weatherApiService.execute(OpenWeatherOneCallRequest(lat = it.lat, lon = it.lon))
                    .getOrNull()

            it.copy(
                weather = FavouriteLocation.Weather(
                    weather?.main?.temperature ?: 0f,
                    System.currentTimeMillis()
                )
            )

        }
        submitLocations(newLocations)
    }

    suspend fun removeLocation(location: FavouriteLocation) {
        submitLocations(_locations.value.filter { it != location })
    }
}
