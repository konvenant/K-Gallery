package com.example.k_gallery.presentation.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FeaturedVideo
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.local.Folder
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.isPermanentlyDenied
import com.example.k_gallery.presentation.viewmodel.FolderViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val folderViewModel: FolderViewModel = hiltViewModel()

    val permissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (permissionState.hasPermission) {
            ContentItems(folderViewModel = folderViewModel, navController = navController)
        } else {
            val textToshow = if (permissionState.shouldShowRationale) {
                "This permission is important for this app"
            } else {
                "Unavailable"
            }

            Text(text = textToshow)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                permissionState.launchPermissionRequest()
            }) {
                Text(text = "Request Permission")
            }
        }
    }


}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentItems(folderViewModel: FolderViewModel, navController: NavController) {

    val folderList by folderViewModel.folderList.observeAsState()

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
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(NavHelper.FavoriteScreen.route)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
           snackbarHost = {
               SnackbarHost(hostState = snackbarHostState)
           },
            bottomBar = {
                BottomNavigation(navController = navController)
            }
        ) {
          Box(modifier = Modifier
              .fillMaxSize()
              .padding(it)) {
              when(folderList){
                  is Resource.Loading -> {
                      CircularProgressIndicator(
                          modifier = Modifier
                              .size(40.dp)
                              .align(Alignment.Center)
                      )
                  }
                  is Resource.Error -> {
                      val errorMessage = (folderList as Resource.Error<List<Folder>>).message
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
                      val folders = (folderList as Resource.Success<List<Folder>>).data
                      FolderList(foldersWithImages = folders!!, navController = navController){
                          navController.navigate(NavHelper.FoldersScreen.route+"/${it.folderId}/${it.folderName}")
                      }
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





@Composable
fun FolderList(
    foldersWithImages: List<Folder>,
    navController: NavController,
    onItemClick: (Folder) -> Unit
){

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            SelectionBoxes(navController, onPhotoClick = {
                navController.navigate(NavHelper.PhotosScreen.route){
                    popUpTo(NavHelper.HomeScreen.route){
                        inclusive = true
                    }
                }
            }, onFolderClick = {

            })
        }
        item{
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(900.dp),
                contentPadding = PaddingValues(top = 20.dp, start = 4.dp, end = 8.dp, bottom = 10.dp)
            ){
                items(foldersWithImages){ folder ->
                    FolderItem(folder = folder, onItemClick = onItemClick)
                }
            }
        }
    }
}

@Composable
fun SelectionBoxes(
    navController: NavController,
    onPhotoClick: () -> Unit,
    onFolderClick: () -> Unit
) {
   Row(
       Modifier
           .fillMaxWidth()
           .padding(16.dp),
       horizontalArrangement = Arrangement.SpaceEvenly
   ) {
       OutlinedButton(onClick = {
           onPhotoClick()
       }
       ) {
           Text(text = "All")
       }
       OutlinedButton(onClick = {
           onFolderClick()
       }) {
           Text(text = "Folders")
       }

   }
}



@Composable
fun FolderItem(
    folder: Folder,
    onItemClick: (Folder) -> Unit
){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(folder) }
            .padding(8.dp)
    ){
        AsyncImage(
            model = folder.recentImagePath,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = folder.folderName,
            style = TextStyle.Default.copy(fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = folder.folderCount!!.toString()+" items"+"   ${folder.folderSize!!} ",
                style = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )

        }

    }
}

@Composable
fun BottomNavigation (navController: NavController) {
    NavigationBar (
        containerColor = Color.Transparent
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {

            },
            label = {
                Text(text = "Images")
            },
            icon = {
                Icon(imageVector = Icons.Default.Image, contentDescription = null)
            }
        )


        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(NavHelper.VideoHomeScreen.route){
                    popUpTo(NavHelper.HomeScreen.route){
                        inclusive = true
                    }
                }
            },
            label = {
                Text(text = "Videos")
            },
            icon = {
                Icon(imageVector = Icons.Default.FeaturedVideo, contentDescription = null)
            }
        )
    }
}



