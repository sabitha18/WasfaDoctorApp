package com.wasfa.doctor.helper

import android.util.Log
import com.wasfa.doctor.network.response.Permissions

object PermissionManager {

    private var appPreferences: AppPreferences? = null

    fun init(preferences: AppPreferences) {
        appPreferences = preferences
    }

    fun hasPermission(permissionName: String): Boolean {
        Log.d("StaffPerms", "Saved check :::  :"+permissionName)
        val prefs = appPreferences ?: return false
        Log.d("StaffPerms", "Saved staff perms: ${prefs.getStaffPermissions()}")
        Log.d("StaffPerms", "Saved all perms: ${prefs.getAllPermissions()}")


        return prefs.getStaffPermissions().contains(permissionName)
    }

    fun getStaffPermissions(): List<String> {
        return appPreferences?.getStaffPermissions() ?: emptyList()
    }

    fun getAllPermissions(): List<Permissions> {
        return appPreferences?.getAllPermissions() ?: emptyList()
    }

    fun getAllowedPermissions(): List<Permissions> {
        val all = getAllPermissions()
        val staffAllowed = getStaffPermissions()
        return all.filter { staffAllowed.contains(it.name) }
    }

    fun getRestrictedPermissions(): List<Permissions> {
        val all = getAllPermissions()
        val staffAllowed = getStaffPermissions()
        return all.filterNot { staffAllowed.contains(it.name) }
    }
}

