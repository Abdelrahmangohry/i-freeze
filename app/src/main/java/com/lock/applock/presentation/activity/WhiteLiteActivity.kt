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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lock.applock.R
import com.lock.applock.helper.getAppIconByPackageName
import com.lock.applock.helper.toImageBitmap
import com.lock.applock.presentation.AppsViewModel
import com.lock.applock.presentation.activity.ui.theme.AppLockTheme
import com.lock.applock.ui.theme.Shape
import com.lock.data.model.AppsModel
import com.patient.data.cashe.PreferencesGateway

//@Composable
//fun  WhiteList(viewModel: AppsViewModel = hiltViewModel(), navController: NavController){
//    val preference = PreferencesGateway(LocalContext.current)
//    var allowedAppsList by remember { mutableStateOf(preference.getList("allowedAppsList") ?: mutableListOf()) }
//    WhiteAppList(allowedAppsList, viewModel,onBackPressed = { navController.popBackStack() })
//
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteList(viewModel: AppsViewModel = hiltViewModel(), navController: NavController) {
    val preference = PreferencesGateway(LocalContext.current)
    var allowedAppsList by remember { mutableStateOf(preference.getList("allowedAppsList") ?: mutableListOf()) }

    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = "Whitelisted Applications",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 15.dp)
                    .padding(horizontal = 30.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.fillMaxWidth()
                    .background(color = Color.White)
                    .padding(15.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black, // Text color // Color of the leading icon
                    unfocusedBorderColor = Color.LightGray, // Border color when unfocused
                    focusedBorderColor = Color.Black,
                    cursorColor = Color.Black,
                ),
                maxLines = 1,
                label = { Text(text = "Package Name", color = Color.Black) },
                placeholder = { Text(text = "Enter Package Name") },
            )

            Button(
                onClick = {
                    if (inputText.isNotEmpty()) {
                        allowedAppsList =
                            allowedAppsList.toMutableList().apply { add(inputText.lowercase().trim()) }
                        preference.saveList("allowedAppsList", allowedAppsList)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
            ) {
                Text("Submit", color = Color.White)
            }
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(allowedAppsList.size) { index ->
                AppListItemWhiteList(
                    app = allowedAppsList[index],
                    onDeleteClick = {
                        // Handle delete action here
                        allowedAppsList = allowedAppsList.toMutableList().apply {
                            remove(allowedAppsList[index])
                        }
                        preference.saveList("allowedAppsList", allowedAppsList)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun AppListItemWhiteList(app: String, onDeleteClick: () -> Unit) {
    val imageBitmap = LocalContext.current.getAppIconByPackageName(app)?.toImageBitmap()
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
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = app,
                    color = Color.Black,
                    modifier = Modifier.weight(1f).padding(8.dp),
                )
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(30.dp).padding(end = 8.dp),

                    ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Black
                    )
                }

            }

        }
    }
}
