package com.munchmatch.app.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

private enum class VenueType(val hue: Float) {
    Restaurant(BitmapDescriptorFactory.HUE_BLUE),
    Cafe(BitmapDescriptorFactory.HUE_YELLOW),
    Hotel(BitmapDescriptorFactory.HUE_GREEN),
    Pub(BitmapDescriptorFactory.HUE_ROSE)
}

private data class Venue(val name: String, val position: LatLng, val type: VenueType)

@SuppressLint("MissingPermission")
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)

    AndroidView(factory = { mapView }) { view ->
        view.getMapAsync { map ->
            setupMumbaiMap(map)
            addMumbaiVenues(map)
        }
    }
}

private fun setupMumbaiMap(map: GoogleMap) {
    val mumbai = LatLng(19.0760, 72.8777)
    map.uiSettings.isZoomControlsEnabled = true
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(mumbai, 10f))
}

private fun addMumbaiVenues(map: GoogleMap) {
    val venues = generateMumbaiVenues()
    venues.forEach { v ->
        map.addMarker(
            MarkerOptions()
                .position(v.position)
                .title(v.name)
                .icon(BitmapDescriptorFactory.defaultMarker(v.type.hue))
        )
    }
}

private fun generateMumbaiVenues(): List<Venue> {
    val boundsLat = 18.88..19.30
    val boundsLng = 72.77..73.10
    val rnd = java.util.Random(1234L)

    fun randLat() = boundsLat.start + rnd.nextDouble() * (boundsLat.endInclusive - boundsLat.start)
    fun randLng() = boundsLng.start + rnd.nextDouble() * (boundsLng.endInclusive - boundsLng.start)

    val venues = mutableListOf<Venue>()

    repeat(25) { i ->
        venues.add(Venue("Restaurant ${i + 1}", LatLng(randLat(), randLng()), VenueType.Restaurant))
    }
    repeat(25) { i ->
        venues.add(Venue("Cafe ${i + 1}", LatLng(randLat(), randLng()), VenueType.Cafe))
    }
    repeat(25) { i ->
        venues.add(Venue("Hotel ${i + 1}", LatLng(randLat(), randLng()), VenueType.Hotel))
    }
    repeat(25) { i ->
        venues.add(Venue("Pub ${i + 1}", LatLng(randLat(), randLng()), VenueType.Pub))
    }
    return venues
}

@Composable
private fun rememberMapViewWithLifecycle(context: Context): MapView {
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    return mapView
}
