package com.lock.applock.presentation.nav_graph
const val DETAIL_ARGUMENT_KEY = "id"
const val DETAIL_ARGUMENT_KEY2 = "name"
const val ROOT_GRAPH_ROUTE = "root"
const val HOME_GRAPH_ROUTE = "home"
sealed class Screen(val route:String){
    object Splash : Screen(route = "splash_screen")
    object AdminAccess : Screen(route = "admin_access")
    object Login : Screen(route = "login")
    object Home : Screen(route = "home_screen")
    object GeneralWebView : Screen(route = "general_web_view")
    object Setting : Screen(route = "setting_screen")
    object LicenseActivation : Screen(route = "license_activation")
    object WhiteList : Screen(route = "withe_list_screen")
    object BlackList : Screen(route = "black_list_screen")
    object BlackListWeb : Screen(route = "black_list_web_screen")
    object WhiteListWeb : Screen(route = "white_list_web_screen")
    object WhiteListWifi : Screen(route = "white_list_wifi")
    object AppManager : Screen(route = "app_manager")
    object WebManager : Screen(route = "web_manager")
    object NetworkControl : Screen(route = "network_control")
    object Detail : Screen(route = "detail_screen?id={id}&name={name}") {
        fun passNameAndId(
            id: Int = 0,
            name: String = "islam-San"
        ): String {
            return "detail_screen?id=$id&name=$name"
        }
    }
}
