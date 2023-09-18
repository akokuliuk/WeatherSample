package com.example.weathersample.model

import kotlinx.serialization.Serializable

@Serializable
data class FavouriteLocation(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val name: String,
    val weather: Weather?
) {
    /**
     * TODO:
     * 1) Define measurement units
     * 2) Make a good name
     */
    @Serializable
    data class Weather(val temperature: Float, val updatedAt: Long)
}