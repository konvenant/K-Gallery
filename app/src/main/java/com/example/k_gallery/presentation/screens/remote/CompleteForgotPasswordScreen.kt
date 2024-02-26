package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.input.VisualTransformation
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun CompleteForgotPasswordPasswordScreen(
    navController: NavController,
    email: String
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    authViewModel.userDetails.value = null
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    Scaffold (
        modifier = Modifier.fillMaxSize()
    ) {

        var password by remember {
            mutableStateOf("")
        }


        val isButtonEnabled by derivedStateOf {
            password.isNotEmpty()
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
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(id = R.drawable.full_logo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.3f),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Update Password",
                modifier = Modifier.padding(8.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.clearFocus(true)
                }),

                placeholder = {
                    Text(text = "Password")
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
                    authViewModel.completeForgotPassword(email, password)
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
        }



   HandleCompleteForgotPassword(authViewModel, email, password,navController,snackbarHostState, scope, )
    }

}

@Composable
fun HandleCompleteForgotPassword(
    authViewModel: AuthViewModel,
    email: String,
    password:String,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
){
    val state by authViewModel.userDetails.observeAsState()
    var showDialog by remember {
        mutableStateOf(true)
    }


    when(state){
        is Resource.Success -> {
            val user = (state as Resource.Success<User>).data?.user
            showDialog = false

//            ShowAuthAlertDialog(
//                title = "Success",
//                desc = "",
//                positive = {  },
//                negative = {  },
//                positiveText = "",
//                negativeText = "close",
//                icon = Icons.Default.CheckCircleOutline
//            )

            navController.navigate(NavHelper.LoginScreen.route) {
                popUpTo(NavHelper.CompleteForgotPasswordScreen.route){
                    inclusive = true
                }
                launchSingleTop = true
            }

            authViewModel.userDetails.value = null

          LaunchedEffect(Unit){
                  snackbarHostState.showSnackbar("User Password Updated Successfully", withDismissAction = true)
          }
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
            if (showDialog2){
                ShowAuthAlertDialog(
                    title = "Error",
                    desc = error.toString(),
                    positive = {
                        authViewModel.userDetails.value = null
                        authViewModel.completeForgotPassword(email, password)
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

