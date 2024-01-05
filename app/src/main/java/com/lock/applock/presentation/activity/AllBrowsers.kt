package com.lock.applock.presentation.activity

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lock.applock.helper.getAppIconByPackageName
import com.lock.applock.helper.toImageBitmap
import com.lock.applock.presentation.AppsViewModel
import com.lock.applock.presentation.activity.ui.theme.AppLockTheme
import com.lock.applock.ui.theme.Shape
import com.lock.data.model.AppsModel
import com.patient.data.cashe.PreferencesGateway

@Composable
fun  AllBrowsersList(viewModel: AppsViewModel = hiltViewModel(), navController: NavController){
    val browsersList = listOf("com.android.chrome", "org.mozilla.firefox")
    val appsList = browsersList.map { packageName ->
        AppsModel(packageName, "App Name Placeholder", false, false)
    }
    viewModel.insertApps(appsList)

    val newList= viewModel.articlesItems.collectAsState()
    AllbrowsersList(newList.value, onBackPressed = { navController.popBackStack() })
    
}

@Composable
fun AllbrowsersList(
    listItems: List<AppsModel> = listOf(), onBackPressed: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF175AA8))) {
        Row (modifier = Modifier.fillMaxWidth().padding(top = 20.dp)){
            IconButton(onClick = { onBackPressed() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        Text(
            text = "All Browsers Applications",
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 15.dp).padding(horizontal = 35.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )}
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(listItems.size) { it ->
                allBrowsersListItem(listItems[it])
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun allBrowsersListItem(
    app: AppsModel) {
    val viewModel: AppsViewModel = hiltViewModel()
    Card(
        modifier = Modifier

            .fillMaxWidth()
            .background(Color(0xFF175AA8))
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),

        shape = Shape.large
    ) {
        val imageBitmap = LocalContext.current.getAppIconByPackageName(app.packageName)?.toImageBitmap()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(shape = Shape.medium)
                    .background(Color(0xFF175AA8))
            ) {

                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "App Icon",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column (modifier = Modifier.padding(start=10.dp)){
                Text(
                    text = app.appName,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    color = Color(0xFF175AA8)
                )
            }
            var isToggle = remember { mutableStateOf(app.statusWhite) }
            Box(
                modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterEnd
            ) {
                Switch(
                    checked = isToggle.value,
                    onCheckedChange = {
                        viewModel.updateApp(app.copy(status = false, statusWhite = it))
                        isToggle.value = !isToggle.value

                    }
                )
            }
        }
    }

}
