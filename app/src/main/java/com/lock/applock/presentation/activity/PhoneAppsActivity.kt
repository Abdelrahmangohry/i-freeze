 package com.lock.applock.presentation.activity


 import android.graphics.Bitmap
 import android.graphics.Canvas
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
 import androidx.compose.material.icons.filled.Check
 import androidx.compose.material.icons.filled.Search
 import androidx.compose.material3.Card
 import androidx.compose.material3.ExperimentalMaterial3Api
 import androidx.compose.material3.Icon
 import androidx.compose.material3.IconButton
 import androidx.compose.material3.Switch
 import androidx.compose.material3.SwitchDefaults
 import androidx.compose.material3.Text
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.collectAsState
 import androidx.compose.runtime.mutableStateOf
 import androidx.compose.runtime.remember
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.draw.clip
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.graphics.asImageBitmap
 import androidx.compose.ui.platform.LocalContext
 import androidx.compose.ui.text.TextStyle
 import androidx.compose.ui.text.font.FontWeight
 import androidx.compose.ui.text.style.TextAlign
 import androidx.compose.ui.unit.dp
 import androidx.compose.ui.unit.sp
 import androidx.hilt.navigation.compose.hiltViewModel
 import androidx.navigation.NavController
 import com.lock.applock.helper.getAppIconByPackageName
 import com.lock.applock.helper.getBlackListApps
 import com.lock.applock.helper.toImageBitmap
 import com.lock.applock.presentation.AppsViewModel
 import com.lock.applock.ui.theme.Shape
 import com.lock.data.model.AppsModel
 import com.patient.data.cashe.PreferencesGateway

 @Composable
fun BlackList(viewModel: AppsViewModel = hiltViewModel(), navController: NavController){
     viewModel.getAllApps()
     val newList= viewModel.articlesItems.collectAsState().value
             .sortedBy { it.packageName }
     BlackListApps(newList,viewModel,onBackPressed = { navController.popBackStack() } )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlackListApps(listItems: List<AppsModel> = listOf(),viewModel: AppsViewModel,onBackPressed: () -> Unit) {
    val selectedApps = remember { mutableSetOf<String>() }
    val searchText = remember { mutableStateOf("") }


    Column(modifier = Modifier
        .fillMaxSize()
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
                text = "Blacklist Applications",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 15.dp).padding(horizontal = 30.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
        }
// Search Box
        androidx.compose.material3.TextField(
            value = searchText.value,
            onValueChange = { searchText.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            label = { Text("Search...") },
            textStyle = TextStyle(color = Color.Black),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
            }

        )

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    val filteredList = listItems.filter {
                        it.appName.contains(searchText.value, ignoreCase = true)
                    }

                    items(filteredList.size) { it ->
                        AppListItem(filteredList[it], viewModel)
                        Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }

}

@Composable
fun AppListItem(app: AppsModel ,viewModel: AppsViewModel) {
    val imageBitmap = LocalContext.current.getAppIconByPackageName(app.packageName)?.toImageBitmap()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF175AA8))
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),
        shape = Shape.large
    ) {

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
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = app.appName,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    color = Color(0xFF175AA8)
                )

            }
            val isToggle =remember { mutableStateOf(app.status) }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Switch(
                    checked = isToggle.value,
                    onCheckedChange = {
                        viewModel.updateApp(app.copy(status = it, statusWhite = false))
                        isToggle.value =!isToggle.value
                    },
                    thumbContent = if (isToggle.value) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    }else {
                        null
                    }

                )
            }
        }
    }

}


