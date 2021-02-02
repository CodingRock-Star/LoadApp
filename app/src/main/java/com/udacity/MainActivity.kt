package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    var fileName = ""
    var checkStatus = ""
    private var downloadID: Long = 0
    private var downloadManager: DownloadManager? = null

    private lateinit var notificationManager: NotificationManager


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        retrofit.isChecked = false
        load.isChecked = false
        glide.isChecked = false

        createChannel(
            CHANNEL_ID,
            getString(R.string.notificationchannelname)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        buttonenable.setOnClickListener {
            enableAndSendCustomUrl()
        }
        custombutton.setOnClickListener {
            //checking the internet connection...
          //  checkInternetConnectioin()
            if (URL.length > 0) {
                URL = ""
            }
            when {
                customurl.isEnabled -> {
                    enableRadioButtons()
                    if (Util.isValidUrl(customurl.text.toString())) {
                        URL = customurl.text.toString()
                        fileName = applicationContext.resources.getString(R.string.customurltext)
                        download()
                    } else {
                        Toast.makeText(
                            this,
                            applicationContext.resources.getString(R.string.invalidurl),
                            Toast.LENGTH_LONG
                        ).show()
                            .toString()
                    }
                }
                glide.isChecked -> {
                    clearText(customurl)
                    URL = applicationContext.getString(R.string.glideUrl)
                    fileName = getString(R.string.glide)
                    download()
                }
                load.isChecked -> {
                    clearText(customurl)
                    URL =
                        applicationContext.getString(R.string.loadurl)
                    fileName = getString(R.string.load)
                    download()
                }
                retrofit.isChecked -> {
                    clearText(customurl)
                    URL = applicationContext.getString(R.string.retrofiturl)
                    fileName = getString(R.string.retrofit)
                    download()
                }
                else -> {
                    Toast.makeText(
                        this,
                        applicationContext.getString(R.string.select),
                        Toast.LENGTH_LONG
                    ).show()
                        .toString()

                }
            }
            //setting the URL to Util.
            Util.setUrl(URL)
        }

    }

    private fun checkInternetConnectioin() {
       if(!Util.isNetworkAvailable(applicationContext)){
           Util.showToast(applicationContext,applicationContext.resources.getString(R.string.nointernetconnection))
           return
       }
    }

    private fun enableAndSendCustomUrl() {
        customurl.visibility = View.VISIBLE
        customurl.isEnabled = true
        retrofit.isChecked = false
        load.isChecked = false
        glide.isChecked = false

    }

    private fun enableRadioButtons() {
        retrofit.isChecked = true
        load.isChecked = true
        glide.isChecked = true
        customurl.visibility = View.GONE
        customurl.isEnabled = false
    }

    private fun clearText(customurl: EditText?): Unit {
        if (customurl?.text != null) {
            if (customurl.text.toString().length > 0) {
                customurl.text.clear()
            }
        }

    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                val action = intent.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    val query = DownloadManager.Query()
                    query.setFilterById(downloadID)
                    val c = downloadManager!!.query(query)
                    if (c.moveToFirst()) {
                        val columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            checkStatus = applicationContext.getString(R.string.success)
                        } else if (DownloadManager.STATUS_FAILED == c.getInt(columnIndex) ||
                            DownloadManager.STATUS_PAUSED == c.getInt(columnIndex) ||
                            DownloadManager.STATUS_PENDING == c.getInt(columnIndex)
                        ) {
                            checkStatus = applicationContext.getString(R.string.failed)
                        }
                    }
                }
                custombutton.completedDownload()
                sendNotification(downloadID.toInt())

            }
        }
    }


    private fun createChannel(channelId: String, channelName: String) {
        // create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = applicationContext.getString(R.string.loadfile)

            notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun sendNotification(notificationId: Int) {
        notificationManager.sendNotification(
            fileName,
            this,
            checkStatus,
            notificationId
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager!!.enqueue(request)// enqueue puts the download request in the queue.


    }

    companion object {
        const val CHANNEL_ID = "loadAppChannelID"
        var URL = " "
    }

}
