#-#-#-#-#
Screens
#-#-#-#-#-#
AdminAccess
	-Main Composable function for the AdminAccess screen.
AppManager
	-Composable function to manage app settings.
BlackListWeb
	-Composable function for managing blacklisted websites.
BookmarkActivity
	-A class to save the bookmark list from i-freeze web browser
FullSystemScan
	-Activity class for performing a full system scan to detect affected files based on a hash 	list.
	-Manages permissions, initializes views, and performs the scanning operation.
KioskMode
	-Composable function that displays a list of applications in kiosk mode.
	-The list is populated with application names retrieved from preferences.
	-Clicking on an item will open the corresponding application.
LicenseActivation
	-Composable function that represents the License Activation screen.
	-It displays the status of the license and provides a button to activate the license key.
Login
	-Composable function that represents the Login screen.
	-It displays input fields for username and password, and a login button.
BlackList
	-Composable function to manage blacklist applications.
Scan
	-Composable function that manage scan settings.
ScanProperties
	-Composable function that check untrusted applications, device has lock screen or not, if the 		developer options enable, rooted device
SettingScreen
	-Composable function to manage the settings screen that includes the required permissions
SettingAdmin
	-Composable function to manage the Admin settings screen that includes the required 	   	 permissions
SupportTeam
	-Composable function to send a ticket
WebManager
	-Composable function that represents the WebManager screen. This screen provides
	settings to manage blacklisted and whitelisted URLs, allowing the user to toggle
	between these settings and navigate to respective screens for further management.
WhiteListWeb
	-Composable function that represents the Whitelist Websites screen. This screen allows
	-the user to add websites to a whitelist and manage them by removing unwanted entries.
WhiteListWifi
	-Composable function that represents the whitelist wifi screen.
WhiteListActivity
	-Composable function that represents the whitelist application screen.

#-#-#-#-#
Services
#-#-#-#-#
	
AccessibilityServices
	-Accessibility service for managing application access based on user-defined preferences.
AdminService
	-DeviceAdminReceiver subclass for handling device administration events.
AutoSyncWorker
	-A worker that performs automatic synchronization tasks, including network requests and system 	checks.
ForceCloseKiosk
	-A service that manages a chat head view for a kiosk mode. The service allows for showing
	a floating chat head with options and handles password protection to stop kiosk mode.
ForceCloseLocation
	-A service that manages a chat head view to prompt the user to enable location services.
ForceCloseService
	-A service that manages a chat head view as an overlay on the screen.
ForceCloseWifi
	-A service that manages a chat head view as an overlay on the screen, specifically for 	handling Wi-Fi related actions.
LocationService
	-A service that monitors location settings and manages an overlay layout based on location 	provider status.
NetworkMonitoringService
	-A service that monitors network connectivity and manages Wi-Fi connectivity information.
WebrtcService
	- A foreground service that handles WebRTC-related operations such as managing calls,
 	 screen sharing, and notifications. Implements `MainRepository.Listener` to receive
 	 updates from the `MainRepository`.
WebrtcServiceRepository
	- A repository class that handles starting and managing WebRTC-related intents for the 		`WebrtcService`.
	-Uses background threads to start the service with various actions.
