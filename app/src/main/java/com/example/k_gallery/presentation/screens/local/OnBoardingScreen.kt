package com.example.k_gallery.presentation.screens.local

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.UserSharedPrefManager
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardScreen(
    navController: NavController
){

    val context = LocalContext.current

    val userPrefManager = remember {
        UserSharedPrefManager(context)
    }

    val items = listOf<OnBoardingItem>(
        OnBoardingItem(
            text = "K-Gallery App, closer to perfection",
            image = R.drawable.onboarding_3,
            isButton = false
        ),
        OnBoardingItem(
            text = "Your Gallery Arranged in the best Collection",
            image = R.drawable.onboarding_5,
            isButton = false
        ),
        OnBoardingItem(
            text = "Local and Online Gallery in one place",
            image = R.drawable.onboarding_6,
            isButton = true
        ),

    )
    Box(modifier = Modifier.fillMaxSize()){
        HorizontalPager (
            pageCount = items.size
                ) {index ->
            Image(
                painter = painterResource(id = items[index].image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Milk)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = items[index].text, color = Blue)
                Spacer(modifier = Modifier.height(16.dp))
                if (!items[index].isButton){
                    Text(text = "Scroll Left", color = Red, fontSize = 13.sp)
                }

                if (items[index].isButton){
                    Button(
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                        userPrefManager.setFirstTime(false)
                        navController.navigate(NavHelper.HomeScreen.route){
                            popUpTo(NavHelper.SplashScreen.route){
                                inclusive = true
                            }
                        }
                    }) {
                        Text(text = "Start")
                    }
                }
            }
        }
    }
}

data class OnBoardingItem(
    val text: String,
    val image: Int,
    val isButton: Boolean
)
