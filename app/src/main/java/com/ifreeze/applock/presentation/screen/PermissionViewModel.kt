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

    private val _permissionStates = MutableStateFlow(PermissionStates())
    val permissionStates: StateFlow<PermissionStates> = _permissionStates

    fun checkAllPermissions(context: Context, compName: ComponentName, deviceManager: DevicePolicyManager): List<String> {
        val nonGrantedPermissions = mutableListOf<String>()

        val adminPermission = deviceManager.isAdminActive(compName)
        val drawOverPermission = Settings.canDrawOverlays(context)
        val unknownAppsPermission = context.packageManager.canRequestPackageInstalls()
        val locationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val filesPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        // Update the state
        _permissionStates.value = PermissionStates(
            adminPermission = adminPermission,
            drawOverPermission = drawOverPermission,
            unknownAppsPermission = unknownAppsPermission,
            locationPermission = locationPermission,
            filesPermission = filesPermission
        )

        // Add to non-granted list if necessary
        if (!adminPermission) nonGrantedPermissions.add("Admin Permission")
        if (!drawOverPermission) nonGrantedPermissions.add("Over Draw")
        if (!unknownAppsPermission) nonGrantedPermissions.add("Install Unknown Apps")
        if (!locationPermission) nonGrantedPermissions.add("Location Permission")
        if (!filesPermission) nonGrantedPermissions.add("Files Permission")

        return nonGrantedPermissions
    }
}

data class PermissionStates(
    val adminPermission: Boolean = false,
    val drawOverPermission: Boolean = false,
    val unknownAppsPermission: Boolean = false,
    val locationPermission: Boolean = false,
    val filesPermission: Boolean = false
)