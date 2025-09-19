package com.wasfa.doctor

import android.app.Application
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.PermissionManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PermissionManager.init(AppPreferences(this))
    }
}
