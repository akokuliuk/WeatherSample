package com.example.weathersample.model.openweather

import android.net.Uri
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


interface OpenWeatherApiRequest<T : Any> {
    // TODO: Maybe it worth to receive a config object here
    fun getUri(baseUrl: String, apiKey: String): Uri
    fun getResponseType(): TypeInfo
}


data class OpenWeatherOneCallRequest(
    val lat: Double,
    val lon: Double
) : OpenWeatherApiRequest<OpenWeatherOneCallRequest.Response> {

    override fun getResponseType(): TypeInfo = typeInfo<Response>()

    override fun getUri(baseUrl: String, apiKey: String): Uri {
        return Uri.parse("$baseUrl/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$apiKey")
    }

    @Serializable
    data class Response(
        @SerialName("main") val main: WeatherFrame
    ) {
        @Serializable
        data class WeatherFrame(
            @SerialName("temp")
            val temperature: Float,
        )
    }
}
