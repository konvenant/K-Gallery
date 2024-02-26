package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.UserSharedPrefManager
import com.example.k_gallery.presentation.viewmodel.AuthViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassword(
    navController: NavController
) {

    val authViewModel: AuthViewModel = hiltViewModel()
    authViewModel.userDetails.value = null

    Scaffold (
        modifier = Modifier.fillMaxSize()
    ){

        var email by remember {
            mutableStateOf("")
        }

        var showError by remember {
            mutableStateOf(false)
        }



        val isButtonEnabled by derivedStateOf {
            email.isNotEmpty()
        }



        val color = if (isButtonEnabled) Blue else Color.Red

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Milk),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {


            Image(
                painter = painterResource(id = R.drawable.full_logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(120.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Reset Password")
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    authViewModel.forgotPassword(email)
                    authViewModel.userDetails.value = null
                }),

                placeholder = {
                    Text(text = "Email address")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = color,
                    unfocusedBorderColor = color
                )
            )


            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    authViewModel.forgotPassword(email)
                    authViewModel.userDetails.value = null
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Red
                )
            ) {
                Text(
                    text = "Continue", style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "I remember my password?,",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { navController.navigate(NavHelper.LoginScreen.route){
                            popUpTo(NavHelper.ForgotPasswordScreen.route){
                                inclusive = true
                            }
                        } }
                )
                Text(
                    text = "Log in!!",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { navController.navigate(NavHelper.LoginScreen.route){
                            popUpTo(NavHelper.ForgotPasswordScreen.route){
                                inclusive = true
                            }
                        } },
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

        }
        PerformForgotPassword(authViewModel, email, navController)
    }
}
@Composable
fun PerformForgotPassword(
    authViewModel: AuthViewModel,
    email: String,
    navController: NavController
) {
    var showDialog by remember {
        mutableStateOf(true)
    }

    val state by authViewModel.userDetails.observeAsState()

    when (state) {
        is Resource.Success -> {
            authViewModel.userDetails.value = null
            navController.navigate(NavHelper.VerifyForgotPasswordTokenScreen.route+"/${email}")
            showDialog = false
//            ShowAuthAlertDialog(
//                title = "Success",
//                desc = "Mail Sent to the registered email",
//                positive = {  },
//                negative = {  },
//                positiveText = "",
//                negativeText = "close",
//                icon = Icons.Default.CheckCircleOutline
//            )

        }

        is Resource.Loading -> {
            LoadingDialog()
        }

        is Resource.Error -> {
            showDialog = false
            val error = (state as Resource.Error<User>).message
            var showDialog2 by remember {
                mutableStateOf(true)
            }
            if (showDialog2) {
                ShowAuthAlertDialog(
                    title = "Error",
                    desc = error.toString(),
                    positive = {
                        authViewModel.userDetails.value = null
                        authViewModel.forgotPassword(email)
                    },
                    negative = { showDialog2 = !showDialog2 },
                    positiveText = "try again",
                    negativeText = "close",
                    icon = Icons.Default.Error
                )
            }

        }

        else -> {

        }
    }
}


