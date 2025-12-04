package com.munchmatch.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.munchmatch.app.R

object NotificationHelper {
    private const val CHANNEL_ID = "munchmatch.alerts"
    private const val CHANNEL_NAME = "Munch Match Alerts"
    private const val CHANNEL_DESC = "AI-based food discovery alerts"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    fun postSampleAlerts(context: Context) {
        ensureChannel(context)
        val alerts = listOf(
            Alert(
                title = "Pizza Palace · 4.7★",
                text = "Best items: Margherita, Mexican Wave · Location: FC Road, Pune"
            ),
            Alert(
                title = "Spice Route · 4.6★",
                text = "Best items: Paneer Tikka, Veg Biryani · Location: MG Road, Pune"
            ),
            Alert(
                title = "Cafe Mocha · 4.5★",
                text = "Best items: Red Velvet, Cheesecake · Location: Koregaon Park, Pune"
            ),
            Alert(
                title = "Sushi House · 4.4★",
                text = "Best items: Sushi Platter, Maki Rolls · Location: Baner, Pune"
            ),
            Alert(
                title = "Burger Hub · 4.3★",
                text = "Best items: Veg Classic, Paneer Melt · Location: Hinjewadi, Pune"
            )
        )
        val nm = NotificationManagerCompat.from(context)
        alerts.forEachIndexed { index, alert ->
            val n = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pizza)
                .setContentTitle(alert.title)
                .setContentText(alert.text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(alert.text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            nm.notify(1000 + index, n)
        }
    }

    fun postMessage(context: Context, title: String, text: String, id: Int = 2000) {
        ensureChannel(context)
        val n = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pizza)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(id, n)
    }

    private data class Alert(val title: String, val text: String)
}
