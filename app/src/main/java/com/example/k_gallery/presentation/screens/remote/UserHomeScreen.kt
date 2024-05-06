package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.FavoriteImages
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideos
import com.example.k_gallery.data.dataSources.api.models.Image
import com.example.k_gallery.data.dataSources.api.models.ImageX
import com.example.k_gallery.data.dataSources.api.models.ImageXX
import com.example.k_gallery.data.dataSources.api.models.SavedImages
import com.example.k_gallery.data.dataSources.api.models.SavedVideos
import com.example.k_gallery.data.dataSources.api.models.SentImages
import com.example.k_gallery.data.dataSources.api.models.SentVideos
import com.example.k_gallery.data.dataSources.api.models.Video
import com.example.k_gallery.data.dataSources.api.models.VideoX
import com.example.k_gallery.data.dataSources.api.models.VideoXX
import com.example.k_gallery.data.dataSources.local.Folder
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun UserHomeScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    email: String,
    anotherNavController: NavController
) {
    LaunchedEffect(Unit){
        userViewModel.getSavedImages(email)
        userViewModel.getSavedVideos(email)
        userViewModel.getSentImages(email)
        userViewModel.getSentVideos(email)
        userViewModel.getReceivedImages(email)
        userViewModel.getReceivedVideos(email)
        userViewModel.getFavoriteImages(email)
        userViewModel.getFavoriteVideos(email)
    }

    val savedImagesList by userViewModel.savedImages.observeAsState()
    val savedVideosList by userViewModel.savedVideos.observeAsState()
    val sentImagesList by userViewModel.sentImages.observeAsState()
    val sentVideosList by userViewModel.sentVideos.observeAsState()
    val receivedImageList by userViewModel.receivedImages.observeAsState()
    val receivedVideoList by userViewModel.receivedVideos.observeAsState()
    val favoriteImageList by userViewModel.favoriteImages.observeAsState()
    val favoriteVideoList by userViewModel.favoriteVideos.observeAsState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 52.dp,
                    start = 2.dp,
                    end = 2.dp,
                    bottom = 52.dp
                )
            ){
                item {
                    when(savedImagesList){
                        is Resource.Success-> {
                            val savedImages = (savedImagesList as Resource.Success<SavedImages>).data?.images
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)) {
                                if (!savedImages.isNullOrEmpty()){
                                    Text(text = "Saved Images")
                                }
                                LazyRow(modifier = Modifier.padding(4.dp,16.dp)){
                                    if (!savedImages.isNullOrEmpty()) {
                                        items(savedImages.take(3)){ image ->
                                            RecentImageItem(image = image, onItemClick = { img ->
                                                val  encodedUrl = Uri.encode(img.imageUrl)
                                                anotherNavController.navigate(NavHelper.ViewOneImageScreen.route+"/${encodedUrl}"+"/${img.caption}"+"/${img._id}"+"/${img.date}")
                                            })
                                        }
                                        item {

                                               IconButton(onClick = {
                                                   navController.navigate(NavHelper.SavedMediaScreen.route){
                                                       popUpTo(navController.graph.findStartDestination().id)
                                                       launchSingleTop = true

                                                   }
                                               }, modifier = Modifier.padding(top = 42.dp)) {
                                                   Icon(
                                                       imageVector = Icons.Default.ArrowForwardIos,
                                                       contentDescription = null
                                                   )
                                               }


                                        }
                                    }
                                }
                            }
                        }
                        is Resource.Loading -> {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){

                                    ShimmerLoading(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .padding(8.dp))

                            }


                        }
                        is Resource.Error -> {
                            val errorMessage = (savedImagesList as Resource.Error<SavedImages>).message
                            val error =   "Error: $errorMessage"
                            val context = LocalContext.current
                           Toast.makeText(context,error,Toast.LENGTH_SHORT).show()

                        }

                        else -> {}
                    }



                }

                item {
                    when(savedVideosList){
                        is Resource.Success-> {
                            val savedVideos = (savedVideosList as Resource.Success<SavedVideos>).data?.videos
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {
                                if (!savedVideos.isNullOrEmpty() ){
                                    Text(text = "Saved Videos")
                                }
                                LazyRow(modifier = Modifier.padding(16.dp)){
                                    if (!savedVideos.isNullOrEmpty()) {
                                        items(savedVideos.take(3)){ video ->
                                            RecentVideoItem(video, onItemClick = { vid ->
                                                val  encodedUrl = Uri.encode(vid.videoUrl)
                                                anotherNavController.navigate(NavHelper.ViewOneVideoScreen.route+"/${encodedUrl}"+"/${vid.email}"+"/${vid._id}"+"/${vid.date}")
                                            })
                                        }
                                        item {
                                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                IconButton(onClick = {
                                                    navController.navigate(NavHelper.SavedMediaScreen.route){
                                                        popUpTo(navController.graph.findStartDestination().id)
                                                        launchSingleTop = true

                                                    }
                                                },modifier = Modifier.padding(top = 42.dp))  {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowForwardIos,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        is Resource.Loading -> {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ){
                                    ShimmerLoading(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .padding(8.dp))

                            }

                        }
                        is Resource.Error -> {
                            val errorMessage = (savedVideosList as Resource.Error<SavedVideos>).message
                            val error =   "Error: $errorMessage"
                            val context = LocalContext.current
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show()

                        }

                        else -> {}
                    }



                }

                item {
                    when(sentImagesList){
                        is Resource.Success-> {
                            val sentImages = (sentImagesList as Resource.Success<SentImages>).data?.images
                            val filteredImages= sentImages?.filter { images ->
                                !images.isSenderDeleted
                            }
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {
                                if (!filteredImages.isNullOrEmpty()){
                                    Text(text = "Sent Images")
                                }

                                LazyRow(modifier = Modifier.padding(16.dp)){
                                    if (!filteredImages.isNullOrEmpty()) {
                                        items(filteredImages.take(3)){ image ->

                                            val desc = "Sent to : ${image.toEmail}"
                                            RecentSentImageItem(image = image, desc = desc,onItemClick = {img ->
                                                val  encodedUrl = Uri.encode(img.imageUrl)
                                                anotherNavController.navigate(NavHelper.ViewOneImageScreen.route+"/${encodedUrl}"+"/${desc}"+"/${img._id}"+"/${img.date}")
                                            })
                                        }
                                        item {
                                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                IconButton(onClick = {
                                                    navController.navigate(NavHelper.SendMediaScreen.route){
                                                        popUpTo(navController.graph.findStartDestination().id)
                                                        launchSingleTop = true

                                                    }
                                                }, modifier = Modifier.padding(top = 42.dp) ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowForwardIos,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        is Resource.Loading -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){

                                    ShimmerLoading(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .padding(8.dp))

                            }
                        }
                        is Resource.Error -> {
                            val errorMessage = (sentImagesList as Resource.Error<SentImages>).message
                            val error =   "Error: $errorMessage"
                            val context = LocalContext.current
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show()

                        }

                        else -> {}
                    }
                }

                item {
                    when(sentVideosList){
                        is Resource.Success-> {
                            val sentVideos = (sentVideosList as Resource.Success<SentVideos>).data?.videos

                            val filteredVideos = sentVideos?.filter { video ->
                                !video.isSenderDeleted
                            }
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {
                                if (!filteredVideos.isNullOrEmpty()){
                                    Text(text = "Sent Videos")
                                }
                                LazyRow(modifier = Modifier.padding(16.dp)){
                                    if (!filteredVideos.isNullOrEmpty()) {
                                        items(filteredVideos.take(3)){ video ->
                                            val desc = "Sent to : ${video.toEmail}"
                                            RecentSentVideoItem(video,desc,onItemClick = { vid ->
                                                val  encodedUrl = Uri.encode(vid.videoUrl)
                                                anotherNavController.navigate(NavHelper.ViewOneVideoScreen.route+"/${encodedUrl}"+"/${desc}"+"/${vid._id}"+"/${vid.date}")
                                            })
                                        }
                                        item {
                                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                IconButton(onClick = {
                                                    navController.navigate(NavHelper.SendMediaScreen.route){
                                                        popUpTo(navController.graph.findStartDestination().id)
                                                        launchSingleTop = true

                                                    }
                                                }, modifier = Modifier.padding(top = 42.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowForwardIos,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        is Resource.Loading -> {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){

                                    ShimmerLoading(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .padding(8.dp))

                            }

                        }
                        is Resource.Error -> {
                            val errorMessage = (sentVideosList as Resource.Error<SentVideos>).message
                            val error =   "Error: $errorMessage"
                            val context = LocalContext.current
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show()

                        }

                        else -> {}
                    }



                }

                item {
                    when(receivedImageList){
                        is Resource.Success-> {
                            val receivedImages = (receivedImageList as Resource.Success<SentImages>).data?.images

                            val filteredImages= receivedImages?.filter { images ->
                                !images.isRecieverDeleted
                            }
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {
                                if (!filteredImages.isNullOrEmpty()){
                                    Text(text = "Received Images")
                                }
                                LazyRow(modifier = Modifier.padding(16.dp)){
                                    if (!filteredImages.isNullOrEmpty()) {
                                        items(filteredImages.take(3)){ image ->
                                            val desc = "Sent by : ${image.fromEmail}"
                                            RecentSentImageItem(image = image, desc = desc,onItemClick = { img ->
                                                val  encodedUrl = Uri.encode(img.imageUrl)
                                                anotherNavController.navigate(NavHelper.ViewOneImageScreen.route+"/${encodedUrl}"+"/${desc}"+"/${img._id}"+"/${img.date}")
                                            })
                                        }
                                        item {
                                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                IconButton(onClick = {
                                                    navController.navigate(NavHelper.SendMediaScreen.route){
                                                        popUpTo(navController.graph.findStartDestination().id)
                                                        launchSingleTop = true

                                                    }
                                                }, modifier = Modifier.padding(top = 42.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowForwardIos,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        is Resource.Loading -> {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){

                                    ShimmerLoading(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .padding(8.dp))


                            }
                        }
                        is Resource.Error -> {
                            val errorMessage = (receivedImageList as Resource.Error<SentImages>).message
                            val error =   "Error: $errorMessage"
                            val context = LocalContext.current
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show()

                        }

                        else -> {}
                    }
                }

                item {
                    when(receivedVideoList){
                        is Resource.Success-> {
                            val receivedVideos = (receivedVideoList as Resource.Success<SentVideos>).data?.videos

                            val filteredVideos = receivedVideos?.filter { video ->
                                !video.isRecieverDeleted
                            }

                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {
                                if (!filteredVideos.isNullOrEmpty()){
                                    Text(text = "Received Videos")
                                }
                                LazyRow(modifier = Modifier.padding(16.dp)){
                                    if (!filteredVideos.isNullOrEmpty()) {
                                        items(filteredVideos.take(3)){ video ->
                                            val desc = "Sent from : ${video.toEmail}"
                                            RecentSentVideoItem(video, desc = desc,onItemClick = {vid ->
                                                val  encodedUrl = Uri.encode(vid.videoUrl)
                                                anotherNavController.navigate(NavHelper.ViewOneVideoScreen.route+"/${encodedUrl}"+"/${desc}"+"/${vid._id}"+"/${vid.date}")
                                            })
                                        }
                                        item {
                                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                IconButton(onClick = {
                                                    navController.navigate(NavHelper.SendMediaScreen.route){
                                                        popUpTo(navController.graph.findStartDestination().id)
                                                        launchSingleTop = true

                                                    }
                                                }, modifier = Modifier.padding(top = 42.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowForwardIos,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        is Resource.Loading -> {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){

                                    ShimmerLoading(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .padding(8.dp))

                            }

                        }
                        is Resource.Error -> {
                            val errorMessage = (receivedVideoList as Resource.Error<SentVideos>).message
                            val error =   "Error: $errorMessage"
                            val context = LocalContext.current
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show()

                        }

                        else -> {}
                    }



                }

                item {
                    when(favoriteImageList){
                        is Resource.Success-> {
                            val favoriteImages = (favoriteImageList as Resource.Success<FavoriteImages>).data?.images
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {
                                if (!favoriteImages.isNullOrEmpty()){
                                    Text(text = "Favorite Images")
                                }
                                LazyRow(modifier = Modifier.padding(16.dp)){
                                    if (!favoriteImages.isNullOrEmpty()) {
                                        items(favoriteImages.take(3)){ image ->
                                            RecentFavoriteImageItem(image = image,onItemClick = {img ->
                                                val  encodedUrl = Uri.encode(img.imageUrl)
                                                anotherNavController.navigate(NavHelper.ViewOneImageScreen.route+"/${encodedUrl}"+"/${img.email}"+"/${img._id}"+"/${img.date}")
                                            })
                                        }
                                        item {
                                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                IconButton(onClick = {
                                                    navController.navigate(NavHelper.OnlineFavoriteScreen.route){
                                                        popUpTo(navController.graph.findStartDestination().id)
                                                        launchSingleTop = true

                                                    }
                                                }, modifier = Modifier.padding(top = 42.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowForwardIos,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        is Resource.Loading -> {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){

                                    ShimmerLoading(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .padding(8.dp))

                            }

                        }
                        is Resource.Error -> {
                            val errorMessage = (favoriteImageList as Resource.Error<FavoriteImages>).message
                            val error =   "Error: $errorMessage"
                            val context = LocalContext.current
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show()

                        }

                        else -> {}
                    }
                }

                item {
                    when(favoriteVideoList){
                        is Resource.Success-> {
                            val favoriteVideos = (favoriteVideoList as Resource.Success<FavoriteVideos>).data?.videos
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {
                                if (!favoriteVideos.isNullOrEmpty()){
                                    Text(text = "Favorite Videos")
                                }
                                LazyRow(modifier = Modifier.padding(16.dp)){
                                    if (!favoriteVideos.isNullOrEmpty()) {
                                        items(favoriteVideos.take(3)){ video ->
                                            RecentFavoriteVideoItem(video = video, onItemClick = { vid ->
                                                val  encodedUrl = Uri.encode(vid.videoUrl)
                                                anotherNavController.navigate(NavHelper.ViewOneVideoScreen.route+"/${encodedUrl}"+"/${vid.email}"+"/${vid._id}"+"/${vid.date}")
                                            })
                                        }
                                        item {
                                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                IconButton(onClick = {
                                                    navController.navigate(NavHelper.OnlineFavoriteScreen.route){
                                                        popUpTo(navController.graph.findStartDestination().id)
                                                        launchSingleTop = true

                                                    }
                                                }, modifier = Modifier.padding(top = 42.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowForwardIos,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        is Resource.Loading -> {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){

                                    ShimmerLoading(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .padding(8.dp))

                            }

                        }
                        is Resource.Error -> {
                            val errorMessage = (favoriteVideoList as Resource.Error<FavoriteVideos>).message
                            val error =   "Error: $errorMessage"
                            val context = LocalContext.current
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show()

                        }

                        else -> {}
                    }
                }

            }
}



@Composable
fun RecentImageItem(
    image: Image,
    onItemClick: (Image) -> Unit
){
    Column (
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .clickable { onItemClick(image) }
            .padding(8.dp)
    ){
        AsyncImage(
            model = image.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.full_logo),
            error = painterResource(id = R.drawable.logo)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = image.caption,
            style = TextStyle.Default.copy(fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if(image.date.length > 22) image.date.slice(0..22) + "..." else image.date,
                style = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )

        }

    }
}


@Composable
fun RecentVideoItem(
    video: Video,
    onItemClick: (Video) -> Unit
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
        imageLoader = imageLoader,
        error = painterResource(id = R.drawable.full_logo),
        placeholder = painterResource(id = R.drawable.full_logo)
    )

    Column (
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .clickable { onItemClick(video) }
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
    ){

        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { onItemClick(video) },
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

        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if(video.date.length > 22) video.date.slice(0..22) + "..." else video.date,
                style = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )

        }

    }
}

@Composable
fun RecentSentImageItem(
    image: ImageX,
    desc: String,
    onItemClick: (ImageX) -> Unit
){
    Column (
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .clickable { onItemClick(image) }
            .padding(8.dp)
    ){
        AsyncImage(
            model = image.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.full_logo),
            error = painterResource(id = R.drawable.logo)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if(desc.length > 20) desc.slice(0..20) + "..." else desc,
            style = TextStyle.Default.copy(fontSize = 14.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if(image.date.length > 22) image.date.slice(0..22) + "..." else image.date,
                style = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )

        }

    }
}

@Composable
fun RecentSentVideoItem(
    video: VideoX,
    desc: String,
    onItemClick: (VideoX) -> Unit
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
        imageLoader = imageLoader,
        error = painterResource(id = R.drawable.full_logo),
        placeholder = painterResource(id = R.drawable.full_logo)
    )

    Column (
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .clickable { onItemClick(video) }
            .padding(8.dp)
    ){

        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { onItemClick(video) },
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Transparent)
                    .align(Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if(desc.length > 20) desc.slice(0..20) + "..." else desc,
            style = TextStyle.Default.copy(fontSize = 14.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if(video.date.length > 22) video.date.slice(0..22) + "..." else video.date,
                style = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )

        }

    }
}

@Composable
fun RecentFavoriteImageItem(
    image: ImageXX,
    onItemClick: (ImageXX) -> Unit
){
    Column (
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .clickable { onItemClick(image) }
            .padding(8.dp)
    ){
        AsyncImage(
            model = image.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.full_logo),
            error = painterResource(id = R.drawable.logo)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if(image.date.length > 22) image.date.slice(0..22) + "..." else image.date,
                style = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )

        }

    }
}

@Composable
fun RecentFavoriteVideoItem(
    video: VideoXX,
    onItemClick: (VideoXX) -> Unit
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
        imageLoader = imageLoader,
        error = painterResource(id = R.drawable.full_logo),
        placeholder = painterResource(id = R.drawable.full_logo)
    )

    Column (
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .clickable { onItemClick(video) }
            .padding(8.dp)
    ){

        Box(
            modifier = Modifier.fillMaxWidth(0.5f)
        ){
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { onItemClick(video) },
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

        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if(video.date.length > 22) video.date.slice(0..22) + "..." else video.date,
                style = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )

        }

    }
}




@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier
){
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxHeight(0.2f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect()
        )
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat() ,
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        )
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5)
            ),
            start = Offset(startOffsetX,0f),
            end = Offset(startOffsetX + size.width.toFloat(),size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}








