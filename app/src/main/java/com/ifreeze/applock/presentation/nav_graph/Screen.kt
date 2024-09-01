package com.ifreeze.applock.presentation.nav_graph

/**
 * Constants for argument keys used in navigation.
 */
const val DETAIL_ARGUMENT_KEY = "id"
const val DETAIL_ARGUMENT_KEY2 = "name"

/**
 * Constants for route names used in the navigation graph.
 */
const val ROOT_GRAPH_ROUTE = "root"
const val HOME_GRAPH_ROUTE = "home"

/**
 * Sealed class representing different screens in the navigation graph.
 *
 * Each screen has a route string associated with it, which is used for navigation.
 */
sealed class Screen(val route:String){
    /** Route for the splash screen. etc*/
    object Splash : Screen(route = "splash_screen")
    object AdminAccess : Screen(route = "admin_access")
    object Login : Screen(route = "login")
    object Home : Screen(route = "home_screen")
    object Setting : Screen(route = "setting_screen")

    object OnboardingScreen1 : Screen(route = "on_boarding_Screen_1")
    object OnboardingScreen2 : Screen(route = "on_boarding_Screen_2")
    object OnboardingScreen3 : Screen(route = "on_boarding_Screen_3")
    object OnboardingScreen4 : Screen(route = "on_boarding_Screen_4")
    object SupportTeam : Screen(route = "support_team")
    object KioskMode : Screen(route = "kiosk_mode")
    object LicenseActivation : Screen(route = "license_activation")
    object SettingAdmin : Screen(route = "setting_admin")
    object WhiteList : Screen(route = "withe_list_screen")
    object BlackList : Screen(route = "black_list_screen")
    object BlackListWeb : Screen(route = "black_list_web_screen")
    object WhiteListWeb : Screen(route = "white_list_web_screen")
    object WhiteListWifi : Screen(route = "white_list_wifi")
    object AppManager : Screen(route = "app_manager")
    object WebManager : Screen(route = "web_manager")
    object NetworkControl : Screen(route = "network_control")
    object Scan : Screen(route = "scan")
    object ScanProperties : Screen(route = "scan_properties")

    object Detail : Screen(route = "detail_screen?id={id}&name={name}") {
        fun passNameAndId(
            id: Int = 0,
            name: String = "islam-San"
        ): String {
            return "detail_screen?id=$id&name=$name"
        }
    }
}
