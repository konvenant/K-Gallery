package com.example.k_gallery.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.local.Folder
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.FavoriteViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
 navController: NavController
) {

 val favoriteViewModel: FavoriteViewModel = hiltViewModel()
 val context = LocalContext.current

 val favImageList by favoriteViewModel.favoriteImg.observeAsState()

 val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

 val snackbarHostState = remember {
  SnackbarHostState()
 }

 val scope = rememberCoroutineScope()
 var showSnackbar by remember {
  mutableStateOf(false)
 }
 var errorMessageFromComposable by remember {
  mutableStateOf("")
 }

 var selected by remember {
  mutableStateOf(false)
 }

 var cancel by remember {
  mutableStateOf(false)
 }

 val selectedImages = remember {
  mutableStateListOf<Image>()
 }
 LaunchedEffect(key1 = showSnackbar ){
  if(showSnackbar){
   scope.launch {
    val result = snackbarHostState
     .showSnackbar(
      message = "Error: $errorMessageFromComposable",
      actionLabel = "Try again",
      duration = SnackbarDuration.Indefinite
     )
    when(result){
     SnackbarResult.ActionPerformed -> {

     }
     SnackbarResult.Dismissed -> {

     }
    }
   }
  }
 }

 Scaffold(
  modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
  topBar = {
   TopAppBar(
    title = {
     Row (
      verticalAlignment = Alignment.CenterVertically
     ) {
      Image(
       painter = painterResource(id = R.drawable.klogo),
       contentDescription = null,
       modifier = Modifier.size(40.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(text = "K-Gallery")
     }
    },
    scrollBehavior = scrollBehavior,

   )
  },
  snackbarHost = {
   SnackbarHost(hostState = snackbarHostState)
  }

 ) {
  Box(modifier = Modifier
   .fillMaxSize()
   .padding(it)) {
   when(favImageList) {
    is Resource.Loading -> {
     CircularProgressIndicator(
      modifier = Modifier
       .size(40.dp)
       .align(Alignment.Center)
     )
    }
    is Resource.Error -> {
     val errorMessage = (favImageList as Resource.Error<List<Image>>).message
     Text(
      text = "Error: $errorMessage",
      color = Color.Red,
      modifier = Modifier
       .fillMaxSize()
       .padding(16.dp)
     )
     showSnackbar = true
     errorMessageFromComposable = errorMessage!!
    }
    is Resource.Success -> {
     val images = (favImageList as Resource.Success<List<Image>>).data
          PhotoImageList(
           images = images!!,
           onClick = {

           },
           selected = false,
           selectedChange = {
            selected = !selected
           } ,
           enabled = selected ,
           selectedImagesChange = { imgs ->
            selectedImages.clear()
            selectedImages.addAll(imgs)
           },
           cancelAction = cancel
          )
     Toast.makeText(context,"Successful", Toast.LENGTH_LONG).show()
    }

    else -> {
     Text(
      text = "Else Block",
      color = Color.Red,
      modifier = Modifier
       .fillMaxSize()
       .padding(16.dp)
     )
    }
   }
  }
 }

}

