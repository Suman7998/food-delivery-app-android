package com.munchmatch.app.ui.screens

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.munchmatch.app.notifications.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MultimediaScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val videoUri = remember { mutableStateOf<Uri?>(null) }
    val audioUri = remember { mutableStateOf<Uri?>(null) }
    val locationText = remember { mutableStateOf("Location: Unknown") }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && imageUri.value != null) {
            NotificationHelper.postMessage(context, "Photo saved", "Saved to gallery")
        }
    }
    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && videoUri.value != null) {
            NotificationHelper.postMessage(context, "Video saved", "Saved to gallery")
        }
    }
    val audioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val uri = data?.data
            if (uri != null) {
                audioUri.value = uri
                NotificationHelper.postMessage(context, "Audio recorded", "Saved to device")
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val loc = getBestLastKnownLocation(context)
            if (loc != null) {
                locationText.value = "Location: ${loc.latitude}, ${loc.longitude}"
            } else {
                locationText.value = "Location: Unavailable"
            }
        } else {
            locationText.value = "Location: Permission denied"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Multimedia & GPS", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            imageUri.value = createImageUri(context)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, imageUri.value)
            }
            photoLauncher.launch(intent)
        }) { Text("Take Photo") }

        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            videoUri.value = createVideoUri(context)
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, videoUri.value)
                putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
                putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            }
            videoLauncher.launch(intent)
        }) { Text("Record Video") }

        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
            audioLauncher.launch(intent)
        }) { Text("Record Audio") }

        Spacer(Modifier.height(8.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }) { Text("Get GPS Location") }
        Text(locationText.value)
    }
}

private fun createImageUri(context: Context): Uri? {
    val name = "IMG_${timestamp()}"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MunchMatch")
        }
    }
    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

private fun createVideoUri(context: Context): Uri? {
    val name = "VID_${timestamp()}"
    val contentValues = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, "$name.mp4")
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/MunchMatch")
        }
    }
    return context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
}

private fun timestamp(): String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

private fun getBestLastKnownLocation(context: Context): Location? {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
    var best: Location? = null
    for (p in providers) {
        val l = try { lm.getLastKnownLocation(p) } catch (e: SecurityException) { null }
        if (l != null && (best == null || l.accuracy < best!!.accuracy)) {
            best = l
        }
    }
    return best
}
