package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.SentImages
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ViewAllReceivedImageScreen(
    navController: NavController,
    imageIndex: String,
    email : String,
    userViewModel: UserViewModel
){

    val windowInsetsController = LocalView.current.windowInsetsController

    val context = LocalContext.current
    var isVisible by remember {
    mutableStateOf(true)
}
    val scale = remember {
        mutableFloatStateOf(1f)
    }
val rotationState = remember {
    mutableFloatStateOf(0f)
}
    val listState = rememberLazyListState(
    initialFirstVisibleItemIndex = imageIndex.toInt()
)



    var deletionResult by remember { mutableStateOf(false) }
    var deletionConfirmation by remember { mutableStateOf(false) }

    var indexHolder by rememberSaveable {
        mutableStateOf(imageIndex.toInt())
    }
    val zoomState = rememberTransformableState{zoom, _, _ ->
        scale.floatValue *=  zoom
    }
    val pagerState = rememberPagerState(imageIndex.toInt())

    val imageList by userViewModel.receivedImages.observeAsState()
    LaunchedEffect(key1 = true){

        userViewModel.getReceivedImages(email)

    }
        LaunchedEffect(key1 = isVisible){

        if (!isVisible) {
            windowInsetsController?.hide(WindowInsets.Type.systemBars())
            windowInsetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else{
            windowInsetsController?.show(WindowInsets.Type.systemBars())
        }
    }

    val imageModifier = if (isVisible){
        Modifier
            .fillMaxSize()
            .clickable {
                isVisible = !isVisible
            }
            .background(Color.Black)
            .graphicsLayer {
                if (!isVisible) {
                    scaleX = maxOf(.5f, minOf(3f, scale.floatValue))
                    scaleY = maxOf(.5f, minOf(3f, scale.floatValue))
                }
            }
    } else{
        Modifier
            .fillMaxSize()
            .clickable {
                isVisible = !isVisible
//                                           navController.navigate(NavHelper.DetailsScreen.route + "/${folderId.toString()}")
            }
            .background(Color.Black)
            .transformable(zoomState)
            .graphicsLayer {
                if (!isVisible) {
                    scaleX = maxOf(.5f, minOf(3f, scale.floatValue))
                    scaleY = maxOf(.5f, minOf(3f, scale.floatValue))
                }
            }
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var imgName by remember {
        mutableStateOf("")
    }
    var imgPath by remember {
        mutableStateOf("")
    }
    var imgUri by remember {
        mutableStateOf("")
    }
    var imgDate by remember {
        mutableStateOf("")
    }
    var imgSize by remember {
        mutableStateOf("")
    }
    var encodedPath by remember {
        mutableStateOf("")
    }

    var imageUri by remember {
        mutableStateOf("")
    }

    val shareLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->

    }

    val editLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->

    }

    val addFavoriteImageResponse by userViewModel.addFavoriteImage.observeAsState()

    var toEmail by remember {
        mutableStateOf("")
    }

    var fromEmail by remember {
        mutableStateOf("")
    }


    var imageId by remember {
        mutableStateOf("")
    }




    var addResult by remember { mutableStateOf(false) }

    var isLoading by remember {
        mutableStateOf(false)
    }


    val deleteReceivedImageResponse by userViewModel.deleteRecievedImage.observeAsState()


    var isIconBtnEnabled by remember {
        mutableStateOf(true)
    }



    Scaffold(
       topBar = {
           if (isVisible) {
               TopAppBar(
                   title = {

                   },
                   actions = {

                       IconButton(
                           enabled = isIconBtnEnabled,
                           onClick = {
                               addResult = true
                               userViewModel.addFavoriteImage(email, imageUri)
                               isIconBtnEnabled = false
                           }
                       ) {
                           Icon(
                               imageVector = Icons.Outlined.Star,
                               contentDescription = null,
                               tint = Color.White
                           )
                       }
                       IconButton(onClick = { expanded = true }) {
                           Icon(
                               imageVector = Icons.Default.MoreVert,
                               contentDescription = null,
                               tint = Color.White
                           )
                       }
                       DropdownMenu(
                           expanded = expanded,
                           onDismissRequest = {expanded = false}
                       ) {
                           var showMessage by remember {
                               mutableStateOf(false)
                           }
                           DropdownMenuItem(
                               text = { Text(text = "Info") },
                               onClick = {  navController.navigate(NavHelper.DetailsScreen2.route+"/${imgName}"+"/${encodedPath}"+"/${imgDate}"+"/${imgSize}") }
                           )

                           DropdownMenuItem(
                               text = { Text(text = "Download") },

                               onClick = {
                                   if (imageUri.isNotEmpty()){
                                       downloadImage( imageUri,context = context, onSuccess = { drawable ->
                                           //Convert drawable to bitmap
                                           val bitmap = drawable.toBitmap()

                                           //save the bitmap to device
                                           saveBitmapToDevice(context,bitmap,"$email-image.jpg")
                                           showMessage = true
                                       })
                                       if (showMessage){
                                           Toast.makeText(context,"image saved to pictures",Toast.LENGTH_SHORT).show()
                                           showMessage = false
                                       }
                                   } else{
                                       Toast.makeText(context,"wait for image to load",Toast.LENGTH_SHORT).show()
                                   }
                                   expanded = false
                               }
                           )

                       }
                   },
                   navigationIcon = {
                       IconButton(onClick = { navController.navigateUp() }) {
                           Icon(
                               imageVector = Icons.Default.ArrowBack,
                               contentDescription = null,
                               tint = Color.White
                           )
                       }
                   },
                   colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                       containerColor = Color.Black,
                   )
               )

       }
       },
       bottomBar = {
           if (isVisible){
               BottomAppBar (
                   actions = {
                       IconButton(
                           onClick = { shareImage(context,imgPath) }
                       ) {
                           Icon(
                               imageVector = Icons.Default.Share,
                               contentDescription = null,
                               tint = Color.White
                           )
                       }
                       IconButton(
                           onClick = {
                               deletionConfirmation = true

                           }
                       ) {
                           Icon(
                               imageVector = Icons.Default.Delete,
                               contentDescription = null,
                               tint = Color.White
                           )
                       }

                   }, containerColor = Color.Black
                       )
           }
       }
   ) {
       Box(
           modifier = Modifier.fillMaxSize()
       ) {
           when (imageList) {

               is Resource.Success -> {

                   val image = (imageList as Resource.Success<SentImages>).data?.images!!

                   val filteredImages = image.filter {image ->
                       !image.isRecieverDeleted
                   }

                   filteredImages.let {
                       HorizontalPager(
                           pageCount = it.size,
                           state = pagerState,
                           userScrollEnabled = isVisible
                       ) { index ->
                           indexHolder = index
                           imgName = it[index].caption
                           imgPath = it[index].fromEmail
                           fromEmail = it[index].fromEmail
                           toEmail = it[index].toEmail
                           imageId = it[index]._id
                           imageUri = it[index].imageUrl
                           encodedPath = Uri.encode(imgPath)
                           imgDate = it[index].date.substring(0,22)
                           imgSize = "..MB"
                           AsyncImage(
                               model = imageUri,
                               contentDescription = null,
                               modifier = imageModifier
                           )

                       }
                   }
                   }


               else -> {}
           }
       }


        if (addResult) {

            when (addFavoriteImageResponse) {
                is Resource.Error -> {
                    isLoading = false
                    val err = (addFavoriteImageResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    addResult = false
                }

                is Resource.Success -> {
                    isLoading = false
                    val success = (addFavoriteImageResponse as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    addResult = false
                }

                is Resource.Loading -> {
                    isLoading = true
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading) {
                            LoadingDialog()
                        }
                    }
                }

                else -> {

                }

            }
        }



        if (deletionResult){

            when (deleteReceivedImageResponse) {
                is Resource.Error -> {
                    isIconBtnEnabled = true
                    isLoading = false
                    val err = (deleteReceivedImageResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    addResult = false
                    deletionResult = false
                }

                is Resource.Success -> {
                    isIconBtnEnabled = false
                    isLoading = false
                    val success = (deleteReceivedImageResponse as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    addResult = false
                    deletionResult = false
                    navController.navigateUp()
                }

                is Resource.Loading -> {
                    isLoading = true
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading) {
                            LoadingDialog()
                        }
                    }
                }

                else -> {

                }

            }
        }


        if (deletionConfirmation){
            ShowDeleteConfirmationALertDialog(
                title = "Delete Image",
                desc = "Are you sure want to delete this image?",
                deletedAccepted = {
                    deletionConfirmation = false
                    userViewModel.deleteReceivedImage(fromEmail,toEmail,imageId)
                    deletionResult = true
                }
            ) {
                deletionConfirmation = false
            }

        }


    }

}





//

//

//

//
//val imageList by imageViewModel.img.observeAsState()
//
//

//
//LaunchedEffect(key1 = true){
//    imageViewModel.getImg(folderId)
//}

//





//Scaffold(
//topBar = {
////            if (isVisible){
//
////        }
//) {
//    Text(text="assss")
//}