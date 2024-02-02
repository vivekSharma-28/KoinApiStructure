package com.koinapistructure.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.koinapistructure.R
import com.koinapistructure.utils.requestCommonPermission


class MainActivity2 : AppCompatActivity() {

    lateinit var listView: ListView
    lateinit var text: TextView
    lateinit var button: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        listView = findViewById(R.id.listview)
        text = findViewById(R.id.totalapp)
        button=findViewById(R.id.check)

        requestCommonPermission(listOf(Manifest.permission.PACKAGE_USAGE_STATS))

        button.setOnClickListener { getAllApps() }
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun getAllApps() {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        // get list of all the apps installed
        val ril = packageManager.queryIntentActivities(mainIntent, 0)
        val componentList: List<String> = ArrayList()
        lateinit var name: String
        var i = 0

        // get size of ril and create a list
        val apps = arrayOfNulls<String>(ril.size)
        for (ri in ril) {
            if (ri.activityInfo != null) {
                // get package
                val res = packageManager.getResourcesForApplication(ri.activityInfo.applicationInfo)
                // if activity label res is found
                name = if (ri.activityInfo.labelRes != 0) {
                    res.getString(ri.activityInfo.labelRes)
                } else {
                    ri.activityInfo.applicationInfo.loadLabel(packageManager).toString()
                }
                apps[i] = name
                i++
            }
        }
        // set all the apps name in list view
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, apps)
        // write total count of apps available.
        text.text = ril.size.toString() + " Apps are installed"
    }

}
