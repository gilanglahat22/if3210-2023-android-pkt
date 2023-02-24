package com.pkt.majika.utils

import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {
    private const val TAG = "PermissionRequest"
    private const val RECORD_REQUEST_CODE = 101
    fun getRequestCode(): Int {return RECORD_REQUEST_CODE}
    fun isPermissionGranted(activity: AppCompatActivity, permission: String): Boolean {
        val permissionBool = ContextCompat.checkSelfPermission(activity, permission)
        return permissionBool == PackageManager.PERMISSION_GRANTED
    }
}