package com.example.k_gallery.presentation.screens.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.FeaturedVideo
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NearbyError
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.local.Folder
import com.example.k_gallery.presentation.screens.remote.LoadingDialog
import com.example.k_gallery.presentation.screens.remote.ShowAuthAlertDialog
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.UserPreferences
import com.example.k_gallery.presentation.util.UserSharedPrefManager
import com.example.k_gallery.presentation.viewmodel.AuthViewModel
import com.example.k_gallery.presentation.viewmodel.FolderViewModel
import com.example.k_gallery.presentation.viewmodel.SharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    lifecycleOwner: LifecycleOwner,
    sharedViewModel: UserSharedViewModel
) {
    val folderViewModel: FolderViewModel = hiltViewModel()

    val authViewModel: AuthViewModel = hiltViewModel()


    val permissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    var callOnce by remember {
        mutableStateOf(true)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (permissionState.hasPermission) {
            if(callOnce){
                folderViewModel.fetchFoldersWithRecentImages()
                callOnce = false
            }
            ContentItems(
                folderViewModel = folderViewModel,
                navController = navController,
                authViewModel = authViewModel,
                lifecycleOwner = lifecycleOwner,
                sharedViewModel = sharedViewModel
            )
        } else {
            val textToshow = if (permissionState.shouldShowRationale) {
                "This permission is important for this app"
            } else {
                "Nothing to Show"
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
fun ContentItems(
    folderViewModel: FolderViewModel,
    navController: NavController,
    authViewModel: AuthViewModel,
    lifecycleOwner: LifecycleOwner,
    sharedViewModel: UserSharedViewModel
) {

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
    var expanded by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    val userPrefManager = remember {
        UserSharedPrefManager(context)
    }

    val userPref by remember{
        mutableStateOf(userPrefManager.getLoggedInPrefs())
    }

    var showLoadingDialog by remember {
        mutableStateOf(false)
    }
    var showErrorDialog by remember {
        mutableStateOf(false)
    }

    var errorMessageFromLogin by remember {
        mutableStateOf("")
    }


    var showDropDownMenu by remember {
        mutableStateOf(false)
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
                        if(showDropDownMenu){

                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {expanded = false}
                            ) {

                                DropdownMenuItem(
                                    text = { Text(text = "Account") },
                                    onClick = {  performLogin(
                                        userPref,
                                        authViewModel,
                                        navController,
                                        lifecycleOwner,
                                        context,
                                        showLoadingDialog ={
                                            showLoadingDialog = it
                                        },
                                        showErrorDialog = { show, err ->
                                            showErrorDialog = show
                                            errorMessageFromLogin = err
                                        },
                                        sharedViewModel
                                    )
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text(text="Favorites")},
                                    onClick = { navController.navigate(NavHelper.FavoriteScreen.route)}
                                )

                            }
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

                      showDropDownMenu = true

                      FolderList(foldersWithImages = folders!!, navController = navController){
                          navController.navigate(NavHelper.FoldersScreen.route+"/${it.folderId}/${it.folderName}")
                      }

                      if (showLoadingDialog){
                          LoadingDialog()
                      }

                      if (showErrorDialog){
                          ShowAuthAlertDialog(
                              title = "Error",
                              desc = errorMessageFromLogin,
                              positive = {
                                  authViewModel.userDetails.value = null
                                  performLogin(
                                      userPref,
                                      authViewModel,
                                      navController,
                                      lifecycleOwner,
                                      context,
                                      showLoadingDialog ={
                                          showLoadingDialog = it
                                      },
                                      showErrorDialog = { show, err ->
                                          showErrorDialog = show
                                          errorMessageFromLogin = err
                                      },
                                      sharedViewModel
                                  )
                              },
                              negative = { showErrorDialog = false  },
                              positiveText = "try again",
                              negativeText = "close",
                              icon = Icons.Default.NearbyError
                          )
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

fun performLogin(
    pref: UserPreferences,
    authViewModel: AuthViewModel,
    navController: NavController,
    lifecycleOwner: LifecycleOwner,
    context:Context,
    showLoadingDialog: (Boolean) -> Unit,
    showErrorDialog: (Boolean,String) -> Unit,
    sharedViewModel: UserSharedViewModel
) {
    if (pref.isLoggedIn){
       authViewModel.login(pref.email,pref.password)
        authViewModel.userDetails.observe(
            lifecycleOwner,Observer { response ->
                when(response){
                    is Resource.Success -> {
                        showLoadingDialog(false)
                        showErrorDialog(false,"")
                        response.data?.let {user ->
                            sharedViewModel.userData.postValue(user)
                            if (user.user.isEmailVerified){
                                if (user.user.country == "" || user.user.city == ""){
                                    navController.navigate(NavHelper.CompleteRegistrationScreen.route+"/${user.user.email}")
                                } else{
                                    sharedViewModel.userData.postValue(user)
                                    navController.navigate(NavHelper.HomeDashBoardScreenScreen.route)
                                    Toast.makeText(context,"Logged in as ${pref.email}",Toast.LENGTH_LONG).show()
                                }
                            } else{
                                authViewModel.sendToken(user.user.email)
                                navController.navigate(NavHelper.VerifyEmailScreen.route+"/${user.user.email}")
                            }
                        }
                    }


                    is Resource.Error -> {
                        showLoadingDialog(false)
                        val error = response.message.toString()
                        showErrorDialog(true,error)
                        authViewModel.userDetails.value = null
                    }

                    is Resource.Loading -> {
                        showLoadingDialog(true)
                        showErrorDialog(false,"")
                    }

                    else -> {

                    }
                }
            }
        )

    } else{
        navController.navigate(NavHelper.ChoiceScreen.route)
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
                items(
                    foldersWithImages,
                    key = {
                        it.folderId
                    }
                ){ folder ->
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



