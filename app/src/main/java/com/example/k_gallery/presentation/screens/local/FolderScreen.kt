package com.example.k_gallery.presentation.screens.local

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.viewmodel.ImageViewModel
import com.example.k_gallery.presentation.viewmodel.ImageViewState


@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersScreen(
    navController: NavController,
    folderId:String,
    folderName: String
){
    val imageViewModel : ImageViewModel = hiltViewModel()
    val folderSize = imageViewModel.folderMetrics.value?.first
    val folderCount = imageViewModel.folderMetrics.value?.second.toString()
    val context = LocalContext.current

    var selected by remember {
        mutableStateOf(false)
    }

    var cancel by remember {
        mutableStateOf(false)
    }

    val selectedImages = remember {
        mutableStateListOf<Image>()
    }


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    LaunchedEffect(folderId){
        imageViewModel.fetchFoldersWithRecentImages(folderId)
    }

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
                val err = imageViewModel.multipleDeleteErr
                Toast.makeText(context,"Error Deleting Image : $err", Toast.LENGTH_LONG).show()
            }
        }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (selected){
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "${selectedImages.size} Items Selected")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            selected = !selected
                            cancel = !cancel
                        }) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    actions = {
                        IconButton(onClick = {
                            deletionConfirmation = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            shareMultipleImages(context, selectedImages.map { it.uri })
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null
                            )
                        }
                    }
                )
            } else{
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = folderName)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$folderCount items    $folderSize",
                                style = TextStyle.Default.copy(
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        }
            ) {

        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ){
            when(imageViewModel.viewState.value){
                is ImageViewState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }
                is ImageViewState.Error -> {
                    val errorMessage = (imageViewModel.viewState.value as ImageViewState.Error).message
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
                is ImageViewState.Success -> {
                    val imageList = (imageViewModel.viewState.value as ImageViewState.Success).Images

                    ImageList(
                        images = imageList,
                        selected = selected,
                        onClick = {
                            val imageIndex = imageList.indexOf(it).toString()
                            navController.navigate(NavHelper.ImageScreen.route + "/$imageIndex"+"/$folderId")
                        },
                        selectedChange = {
//                            selected = it
                            selected = !selected
                        },
                        enabled = selected,
                        selectedImagesChange = { images ->
                            selectedImages.clear()
                            selectedImages.addAll(images)
                        },
                        cancelAction = cancel
                    )
                }

                else -> {}
            }
        }

        if (deletionResult){
            LaunchedEffect(Unit){
                val message = if(deletionResult) "Images deleted successfully" else "Failed to delete image"
                val duration = Toast.LENGTH_LONG
                Toast.makeText(context,message,duration).show()
                imageViewModel.fetchFoldersWithRecentImages(folderId)
                cancel = !cancel
                selected = !selected
            }
        }

        if (deletionConfirmation){
            ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedImages.size} Images",
                desc = "Are you sure want to delete these images? ",
                deletedAccepted = {
                    imageViewModel.deleteMultipleImage(
                        selectedImages,
                        deleteImagLauncher,
                        context
                    )
                    deletionConfirmation = false
                }
            ) {
                deletionConfirmation = false
            }
        }
    }
}


@SuppressLint("MutableCollectionMutableState")
@Composable
fun ImageList(
    images: List<Image>,
    onClick: (Image) -> Unit,
    selected: Boolean,
    selectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    selectedImagesChange: (MutableList<Image>) -> Unit,
    cancelAction: Boolean
){
    val selectedImages = remember {
        mutableStateListOf<Image>()
    }

    var isEnabled by remember {
        mutableStateOf(enabled)
    }

    var selectedAllChecked by remember {
        mutableStateOf(false)
    }

    var showDeleteTitle by remember {
        mutableStateOf(selected)
    }


    LaunchedEffect(cancelAction){
        selectedImages.clear()
        selectedAllChecked = selectedImages.size == images.size
    }

    LazyColumn(modifier = Modifier.fillMaxSize()){
        item {
            if (enabled){
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedAllChecked = !selectedAllChecked
                            if (selectedAllChecked) {
                                selectedImages.clear()
                                selectedImages.addAll(images)
                                Log.d("selectedImages", "$selectedImages")
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                            } else {
                                selectedImages.clear()
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedImages)
                        }
                        .padding(16.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select all",
                        modifier = Modifier.clickable {
                            selectedAllChecked = !selectedAllChecked
                            if (selectedAllChecked) {
                                selectedImages.clear()
                                selectedImages.addAll(images)
                                Log.d("selectedImages", "$selectedImages")
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                            } else {
                                selectedImages.clear()
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedImages)
                        }
                    )
                    Checkbox(
                        checked = selectedAllChecked,
                        onCheckedChange = {
                            selectedAllChecked = it
                            if (it){
                                selectedImages.clear()
                                selectedImages.addAll(images)
                                Log.d("selectedImages","$selectedImages")
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                            } else{
                                selectedImages.clear()
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedImages)
                        }
                    )
                }
            }
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(count = 3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(900.dp),
                contentPadding = PaddingValues(top = 40.dp)
            ){

                items(images){ image ->
                    val isSelected = selectedImages.contains(image)

                    ImageItem(
                        image = image,
                        isEnabled = enabled,
                        onLongClick = {
                            if (!isSelected){
                                selectedImages.add(image)
                            }
                            isEnabled = !isEnabled
                            selectedChange(isEnabled)
                            selectedImagesChange(selectedImages)
                        },
                        onCheckedChange = { isChecked ->
                            if (isChecked){
                                selectedImages.add(image)
                            } else {
                                selectedImages.remove(image)
                                if(selectedImages.size == 0){
                                    selectedChange(isEnabled)
                                }
                            }
                            selectedAllChecked = selectedImages.size == images.size
                            selectedImagesChange(selectedImages)

                            Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                        },
                        onClick = {
                            if(enabled){

                                if (!isSelected){
                                    selectedImages.add(image)
                                } else {
                                    selectedImages.remove(image)
                                    if(selectedImages.size == 0){
                                        selectedChange(isEnabled)
                                    }
                                }
                                selectedAllChecked = selectedImages.size == images.size
                                selectedImagesChange(selectedImages)

                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                            } else{
                                onClick(it)
                            }
                        },
                        selectedItem = selectedImages.contains(image)
                    )
                }
            }
        }
    }
}

@Composable
fun  PhotoImageList(
    images: List<Image>,
    onClick: (Image) -> Unit,
    selected: Boolean,
    selectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    selectedImagesChange: (MutableList<Image>) -> Unit,
    cancelAction: Boolean
){


    val selectedImages = remember {
        mutableStateListOf<Image>()
    }

    var isEnabled by remember {
        mutableStateOf(enabled)
    }

    var selectedAllChecked by remember {
        mutableStateOf(false)
    }

    var showDeleteTitle by remember {
        mutableStateOf(selected)
    }


    LaunchedEffect(cancelAction){
        selectedImages.clear()
        selectedAllChecked = selectedImages.size == images.size
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 3),
        modifier = Modifier
            .fillMaxWidth()
            .height(700.dp),
        contentPadding = PaddingValues(top = 40.dp)
    ){


        items(images){ image ->
            val isSelected = selectedImages.contains(image)
            ImageItem(
                image = image,
                onClick = {
                    if(enabled){

                        if (!isSelected){
                            selectedImages.add(image)
                        } else {
                            selectedImages.remove(image)
                            if(selectedImages.size == 0){
                                selectedChange(isEnabled)
                            }
                        }
                        selectedAllChecked = selectedImages.size == images.size
                        selectedImagesChange(selectedImages)

                        Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                    } else{
                        onClick(it)
                    }
                },
                isEnabled = enabled,
                onCheckedChange = {isChecked ->
                    if (isChecked){
                    selectedImages.add(image)
                } else {
                    selectedImages.remove(image)
                    if(selectedImages.size == 0){
                        selectedChange(isEnabled)
                    }
                }
                    selectedAllChecked = selectedImages.size == images.size
                    selectedImagesChange(selectedImages)

                    Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                },
                onLongClick = {
                    if (!isSelected){
                        selectedImages.add(image)
                    }
                    isEnabled = !isEnabled
                    selectedChange(isEnabled)
                    selectedImagesChange(selectedImages)
                },
                selectedItem = selectedImages.contains(image)
                )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageItem(
    image: Image,
    onClick: (Image) -> Unit,
    selectedItem: Boolean,
    onLongClick: () -> Unit,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
){

    var selected by remember {
        mutableStateOf(selectedItem)
    }

    val padding = if(selected) 4.dp else 6.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(image)
            }
            .padding(padding)
            .combinedClickable(
                onLongClick = {
                    onLongClick()
                },
                onClick = { onClick(image) }
            )
    ) {
       Box(
           modifier = Modifier
               .fillMaxSize()
       ) {
           AsyncImage(
               model = image.uri,
               contentDescription = null,
               modifier = Modifier
                   .fillMaxWidth()
                   .height(120.dp),
               contentScale = ContentScale.Crop
           )
           if (isEnabled){
               Checkbox(
                   checked = selectedItem,
                   onCheckedChange = {
                       onCheckedChange(it)
                       selected = it
                   },
                   modifier = Modifier
                       .padding(8.dp)
                       .align(Alignment.TopEnd)
               )
           }


       }
    }
}

fun shareMultipleImages(
    context: Context,
    imageUris: List<Uri>
){
    val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "image/*"
        putParcelableArrayListExtra(
            Intent.EXTRA_STREAM,
            ArrayList<Parcelable>(imageUris)
        )
    }
    context.startActivity(Intent.createChooser(shareIntent,"Share Images"))
}

