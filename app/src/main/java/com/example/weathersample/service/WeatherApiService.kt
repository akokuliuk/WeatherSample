package com.example.weathersample.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.weathersample.model.openweather.OpenWeatherApiConfig
import com.example.weathersample.model.openweather.OpenWeatherApiRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException

class WeatherApiService(
    private val config: OpenWeatherApiConfig
) {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun <T : Any> execute(request: OpenWeatherApiRequest<T>): Either<Throwable, T> =
        withContext(Dispatchers.IO) {
            either {
                val resp = client.get(request.getUri(config.baseUrl, config.apiKey).toString())
                ensure(resp.status.value in 200..210) { IOException(resp.status.toString()) }
                resp.body(request.getResponseType())
            }
        }
}
