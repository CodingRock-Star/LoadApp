package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

//The extension method will send the notification.
fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    status: String,
    notificationID: Int
) {
    val intent = Intent(applicationContext, DetailActivity::class.java)
    intent.putExtra(applicationContext.getString(R.string.content_filename), messageBody)
    intent.putExtra(applicationContext.getString(R.string.status), status)

    val notificationPendingIntent = PendingIntent.getActivity(
        applicationContext
        , notificationID
        , intent
        , PendingIntent.FLAG_UPDATE_CURRENT
    )

    val downloadImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.iconscloud
    )
    val bigPictureStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(downloadImage)
        .bigLargeIcon(null)

    //creating a notification builder

    val notificationBuilder = NotificationCompat.Builder(
        applicationContext,
        MainActivity.CHANNEL_ID
    )
        .setSmallIcon(R.drawable.iconscloud).setContentText(messageBody)
        .setContentTitle(
            applicationContext.getString(R.string.notificationcontent)
        )
        .setAutoCancel(true)
        .setStyle(bigPictureStyle)
        .setLargeIcon(downloadImage)
        .addAction(
            R.drawable.iconscloud,
            applicationContext.getString(R.string.statusread),
            notificationPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    //notify with the notificationId and Notofication builder object.
    notify(notificationID, notificationBuilder.build())
}
