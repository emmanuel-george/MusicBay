package com.george.sliceoflife.extrabeat.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.R
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var dialog: AlertDialog? = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        howToUseTV.setOnClickListener {
            val intent = Intent(this@SettingsActivity, HowToUseActivity::class.java)
            startActivity(intent)
        }

        reportBugTv.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("emmanuel.studio.apps@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Report Bug: Music Bay")
            startActivity(Intent.createChooser(intent, "Send Email"))
        }

        contactTv.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("emmanuel.studio.apps@gmail.com"))
            startActivity(Intent.createChooser(intent, "Send Email"))
        }
        ratingTv.setOnClickListener {

            val uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }

        shareTv.setOnClickListener {
            val appPackageName = packageName
            Log.d("package = ", appPackageName)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=$appPackageName"
            )
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, "Share using"))
        }

        privacyTv.setOnClickListener {
            val intent = Intent(this@SettingsActivity, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        termsConditionTv.setOnClickListener {
            val mBuilder = AlertDialog.Builder(this@SettingsActivity)
            val mView = layoutInflater.inflate(R.layout.dialog_terms_conditions, null)

            val agree = mView.findViewById<TextView>(R.id.agree)
            agree.setOnClickListener { dialog?.dismiss() }
            mBuilder.setView(mView)
            dialog = mBuilder.create()
            dialog!!.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(myIntent)
        return super.onOptionsItemSelected(item)
    }

}