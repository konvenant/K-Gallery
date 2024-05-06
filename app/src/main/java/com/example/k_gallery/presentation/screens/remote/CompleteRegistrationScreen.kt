package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.UserSharedPrefManager
import com.example.k_gallery.presentation.viewmodel.AuthViewModel
import com.example.k_gallery.presentation.viewmodel.SharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteRegistrationScreen(
    navController: NavController,
    email : String,
    sharedViewModel: UserSharedViewModel
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    authViewModel.userDetails.value = null


    val context = LocalContext.current

    val userPrefManager = remember {
        UserSharedPrefManager(context)
    }

    Scaffold (
        modifier = Modifier.fillMaxSize()
    ) {
        var country by remember {
            mutableStateOf("")
        }

        var city by remember {
            mutableStateOf("")
        }
        var name by remember {
            mutableStateOf("")
        }
        var phone by remember {
            mutableStateOf("")
        }

        val isButtonEnabled by derivedStateOf {
            country.isNotEmpty() && city.isNotEmpty() && phone.isNotEmpty() && name.isNotEmpty()
        }

        val context = LocalContext.current

        val userPrefManager = remember {
            UserSharedPrefManager(context)
        }

        val focusManager = LocalFocusManager.current

        val color = if (isButtonEnabled) Blue else Color.Red
        Column(
            modifier = Modifier
                .background(Milk)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.full_logo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.1f),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Complete Registration",
                modifier = Modifier.padding(8.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),

                placeholder = {
                    Text(text = "Name")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = color,
                    unfocusedBorderColor = color
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                placeholder = {
                    Text(text = "Phone Number")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = color,
                    unfocusedBorderColor = color
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = city,
                onValueChange = {
                    city = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.LocationCity, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                placeholder = {
                    Text(text = "City")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = color,
                    unfocusedBorderColor = color
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = country,
                onValueChange = {
                    country = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Flag, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus(true)
                }),
                placeholder = {
                    Text(text = "Country")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = color,
                    unfocusedBorderColor = color
                ),
                singleLine = true
            )

            Button(
                onClick = {
                    authViewModel.userDetails.value = null
                    authViewModel.completeRegistration(email,city, country, name, phone)
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
                Text(text = "Complete",  style = TextStyle.Default.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                ))
            }

            Spacer(modifier = Modifier.height(42.dp))

            Text(
                text = "Forfeit",
                color = Color.Red,
                modifier = Modifier.clickable {
                    logoutUser(userPrefManager,navController)
                }
            )
        }



        HandleCompleteRegistration(
            authViewModel,
            email,
            name,
            phone,
            city,
            country,
            sharedViewModel,
            navController
        )

    }


}

@Composable
fun HandleCompleteRegistration(
    authViewModel: AuthViewModel,
    email: String,
    name: String,
    phone: String,
    city: String,
    country: String,
    sharedViewModel: UserSharedViewModel,
    navController: NavController
){
    val state by authViewModel.userDetails.observeAsState()
    var showDialog by remember {
        mutableStateOf(true)
    }
    when(state){
        is Resource.Success -> {
            val user = (state as Resource.Success<User>).data
            showDialog = false
//            ShowAuthAlertDialog(
//                title = "Success",
//                desc = "Successfully Logged In",
//                positive = {  },
//                negative = {  },
//                positiveText = "",
//                negativeText = "close",
//                icon = Icons.Default.CheckCircleOutline
//            )

            sharedViewModel.userData.postValue(user)
            navController.navigate(NavHelper.HomeDashBoardScreenScreen.route){
                popUpTo(NavHelper.CompleteRegistrationScreen.route) {
                    inclusive = true
                }
            }
            authViewModel.userDetails.value = null
        }
        is Resource.Loading -> {
            showDialog = true
          if (showDialog){
              LoadingDialog()
          }
        }

        is Resource.Error -> {
            showDialog = false
            val error = (state as Resource.Error<User>).message
            var showDialog2 by remember {
                mutableStateOf(true)
            }
            if (showDialog2){
                ShowAuthAlertDialog(
                    title = "Error",
                    desc = error.toString(),
                    positive = {
                        authViewModel.userDetails.value = null
                        authViewModel.completeRegistration(email, city, country, name, phone)
                    },
                    negative = { showDialog2 = !showDialog2 },
                    positiveText = "try again" ,
                    negativeText = "close",
                    icon = Icons.Default.Error
                )
            }

        }

        else -> {

        }
    }
}

