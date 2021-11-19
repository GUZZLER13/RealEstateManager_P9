package com.example.realestatemanager.utils

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions


object PermissionsUtils {

    fun hasLocationPermissions(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

}