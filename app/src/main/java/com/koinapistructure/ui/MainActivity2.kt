package com.koinapistructure.ui

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.koinapistructure.R


class MainActivity2 : AppCompatActivity() {
/*
    lateinit var listView: ListView
    lateinit var text: TextView
    lateinit var button: Button*/


    private var textView: TextView? = null
    private var listView: ListView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);

        if (!hasUsageStatsPermission()) {
            requestUsageStatsPermission();
        } else {
            updateUsageStats()
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }

    fun updateUsageStats() {
        val usageStatsMap = getAppUsageStats(this)
        val packageManager = packageManager
        val stringBuilder = StringBuilder()

        println("MainActivity2.updateUsageStats:::::usageStatsMap size: ${usageStatsMap.size}")

        for ((packageName, aggregatedStats) in usageStatsMap) {
            val totalUsageTime = aggregatedStats.totalTimeInForeground / 1000 // in seconds
            if (totalUsageTime == 0L) continue

            // Get app name and icon
            val appInfo = try {
                packageManager.getApplicationInfo(packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                println("MainActivity2.updateUsageStats:::::PackageManager.NameNotFoundException for package: $packageName")
                continue
            }

            val appName = packageManager.getApplicationLabel(appInfo).toString()
            val appIcon = packageManager.getApplicationIcon(appInfo)

            // Log each package's data
            println("MainActivity2.updateUsageStats:::::Processing $appName : $totalUsageTime secs")

            // For demonstration purposes, we're just appending the app name to the string.
            // You can use the appIcon drawable as needed in your UI.
            stringBuilder.append("$appName : $totalUsageTime secs\n\n")
        }

        println("MainActivity2.updateUsageStats:::::Final StringBuilder Output:\n$stringBuilder")
    }

    private fun getAppUsageStats(context: Context): Map<String, UsageStats> {
        // Example implementation of getAppUsageStats
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (1000 * 60 * 60 * 24) // last 24 hours
        val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        val usageStatsMap = mutableMapOf<String, UsageStats>()
        for (usageStats in usageStatsList) {
            usageStatsMap[usageStats.packageName] = usageStats
        }
        return usageStatsMap
    }


}
