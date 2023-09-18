package com.example.weathersample.service

import android.content.Context
import android.location.Geocoder
import arrow.core.Either
import com.google.android.gms.maps.model.LatLng

class GeocodingService(private val context: Context) {
    suspend fun getCityName(coordinates: LatLng): Either<Throwable, String> {
        return Either.catch {
            val geocoder = Geocoder(context)
            //TODO: androidx version?
            val location =
                geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
                    ?.firstOrNull()
            if (location == null) {
                ""
            } else if (location.locality?.isNotEmpty() == true) {
                location.locality!!
            } else if (location.subAdminArea?.isNotEmpty() == true) {
                location.subAdminArea!!
            } else if (location.adminArea?.isNotEmpty() == true) {
                location.adminArea!!
            } else {
                ""
            }
        }
    }
}
