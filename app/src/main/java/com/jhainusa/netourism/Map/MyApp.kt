package com.jhainusa.netourism.Map

import android.app.Application
import androidx.preference.PreferenceManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        org.osmdroid.config.Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
    }
}
