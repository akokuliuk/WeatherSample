@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.weathersample.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import arrow.core.Either
import arrow.core.left
import com.example.weathersample.R
import com.example.weathersample.ui.theme.Typography
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun MapComponent(
    vm: NewLocationViewModel
) {
    val scope = rememberCoroutineScope()
    val startingCoordinates = LatLng(50.4581964, 30.580561)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startingCoordinates, 10f)
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            vm.onLocationHovered(cameraPositionState.position.target)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
        )
        Image(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(id = R.drawable.baseline_grid_goldenratio_24),
            contentDescription = "Select marker"
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun MapScreen(vm: NewLocationViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    MapComponent(vm)
    LocationBottomSheet(vm.location) {
        scope.launch {
            vm.submitLocation()
            navController.popBackStack()
        }
    }
}

@Composable
fun LocationBottomSheet(
    location: Flow<Either<State, SelectedLocation>>,
    onSaveLocation: () -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val currentLocation = location.collectAsState(initial = State.LOADING.left())
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 128.dp,
        sheetDragHandle = {},
        sheetContent = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(128.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(currentLocation.value.fold({
                    it.name
                }, {
                    it.name
                }), style = Typography.headlineMedium)
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Some info about the place")
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = onSaveLocation
                ) {
                    Text("Save location")
                }
            }
        }) {}
}

