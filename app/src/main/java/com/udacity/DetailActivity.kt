package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private var status: String = ""
    private var fileName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        title = applicationContext.resources.getString(R.string.detailsactivityname)

        fileName =
            intent.getStringExtra(applicationContext.resources.getString(R.string.content_filename))
                .toString()
        status = intent.getStringExtra(applicationContext.resources.getString(R.string.status))
            .toString()

        file_name.text = fileName
        statustext.text = status

        okBtn.setOnClickListener {
            var intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

}
