package com.ifreeze.applock.presentation.screen

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifreeze.applock.Receiver.MyDeviceAdminReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class PermissionViewModel : ViewModel() {

    private val _permissionState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val permissionState: StateFlow<Map<String, Boolean>> = _permissionState

    fun checkPermissions(context: Context, deviceManager: DevicePolicyManager, compName: ComponentName) {
        val permissions = mutableMapOf<String, Boolean>()

        permissions["Admin Permission"] = deviceManager.isAdminActive(compName)
        permissions["Over Draw"] = Settings.canDrawOverlays(context)
        permissions["Install Unknown Apps"] = context.packageManager.canRequestPackageInstalls()
        permissions["Location Permission"] = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val filePermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        permissions["Files Permission"] = filePermissionGranted

        // Update the permission state
        _permissionState.value = permissions
    }

    fun updatePermission(context: Context, permission: String) {
        viewModelScope.launch {
            // Trigger a fresh permission check and update the state
            val updatedPermissions = _permissionState.value.toMutableMap().apply {
                when (permission) {
                    "Admin Permission" -> this[permission] = (context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager).isAdminActive(
                        ComponentName(context, MyDeviceAdminReceiver::class.java)
                    )
                    "Over Draw" -> this[permission] = Settings.canDrawOverlays(context)
                    "Install Unknown Apps" -> this[permission] = context.packageManager.canRequestPackageInstalls()
                    "Location Permission" -> this[permission] = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    "Files Permission" -> this[permission] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) == PackageManager.PERMISSION_GRANTED
                    } else {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    }
                }
            }
            _permissionState.value = updatedPermissions
        }
    }
}
