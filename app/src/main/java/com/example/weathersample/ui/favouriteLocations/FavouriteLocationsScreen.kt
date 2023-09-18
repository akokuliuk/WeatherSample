package com.example.weathersample.ui.favouriteLocations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weathersample.model.FavouriteLocation
import com.example.weathersample.service.SavedLocationsService
import com.example.weathersample.ui.theme.Typography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteLocationsScreen(
    navHost: NavHostController, savedLocationsService: SavedLocationsService
) {
    val locations = savedLocationsService.locations.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    LaunchedEffect("update_locations") {
        savedLocationsService.updateWeatherForAllSavedLocations()
    }

    Scaffold(floatingActionButton = {
        SmallFloatingActionButton(onClick = { navHost.navigate("add_new") }) {
            Icon(Icons.Filled.Add, contentDescription = "Add new place")
        }
    }, content = { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp)
        ) {

            items(items = locations.value, key = { it.id }) {
                LocationItem(location = it) {
                    scope.launch {
                        savedLocationsService.removeLocation(it)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationItem(location: FavouriteLocation, onDismiss: () -> Unit) {
    val showItem = remember { mutableStateOf(true) }
    val dismissState = rememberDismissState(confirmValueChange = {
        if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
            showItem.value = false
            true
        } else {
            false
        }
    })

    LaunchedEffect(showItem.value) {
        if (!showItem.value) {
            onDismiss()
        }
    }
    AnimatedVisibility(visible = showItem.value, exit = fadeOut()) {
        SwipeToDismiss(state = dismissState,
            background = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .background(
                            Color.Red
                        )
                ) {
                    Text(text = "GONE")
                }
            }, dismissContent = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 32.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "${location.name}: ${location.weather?.temperature ?: ""}",
                        style = Typography.headlineMedium
                    )
                }
            })
    }

}