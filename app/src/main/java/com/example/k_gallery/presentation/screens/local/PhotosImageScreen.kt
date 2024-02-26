package com.example.k_gallery.presentation.screens.local

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Build
import android.util.Log
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.calculateImageSize
import com.example.k_gallery.presentation.viewmodel.PhotosViewModel
import kotlinx.coroutines.delay


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun PhotosImageScreen(
    navController: NavController,
    imageIndex: String
){
    val context = LocalContext.current
    val windowInsetsController = LocalView.current.windowInsetsController
    val photosViewModel: PhotosViewModel = hiltViewModel()
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

    val favStatus by photosViewModel.favStatus.observeAsState()
    var favouriteResult by remember { mutableStateOf(false) }
    var deletionResult by remember { mutableStateOf(false) }
    var deletionConfirmation by remember { mutableStateOf(false) }
    val deleteImagLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ){ activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK ){
                Toast.makeText(context,"Deleted Image Successfully", Toast.LENGTH_LONG).show()
                deletionResult = true
            }
            else{
                val err = photosViewModel.deleteErr
                Toast.makeText(context,"Error Deleting Image : $err", Toast.LENGTH_LONG).show()
            }
        }

    val zoomState = rememberTransformableState{zoom, _, _ ->
        scale.floatValue *=  zoom
    }
    val pagerState = rememberPagerState(imageIndex.toInt())

    val imageList by photosViewModel.photoImages.observeAsState()
    LaunchedEffect(key1 = true){
//        photosViewModel.getAllImages()
        Log.d("TAG","$imageList")
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

   Scaffold(
       topBar = {
           if (isVisible) {
               TopAppBar(
                   title = {

                   },
                   actions = {

                       IconButton(onClick = {
                           photosViewModel.addImageToFavorites(Uri.parse(imageUri),context)
                           favouriteResult = true
                       }) {
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

                           DropdownMenuItem(
                               text = { Text(text = "Info") },
                               onClick = {  navController.navigate(NavHelper.DetailsScreen.route+"/${imgName}"+"/${encodedPath}"+"/${imgDate}"+"/${imgSize}") }
                           )

                           DropdownMenuItem(
                               text = { Text(text = "Edit in Gallery") },
                               onClick = { openImage(context,imageUri) }
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
                       IconButton(onClick = { shareImage(context,imgPath) }) {
                           Icon(
                               imageVector = Icons.Default.Share,
                               contentDescription = null,
                               tint = Color.White
                           )
                       }
                       IconButton(onClick = {
                           deletionConfirmation = true
                       }) {
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

                   val image = (imageList as Resource.Success<List<Image>>).data


                   image?.let {
                       HorizontalPager(
                           pageCount = it.size,
                           state = pagerState,
                           userScrollEnabled = isVisible
                       ) { index ->
                           imgName = it[index].name
                           imgPath = it[index].path
                           imageUri = it[index].uri.toString()
                           encodedPath = Uri.encode(imgPath)
                           imgDate = it[index].dateTaken.toString()
                           imgSize = calculateImageSize(it[index].size)
                               AsyncImage(
                                   model = image[index].path,
                                   contentDescription = null,
                                   modifier = imageModifier
                               )
                           }
                       }
                   }


               else -> {}
           }
       }

       if (deletionResult){
           LaunchedEffect(Unit){
               val message = if(deletionResult) "Image deleted successfully" else "Failed to delete image"
               val duration = Toast.LENGTH_LONG
               Toast.makeText(context,message,duration).show()
               navController.navigateUp()
           }
       }

       if (deletionConfirmation){
           ShowDeleteConfirmationALertDialog(
               title = "Delete Image",
               desc = "Are you sure want to delete this image?",
               deletedAccepted = {
                  photosViewModel.deleteImg(
                      Uri.parse(imageUri),
                      deleteImagLauncher,
                      context
                  )
                   deletionConfirmation = false
               }
           ) {
               deletionConfirmation = false
           }
       }

       if (favouriteResult) {
           LaunchedEffect(Unit){
               delay(1000)

               when(favStatus){
                   is Resource.Loading -> {
                       Toast.makeText(context,"Loading...",Toast.LENGTH_LONG).show()
                   }
                   is Resource.Success -> {
                       val message = (favStatus as Resource.Success).message
                       Toast.makeText(context,"Video Successfully Added to Favorite $message",Toast.LENGTH_LONG).show()
                   }
                   is Resource.Error -> {
                       val message = (favStatus as Resource.Error).message
                       Toast.makeText(context,"Error $message",Toast.LENGTH_LONG).show()
                   }

                   else -> {

                   }
               }
               favouriteResult = false
           }
       }

   }

}


