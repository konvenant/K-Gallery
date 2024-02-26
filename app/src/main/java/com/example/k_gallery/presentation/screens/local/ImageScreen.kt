package com.example.k_gallery.presentation.screens.local

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.calculateImageSize
import com.example.k_gallery.presentation.util.deleteImage
import com.example.k_gallery.presentation.viewmodel.ImageViewModel
import kotlinx.coroutines.delay


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ImageScreen(
    navController: NavController,
    imageIndex: String,
    folderId: String
){

    val windowInsetsController = LocalView.current.windowInsetsController
    val imageViewModel : ImageViewModel = hiltViewModel()
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


    val favStatus by imageViewModel.favStatus.observeAsState()
    var favouriteResult by remember { mutableStateOf(false) }
    var deletionResult by remember { mutableStateOf(false) }
    var deletionConfirmation by remember { mutableStateOf(false) }
    val deleteImagLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ){ activityResult ->
            if (activityResult.resultCode == RESULT_OK ){
                Toast.makeText(context,"Deleted Image Successfully", Toast.LENGTH_LONG).show()
                deletionResult = true
            }
            else{
                val err = imageViewModel.deleteErr
                Toast.makeText(context,"Error Deleting Image : $err", Toast.LENGTH_LONG).show()
            }
        }
    var indexHolder by rememberSaveable {
        mutableStateOf(imageIndex.toInt())
    }
    val zoomState = rememberTransformableState{zoom, _, _ ->
        scale.floatValue *=  zoom

    }
    val pagerState = rememberPagerState(imageIndex.toInt())

    val imageList by imageViewModel.img.observeAsState()
    LaunchedEffect(key1 = true){
        imageViewModel.getImg(folderId)
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






   Scaffold(
       topBar = {
           if (isVisible) {
               TopAppBar(
                   title = {

                   },
                   actions = {

                       IconButton(onClick = {
                           imageViewModel.addImageToFavorites(Uri.parse(imageUri),context)
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

                   val image = (imageList as Resource.Success<List<Image>>).data


                   image?.let {
                       HorizontalPager(
                           pageCount = it.size,
                           state = pagerState,
                           userScrollEnabled = isVisible
                       ) { index ->
                           indexHolder = index
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
                   imageViewModel.deleteImg(
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

@Composable
fun ShowDeleteConfirmationALertDialog(
    title: String,
    desc: String,
    deletedAccepted: () -> Unit,
    deletedDenied: () -> Unit

) {
    AlertDialog(
        onDismissRequest = {  },
        title = {Text(title)},
        text = { Text(desc)},
        confirmButton = {
            Button(
                onClick = deletedAccepted

            ) {
               Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { deletedDenied() }) {
                Text(text = "Cancel")
            }
        }
    )
}
 fun shareImage(context: Context, imageUri: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "image/*"
    intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(imageUri))
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    val chooser = Intent.createChooser(intent, "Share Image")
    context.startActivity(chooser)
}

 fun openImage(context: Context,imageUri: String){
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(Uri.parse(imageUri),"image/*")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    context.startActivity(intent)
}

fun onDeleteImageClicked(
    imageUri: String,
    context: Context,
    onDeleteNotification: (Boolean) -> Unit
) {
    val deletionResult = deleteImage(imageUri,context)
    onDeleteNotification(deletionResult)
}
@Composable
fun holder(){
//    Text(text="as i go on")
//    when(imageList){
//        is Resource.Loading -> {
//            Box(modifier = Modifier.fillMaxSize()){
//                CircularProgressIndicator(
//                    modifier = Modifier
//                        .size(40.dp)
//                        .align(Alignment.Center)
//                )
//            }
//        }
//        is Resource.Error -> {
//            val errorMessage = (imageList as Resource.Error<List<Image>>).message
//            Text(
//                text = "Error: $errorMessage",
//                color = Color.Red,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//            )
//        }
//
//        is Resource.Success -> {
//            val img = (imageList as Resource.Success<List<Image>>).data
//            LazyRow(
//                state = listState,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                items(img!!){ img ->
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.Black)
//                            .pointerInput(Unit) {
//                                detectTransformGestures { _, _, zoom, rotation ->
//                                    scale.floatValue *= zoom
//                                    rotationState.floatValue += rotation
//                                }
//                            }
//                    ){
//                        AsyncImage(
//                            model = img.path,
//                            contentDescription = null,
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .clickable {
//                                    isVisible = !isVisible
//                                }
//                                .graphicsLayer {
//                                    if (!isVisible) {
//                                        scaleX = maxOf(.5f, minOf(3f, scale.floatValue))
//                                        scaleY = maxOf(.5f, minOf(3f, scale.floatValue))
//                                    }
//                                }
//                        )
//                    }
//                }
//            }
//        }
//
//        else -> {}
//    }




    //        LazyRow(
//            state = listState,
//            modifier = Modifier.fillMaxSize()
//        ){
//            items(results){ image ->
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.Black)
//                        .pointerInput(Unit) {
//                            detectTransformGestures { _, _, zoom, rotation ->
//                                scale.floatValue *= zoom
//                                rotationState.floatValue += rotation
//                            }
//                        }
//                ){
//                    results.let {
//                        if (it.isNotEmpty()){
//                            AsyncImage(
//                                model = image.path,
//                                contentDescription = null,
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .clickable {
//                                        isVisible = !isVisible
//                                    }
//                                    .graphicsLayer {
//                                        if (!isVisible) {
//                                            scaleX = maxOf(.5f, minOf(3f, scale.floatValue))
//                                            scaleY = maxOf(.5f, minOf(3f, scale.floatValue))
//                                        }
//                                    }
//                            )
//                        }
//                    }
//
//                }
//            }
//        }

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