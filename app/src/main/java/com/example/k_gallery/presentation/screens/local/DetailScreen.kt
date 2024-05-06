package com.example.k_gallery.presentation.screens.local

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation",
    "SimpleDateFormat"
)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    name: String,
    path: String,
    dateTaken: String,
    size: String
) {

    val timeStamp = dateTaken.toLong() * 1000
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val date = Date(timeStamp)
    val formattedDate = dateFormat.format(date)
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Info")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack
                            , contentDescription = null
                        )
                    }
                }
            )
        }
    ) {

        Column(Modifier.fillMaxSize()) {
            Text(
                text = "Details",
                modifier = Modifier.padding(16.dp)
            )
            DetailItem(
                icon = Icons.Default.DevicesOther,
                title = name,
                desc = path
            )
            DetailItem(
                icon = Icons.Default.DateRange,
                title = formattedDate.toString(),
                desc = size
            )

        }

    }

}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation",
    "SimpleDateFormat"
)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen2(
    navController: NavController,
    name: String,
    path: String,
    dateTaken: String,
    size: String
) {


    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Info")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack
                            , contentDescription = null
                        )
                    }
                }
            )
        }
    ) {

        Column(Modifier.fillMaxSize()) {
            Text(
                text = "Details",
                modifier = Modifier.padding(16.dp)
            )
            DetailItem(
                icon = Icons.Default.DevicesOther,
                title = name,
                desc = path
            )
            DetailItem(
                icon = Icons.Default.DateRange,
                title = dateTaken,
                desc = size
            )

        }

    }

}

@Composable
fun DetailItem(
    icon: ImageVector,
    title: String,
    desc: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(imageVector = icon, contentDescription = null)
       Column (
           Modifier.fillMaxWidth().padding(4.dp), verticalArrangement = Arrangement.SpaceEvenly
               ){
           Text(text = title)
           Text(
               text = desc,
               style = TextStyle.Default.copy(
                   fontSize = 13.sp,
                   color = Color.Gray
               )
           )
       }

    }

}
