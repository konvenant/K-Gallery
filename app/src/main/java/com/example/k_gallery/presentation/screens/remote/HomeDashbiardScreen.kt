package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Doorbell
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PermMedia
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.presentation.graph.HomeNavGraph
import com.example.k_gallery.presentation.screens.local.performLogin
import com.example.k_gallery.presentation.util.BottomNavigationItem
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.AuthViewModel
import com.example.k_gallery.presentation.viewmodel.SharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    navController: NavHostController ,
    userSharedViewModel: UserSharedViewModel,
    lifecycleOwner: LifecycleOwner,
    userViewModel: UserViewModel
){

    val coroutineException = CoroutineExceptionHandler{ _, throwable ->

    }

    val navControllerBottomBar = rememberNavController()
    val user = userSharedViewModel.userData.value
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var notificationCount by rememberSaveable {
        mutableIntStateOf(0)
    }

    var hasNewNotification by rememberSaveable {
        mutableStateOf(false)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var showTopBar by remember {
        mutableStateOf(true)
    }

    val email = user!!.user.email

    val showFab = userViewModel.showFab.observeAsState()
    val context = LocalContext.current

    var selectedItemIndex by remember {
        mutableIntStateOf(0)
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember {
        derivedStateOf { currentBackStackEntry?.destination?.route?: NavHelper.HomeOnlineScreen.route }
    }

 Surface(modifier = Modifier.fillMaxSize()) {
     LaunchedEffect(Unit){
         userViewModel.getNotificationCount(email)
         userViewModel.getSavedImages(email)
         userViewModel.showFab(email)
     }
     ModalNavigationDrawer(
         drawerContent = {
            ModalDrawer(navController, scope,drawerState, user,notificationCount)
         },
         drawerState = drawerState
     ) {
         Scaffold (
             modifier = Modifier
                 .nestedScroll(scrollBehavior.nestedScrollConnection)
                 .fillMaxSize(),
             topBar = {
                if (showTopBar){
                    TopAppBar(
                        title = {

                        },
                        modifier = Modifier,
                        scrollBehavior = scrollBehavior,
                        actions = {
                            BadgedBox(badge = {
                                if (notificationCount != 0){
                                    Badge{
                                        Text(text = notificationCount.toString() )
                                    }
                                } else if(hasNewNotification) {
                                    Badge()
                                }

                            },modifier = Modifier.padding(16.dp)) {
                                IconButton(onClick = {
                                    navController.navigate(NavHelper.NotificationScreen.route+"/${user.user.email}")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null
                                    )
                                }
                            }
                            AsyncImage(
                                model = user.user.imageUrl,
                                contentDescription = "user image",
                                modifier = Modifier
                                    .size(35.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                               navController.navigate(NavHelper.ProfileViewOrEditScreen.route+"/${user.user.email}")
                                    },
                                placeholder = painterResource(id = R.drawable.full_logo),
                                error = painterResource(id = R.drawable.logo)
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                            }
                        }
                    )
                }
             },
             bottomBar = {
                 OnlineBottomNavigation(navControllerBottomBar,user.user.email,selectedItemIndex)
             },
             floatingActionButton = {
                 if (showFab.value == true){
                     FloatingActionButton(containerColor = Blue,onClick = { expanded = true }) {
                         Icon(
                             imageVector = Icons.Default.Add,
                             contentDescription = null,
                             tint = Color.White
                         )
                     }
                 }
             }

         ) { innerPadding ->
             val modifier = Modifier.padding(innerPadding)
             HomeNavGraph(navControllerBottomBar,userViewModel,email,navController,userSharedViewModel)
             if (showFab.value == true){
                 Box(
                     modifier = Modifier
                         .fillMaxSize()
                         .fillMaxWidth(),
                     contentAlignment = Alignment.BottomCenter
                 ) {
                     DropdownMenu(
                         expanded = expanded,
                         onDismissRequest = {expanded = false}
                     ) {

                         DropdownMenuItem(
                             text = { Text(text = "Save Media") },
                             onClick = {
                                 navController.navigate(NavHelper.SavedMediaScreen.route){
                                     popUpTo(navController.graph.findStartDestination().id)
                                     launchSingleTop = true

                                 }
                             },
                             trailingIcon = {
                                 Icon(imageVector = Icons.Default.PermMedia, contentDescription = null)
                             }
                         )

                         DropdownMenuItem(
                             text = { Text(text="Send Media")},
                             onClick = {
                                 navController.navigate(NavHelper.SendMediaScreen.route){
                                     popUpTo(navController.graph.findStartDestination().id)
                                     launchSingleTop = true

                                 }
                             },
                             trailingIcon = {
                                 Icon(imageVector = Icons.Default.Message, contentDescription = null)
                             }
                         )

                     }
                 }
             }




         }
     }
 }

//   LaunchedEffect(Unit){
       userViewModel.notificationCount.observe(lifecycleOwner, Observer { response ->
           when(response){
               is Resource.Success -> {
                   response.data?.let {
                       notificationCount = it.notice
                       hasNewNotification = false

                   }
               }
               is Resource.Loading -> {
                   hasNewNotification = true

               }

               is Resource.Error -> {
                   userViewModel.notificationCount.value = null

               }

               else -> {

               }
           }
       })
//   }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineBottomNavigation(navController: NavController,email:String,selectedIndex:Int){
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home,
            hasNew = false,
            route = NavHelper.HomeOnlineScreen.route
        ),
        BottomNavigationItem(
            title = "SavedMedia",
            selectedIcon = Icons.Filled.PermMedia,
            unSelectedIcon = Icons.Outlined.PermMedia,
            hasNew = false,
            route = NavHelper.SavedMediaScreen.route
        ),
        BottomNavigationItem(
            title = "SendMedia",
            selectedIcon = Icons.Filled.Message,
            unSelectedIcon = Icons.Outlined.Message,
            hasNew = false,
            route = NavHelper.SendMediaScreen.route
        ),
        BottomNavigationItem(
            title = "Favorites",
            selectedIcon = Icons.Filled.Favorite,
            unSelectedIcon = Icons.Outlined.Favorite,
            hasNew = false,
            route = NavHelper.OnlineFavoriteScreen.route
        ),
        BottomNavigationItem(
            title = "User",
            selectedIcon = Icons.Filled.Person,
            unSelectedIcon = Icons.Outlined.Person,
            hasNew = false,
            route = NavHelper.UserScreen.route
        )
    )

    var selectedItemIndex by remember {
        mutableIntStateOf(selectedIndex)
    }


    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEachIndexed { index, bottomNavigationItem ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == bottomNavigationItem.route } == true,
                onClick = {
                    navController.navigate(bottomNavigationItem.route){
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState = true
                        }
                        launchSingleTop = true
                        selectedItemIndex = index
                        restoreState = true
                    }
                },
                icon = {
                  BadgedBox(badge = {
                      if (bottomNavigationItem.badgeCount != null){
                      Badge{
                          Text(text = bottomNavigationItem.badgeCount.toString())
                      }
                      } else if(bottomNavigationItem.hasNew){
                      Badge()
                      }
                  }) {
                      Icon(
                          imageVector = if (index == selectedItemIndex) {
                              bottomNavigationItem.selectedIcon
                          } else bottomNavigationItem.unSelectedIcon,
                          contentDescription = bottomNavigationItem.title
                      )
                  }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Milk,
                    selectedIconColor = Blue,
                    selectedTextColor = Blue,
                    unselectedTextColor = Red,
                    unselectedIconColor = Red
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDrawer(
    navController: NavController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    user: User,
    notificationCount: Int
){
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            unSelectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home,
            route = NavHelper.HomeDashBoardScreenScreen.route,
            hasNew = false
        ),
        BottomNavigationItem(
            title = "Profile",
            unSelectedIcon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person,
            route = NavHelper.ProfileViewOrEditScreen.route+"/${user.user.email}",
            hasNew = false
        ),
        BottomNavigationItem(
            title = "Notifications",
            unSelectedIcon = Icons.Outlined.Notifications,
            selectedIcon = Icons.Filled.Notifications,
            hasNew = notificationCount != 0,
            badgeCount = notificationCount,
            route = NavHelper.NotificationScreen.route+"/${user.user.email}"
        ),
        BottomNavigationItem(
            title = "Settings",
            unSelectedIcon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings,
            hasNew = false,
            badgeCount = 2,
            route = NavHelper.UserSettingsScreen.route+"/${user.user.email}"
        )
    )
    ModalDrawerSheet {
     Row(
         verticalAlignment = Alignment.CenterVertically,
         horizontalArrangement = Arrangement.SpaceBetween,
         modifier = Modifier
             .fillMaxWidth(0.8f)
             .background(Blue)
             .padding(16.dp)
     ){
         Text(
             text = user.user.name,
             fontWeight = FontWeight.Bold,
             fontSize = 18.sp,
             color = Color.White
         )
         AsyncImage(
             model = user.user.imageUrl,
             contentDescription = "user image",
             modifier = Modifier
                 .size(35.dp)
                 .clip(CircleShape)
                 .clickable { }
         )
     }
        Spacer(modifier = Modifier.height(8.dp))
        items.forEachIndexed { index, item ->
            NavigationDrawerItem(
                label = {
                    Text(text = item.title)
                },
                selected = index == selectedItem,
                onClick = {
                    selectedItem = index
                    scope.launch {
                        drawerState.close()
                    }
                    navController.navigate(item.route){
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = if (index == selectedItem){
                        item.selectedIcon
                    } else item.unSelectedIcon
                        , contentDescription = item.title)
                },
                badge = {
                    if (item.hasNew){
                       Badge(){
                           Text(text = item.badgeCount.toString())
                       }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedIconColor = Red,
                    selectedContainerColor = Milk,
                    selectedTextColor = Red
                )
            )
        }
    }
}
