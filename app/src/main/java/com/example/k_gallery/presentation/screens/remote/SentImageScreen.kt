package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FeaturedVideo
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoChat
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.ChatMessage
import com.example.k_gallery.data.dataSources.api.models.FavoriteImageAddRequest
import com.example.k_gallery.data.dataSources.api.models.Image
import com.example.k_gallery.data.dataSources.api.models.ImageObject
import com.example.k_gallery.data.dataSources.api.models.ImageX
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.SentImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.SentImages
import com.example.k_gallery.presentation.screens.local.shareMultipleImages
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun SentImageScreen(
    navController: NavController,
    email: String,
    userViewModel: UserViewModel,
) {

    LaunchedEffect(Unit){
        userViewModel.getSentImages(email)
    }

    val sentImageList by userViewModel.sentImages.observeAsState()
    val deleteMultipleSentImage by userViewModel.deleteMultipleSentImages.observeAsState()
    val addMultipleSentImageToFavorite by userViewModel.addMultipleFavoriteImages.observeAsState()

    var isLoading by remember {
        mutableStateOf(false)
    }

    var expanded by remember {
        mutableStateOf(false)
    }


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val context = LocalContext.current

    var selected by remember {
        mutableStateOf(false)
    }

    var cancel by remember {
        mutableStateOf(false)
    }

    val selectedImages = remember {
        mutableStateListOf<ImageX>()
    }

    var deletionResult by remember { mutableStateOf(false) }
    var deletionConfirmation by remember { mutableStateOf(false) }

    var favoriteResult by remember { mutableStateOf(false) }
    var favoriteConfirmation by remember { mutableStateOf(false) }

    var selectedImagesUrl : List<String>
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){}


    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if(selected){
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
                            selectedImagesUrl = selectedImages.map { it.imageUrl }
                            if (selectedImagesUrl.isNotEmpty()){
                                downloadAndShareMultipleImages(selectedImagesUrl,context){show,error->
                                    if (show){
                                        Toast.makeText(context,error,Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            favoriteConfirmation = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null
                            )
                        }
                    }
                )
            } else{
                TopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Blue ,
                        titleContentColor = Color.White
                    ),
                    scrollBehavior = scrollBehavior
                    ,
                    title = {
                        Text(text = "Sent Images")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp()}) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {expanded = false},
                            modifier = Modifier.background(Blue)
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = "Sent Videos", color  = Color.White) },
                                onClick = {
                                    navController.navigate(NavHelper.SentVideoScreen.route+"/$email")
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.VideoChat,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(text="Received Images",color = Color.White)},
                                onClick = {
                                    navController.navigate(NavHelper.ReceivedImageScreen.route+"/$email")
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.PictureInPictureAlt,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text="Received Videos",color = Color.White)},
                                onClick = {
                                    navController.navigate(NavHelper.ReceivedVideoScreen.route+"/$email")
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.FeaturedVideo,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            )


                        }
                    }
                )
            }
        },

            ) {


        when(sentImageList){
            is Resource.Loading -> {
                isLoading = true
                Box(modifier = Modifier.fillMaxSize()){
                    if(isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
            is Resource.Success -> {
                isLoading = false
                val sentImages = (sentImageList as Resource.Success<SentImages>).data!!.images

                val filteredImage = sentImages.filter { image ->
                    !image.isSenderDeleted
                }

                SentImageList(
                    images = filteredImage,
                    onClick = {
                        val imageIndex = filteredImage.indexOf(it).toString()
                        navController.navigate(NavHelper.ViewAllSentImageScreen.route + "/$imageIndex" + "/$email")
                    },
                    selected = selected,
                    selectedChange = { selected = !selected },
                    enabled = selected,
                    selectedImagesChange = { images ->
                        selectedImages.clear()
                        selectedImages.addAll(images)
                    },
                    cancelAction = cancel
                )
            }

            is Resource.Error -> {
                isLoading = false
                val errMessage = (sentImageList as Resource.Error<SentImages>).message
                Toast.makeText(context,errMessage, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }



        if (deletionConfirmation){
            com.example.k_gallery.presentation.screens.local.ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedImages.size} Images",
                desc = "Are you sure want to delete these images? ",
                deletedAccepted = {
                    val arrayOfObjects = selectedImages.map {
                        ImageObject(
                            it.fromEmail,
                            it.toEmail,
                            it._id
                        )
                    }
                    val request = SentImageDeleteRequest(
                        email = email,
                        arrayOfObjects = arrayOfObjects
                    )
                    userViewModel.deleteMultipleSentImage(request)
                    deletionConfirmation = false
                    deletionResult = true
                    cancel = !cancel
                    selected = !selected
                }
            ) {
                deletionConfirmation = false
            }
        }

        if (deletionResult) {
            var isLoading2 by remember {
                mutableStateOf(false)
            }
            when (deleteMultipleSentImage) {
                is Resource.Error -> {
                    isLoading2 = false
                    val err = (deleteMultipleSentImage as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    deletionResult = false
                }

                is Resource.Success -> {
                    isLoading2 = false
                    val success = ( deleteMultipleSentImage as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    deletionResult = false
                    userViewModel.getSentImages(email)
                }

                is Resource.Loading -> {
                    isLoading2 = true
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading2) {
                            LoadingDialog()
                        }
                    }
                }

                else -> {

                }

            }
        }


        if (favoriteConfirmation){
            com.example.k_gallery.presentation.screens.local.ShowDeleteConfirmationALertDialog(
                title = "Add ${selectedImages.size} Images to Favorite",
                desc = "Are you sure you want to add these images to Favorite? ",
                deletedAccepted = {
                    val favoriteImages = selectedImages.map {
                        it.imageUrl
                    }
                    val request = FavoriteImageAddRequest(
                        email = email,
                        favoriteImages = favoriteImages
                    )
                    userViewModel.addMultipleFavoriteImage(request)
                    favoriteConfirmation = false
                    favoriteResult = true
                    cancel = !cancel
                    selected = !selected
                }
            ) {
                favoriteConfirmation = false
            }
        }

        if (favoriteResult) {
            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (addMultipleSentImageToFavorite) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (addMultipleSentImageToFavorite as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    favoriteResult = false
                    cancel = !cancel
                    selected = !selected
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( addMultipleSentImageToFavorite as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    favoriteResult = false
                }

                is Resource.Loading -> {
                    isLoading3 = true
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading3) {
                            LoadingDialog()
                        }
                    }
                }

                else -> {

                }

            }
        }


    }
}


@SuppressLint("MutableCollectionMutableState")
@Composable
fun SentImageList(
    images: List<ImageX>,
    onClick: (ImageX) -> Unit,
    selected: Boolean,
    selectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    selectedImagesChange: (MutableList<ImageX>) -> Unit,
    cancelAction: Boolean
){
    val selectedImages = remember {
        mutableStateListOf<ImageX>()
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

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .height(900.dp)){
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
                            } else {
                                selectedImages.clear()
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
                            } else{
                                selectedImages.clear()
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

                items(
                    images,
                    key = {
                        it._id
                    }
                ){ image ->
                    val isSelected = selectedImages.contains(image)

                    SentImageItem(
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


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SentImageItem(
    image: ImageX,
    onClick: (ImageX) -> Unit,
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
                    model = image.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.full_logo),
                    error = painterResource(id = R.drawable.logo)
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
