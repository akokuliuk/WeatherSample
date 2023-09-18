package com.example.weathersample.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import com.example.weathersample.model.FavouriteLocation
import com.example.weathersample.service.GeocodingService
import com.example.weathersample.service.SavedLocationsService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

class NewLocationViewModel(
    private val geocodingService: GeocodingService,
    private val savedLocationService: SavedLocationsService,
) : ViewModel() {
    private val selectedLocation: MutableStateFlow<LatLng?> = MutableStateFlow(null)
    val location: Flow<Either<State, SelectedLocation>>
        get() {
            return selectedLocation
                .throttleLatest(1000).flatMapLatest {
                    flow {
                        if (it != null) {
                            emit(State.LOADING.left())
                            geocodingService.getCityName(it).fold({
                                it.printStackTrace()
                                emit(State.ERROR.left())
                            }, {
                                if (it.isEmpty()) {
                                    emit(State.EMPTY.left())
                                } else {
                                    emit(SelectedLocation(it).right())
                                }
                            })
                        } else {
                            emit(State.EMPTY.left())
                        }
                    }
                }.shareIn(viewModelScope, SharingStarted.Lazily)
        }


    suspend fun onLocationHovered(coordinates: LatLng) {
        withContext(Dispatchers.IO) {
            selectedLocation.emit(coordinates)
        }
    }

    suspend fun submitLocation() {
        // TODO: Handle error here
        // TODO: Move this logic to the separated service
        val location = selectedLocation.value ?: return
        val locationName = geocodingService.getCityName(location).getOrElse { "" }
        savedLocationService.saveLocation(
            FavouriteLocation(
                id = System.currentTimeMillis(),
                name = locationName,
                lat = location.latitude,
                lon = location.longitude,
                weather = null
            )
        )
    }
}

enum class State {
    LOADING, ERROR, EMPTY
}

data class SelectedLocation(
    val name: String = "",
)

fun <T> Flow<T>.throttleLatest(delayMillis: Long): Flow<T> = this.conflate().transform {
    emit(it)
    delay(delayMillis)
}
