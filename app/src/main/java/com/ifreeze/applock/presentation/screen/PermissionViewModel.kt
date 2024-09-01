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

/**
 * ViewModel for managing and checking permissions required by the application.
 *
 * This ViewModel maintains the state of various permissions and provides a method to
 * check whether the necessary permissions are granted or not.
 */
class PermissionViewModel : ViewModel() {

    /**
     * A [StateFlow] that represents the current state of permissions.
     * This flow can be observed to get updates on the status of permissions.
     */
    private val _permissionStates = MutableStateFlow(PermissionStates())
    val permissionStates: StateFlow<PermissionStates> = _permissionStates

    /**
     * Checks the status of all required permissions and updates the [permissionStates] state.
     *
     * This method checks whether the app has the necessary permissions, including:
     * - Device Admin permission
     * - Draw Overlays permission
     * - Install Unknown Apps permission
     * - Location permission
     * - Files permission
     *
     * The results of these checks are used to update the [permissionStates] state and
     * also to compile a list of permissions that have not been granted.
     *
     * @param context The [Context] used to check permissions.
     * @param compName The [ComponentName] used to check if the device admin permission is active.
     * @param deviceManager The [DevicePolicyManager] used to check device admin status.
     * @return A list of strings describing the permissions that have not been granted.
     */
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

        // Update the state with current permission statuses
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

/**
 * Data class representing the state of various permissions.
 *
 * @param adminPermission Boolean indicating if device admin permission is granted.
 * @param drawOverPermission Boolean indicating if draw overlays permission is granted.
 * @param unknownAppsPermission Boolean indicating if permission to install unknown apps is granted.
 * @param locationPermission Boolean indicating if location permission is granted.
 * @param filesPermission Boolean indicating if file access permission is granted.
 */
data class PermissionStates(
    val adminPermission: Boolean = false,
    val drawOverPermission: Boolean = false,
    val unknownAppsPermission: Boolean = false,
    val locationPermission: Boolean = false,
    val filesPermission: Boolean = false
)
