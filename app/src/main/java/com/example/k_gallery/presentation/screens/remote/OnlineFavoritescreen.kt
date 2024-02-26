package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.example.k_gallery.data.dataSources.api.models.FavoriteImages
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideos
import com.example.k_gallery.data.dataSources.api.models.Image
import com.example.k_gallery.data.dataSources.api.models.ImageXX
import com.example.k_gallery.data.dataSources.api.models.SavedImages
import com.example.k_gallery.data.dataSources.api.models.SavedVideos
import com.example.k_gallery.data.dataSources.api.models.Video
import com.example.k_gallery.data.dataSources.api.models.VideoXX
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Red

@Composable
fun OnlineFavoriteScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    email: String
) {
    val composable1 = "composable1"
    val composable2 = "composable2"
    var currentComposable by remember {
        mutableStateOf(composable1)
    }

    val context = LocalContext.current
    LaunchedEffect(Unit){
        userViewModel.getFavoriteImages(email)
        userViewModel.getFavoriteVideos(email)

    }

    val savedImagesList by userViewModel.favoriteImages.observeAsState()
    val savedVideosList by userViewModel.favoriteVideos.observeAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp,16.dp,0.dp,0.dp)
    ){
        item {
            FavoriteChoiceSelectionBoxes(
                onFirstClick = {
                    currentComposable = composable1
                    Toast.makeText(context,composable1.toString(), Toast.LENGTH_LONG).show()
                },
                onSecondClick = { currentComposable = composable2
                    Toast.makeText(context,composable2.toString(), Toast.LENGTH_LONG).show()},
                firstText = "Favorite Images",
                secondText = "Favorite Videos",
                firstSelectedIcon = Icons.Filled.Image,
                firstUnselectedIcon = Icons.Outlined.Image,
                secondSelectedIcon = Icons.Filled.VideoLibrary,
                secondUnselectedIcon = Icons.Outlined.VideoLibrary
            )
        }
        item {
            Box(Modifier.fillMaxSize()) {
                if (currentComposable == composable1){
                    var selected by remember {
                        mutableStateOf(false)
                    }
                    var cancel by remember {
                        mutableStateOf(false)
                    }

                    val selectedImages = remember {
                        mutableStateListOf<ImageXX>()
                    }


                    when(savedImagesList){
                        is Resource.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        is Resource.Error -> {
                            val errorMessage = (savedImagesList as Resource.Error<FavoriteImages>).message
                            Text(
                                text = "Error: $errorMessage",
                                color = Color.Red,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        is Resource.Success -> {
                            val savedImages = (savedImagesList as Resource.Success<FavoriteImages>).data?.images
                            FloatingActionButton(
                                onClick = { /*TODO*/ },
                                modifier = Modifier.align(
                                    Alignment.BottomEnd
                                )
                            ) {
                                Text(text = "Save Image")
                            }
                            FavoriteImageList(
                                images = savedImages!!,
                                onClick = {},
                                selected = selected,
                                selectedChange = {selected = !selected} ,
                                enabled = selected,
                                selectedImagesChange = {images ->
                                    selectedImages.clear()
                                    selectedImages.addAll(images)},
                                cancelAction = cancel
                            )
                        }

                        else -> {}
                    }



                } else if(currentComposable == composable2){


                    var selected by remember {
                        mutableStateOf(false)
                    }
                    var cancel by remember {
                        mutableStateOf(false)
                    }

                    val selectedVideos = remember {
                        mutableStateListOf<VideoXX>()
                    }


                    when(savedVideosList){
                        is Resource.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        is Resource.Error -> {
                            val errorMessage = (savedVideosList as Resource.Error<FavoriteVideos>).message
                            Text(
                                text = "Error: $errorMessage",
                                color = Color.Red,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        is Resource.Success -> {
                            val savedVideos = (savedVideosList as Resource.Success<FavoriteVideos>).data!!.videos
                            FloatingActionButton(
                                onClick = { /*TODO*/ },
                                modifier = Modifier.align(
                                    Alignment.BottomEnd
                                )
                            ) {
                                Text(text = "Save Image")
                            }
                            FavoriteVideosList(
                                videos = savedVideos,
                                onClick = {},
                                selected = selected,
                                selectedChange = {
                                    selected = !selected
                                },
                                enabled = selected,
                                selectedImagesChange = {videos ->
                                    selectedVideos.clear()
                                    selectedVideos.addAll(videos)},
                                cancelAction = cancel
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}


@Composable
fun FavoriteChoiceSelectionBoxes(
    onFirstClick: () -> Unit,
    onSecondClick: () -> Unit,
    firstText:String,
    secondText: String,
    firstSelectedIcon: ImageVector,
    firstUnselectedIcon: ImageVector,
    secondSelectedIcon: ImageVector,
    secondUnselectedIcon: ImageVector
) {
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }

    val selectionItems = listOf(
        TopNavigationItem(
            title = firstText,
            selectedIcon = firstSelectedIcon,
            unSelectedIcon = firstUnselectedIcon,
            action = { onFirstClick() }
        ),
        TopNavigationItem(
            title = secondText,
            selectedIcon = secondSelectedIcon,
            unSelectedIcon = secondUnselectedIcon,
            action = { onSecondClick() }
        )
    )


    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        selectionItems.forEachIndexed { index, item ->
            OutlinedButton(
                onClick = {
                    selectedItem = index
                    item.action()

                },
                border = if(selectedItem == index){
                    BorderStroke(2.dp, Red)
                } else BorderStroke(1.dp, Blue),
                contentPadding = PaddingValues(8.dp)
            ) {
                Icon(
                    imageVector = if (selectedItem == index) item.selectedIcon else item.unSelectedIcon,
                    contentDescription = null
                )
                Text(text = item.title)
            }
        }

    }
}




@SuppressLint("MutableCollectionMutableState")
@Composable
fun FavoriteImageList(
    images: List<ImageXX>,
    onClick: (ImageXX) -> Unit,
    selected: Boolean,
    selectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    selectedImagesChange: (MutableList<ImageXX>) -> Unit,
    cancelAction: Boolean
){
    val selectedImages = remember {
        mutableStateListOf<ImageXX>()
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

    LazyColumn(modifier = Modifier.fillMaxWidth().height(900.dp)){
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

                    FavoriteImageItem(
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteImageItem(
    image: ImageXX,
    onClick: (ImageXX) -> Unit,
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


@Composable
fun FavoriteVideosList(
    videos: List<VideoXX>,
    onClick: (VideoXX) -> Unit,
    selected: Boolean,
    selectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    selectedImagesChange: (MutableList<VideoXX>) -> Unit,
    cancelAction: Boolean
){



    val selectedVideos = remember {
        mutableStateListOf<VideoXX>()
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
        selectedVideos.clear()
        selectedAllChecked = selectedVideos.size == videos.size
    }

    LazyColumn(modifier = Modifier.fillMaxWidth().height(900.dp)){
        item {
            if (enabled){
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedAllChecked = !selectedAllChecked
                            if (selectedAllChecked) {
                                selectedVideos.clear()
                                selectedVideos.addAll(videos)
                                Log.d("selectedImages", "$selectedVideos")
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                            } else {
                                selectedVideos.clear()
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedVideos)
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
                                selectedVideos.clear()
                                selectedVideos.addAll(videos)
                                Log.d("selectedImages", "$selectedVideos")
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                            } else {
                                selectedVideos.clear()
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedVideos)
                        }
                    )
                    Checkbox(
                        checked = selectedAllChecked,
                        onCheckedChange = {
                            selectedAllChecked = it
                            if (it){
                                selectedVideos.clear()
                                selectedVideos.addAll(videos)
                                Log.d("selectedImages","$selectedVideos")
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                            } else{
                                selectedVideos.clear()
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedVideos)
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
                items(videos){ video ->
                    val isSelected = selectedVideos.contains(video)
                    FavoriteVideoItem(
                        video = video,
                        isEnabled = enabled,
                        onLongClick = {
                            if (!isSelected){
                                selectedVideos.add(video)
                            }
                            isEnabled = !isEnabled
                            selectedChange(isEnabled)
                            selectedImagesChange(selectedVideos)
                        },
                        onCheckedChange = { isChecked ->
                            if (isChecked){
                                selectedVideos.add(video)
                            } else {
                                selectedVideos.remove(video)
                                if(selectedVideos.size == 0){
                                    selectedChange(isEnabled)
                                }
                            }
                            selectedAllChecked = selectedVideos.size == videos.size
                            selectedImagesChange(selectedVideos)

                            Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                        },
                        onClick = {
                            if(enabled){

                                if (!isSelected){
                                    selectedVideos.add(video)
                                } else {
                                    selectedVideos.remove(video)
                                    if(selectedVideos.size == 0){
                                        selectedChange(isEnabled)
                                    }
                                }
                                selectedAllChecked = selectedVideos.size == videos.size
                                selectedImagesChange(selectedVideos)

                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                            } else{
                                onClick(it)
                            }
                        },
                        selectedItem = selectedVideos.contains(video)
                    )
                }
            }


        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteVideoItem(
    video: VideoXX,
    onClick: (VideoXX) -> Unit,
    selectedItem: Boolean,
    onLongClick: () -> Unit,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
){

    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(
        model = video.videoUrl,
        imageLoader = imageLoader
    )
    var selected by remember {
        mutableStateOf(selectedItem)
    }

    val padding = if(selected) 4.dp else 6.dp

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(video) }
        .padding(padding)
        .combinedClickable(
            onLongClick = {
                onLongClick()
            },
            onClick = { onClick(video) }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { onClick(video) },
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Transparent)
                    .align(Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = null,
                    tint = Color.White
                )
            }
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

