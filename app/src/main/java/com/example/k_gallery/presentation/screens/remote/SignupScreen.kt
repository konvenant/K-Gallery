package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.UserSharedPrefManager
import com.example.k_gallery.presentation.viewmodel.AuthViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState",
    "SuspiciousIndentation"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    navController: NavController
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    authViewModel.message.value = null
    Scaffold (
        modifier = Modifier.fillMaxSize()
    ) {
        var email by remember {
            mutableStateOf("")
        }

        var password by remember {
            mutableStateOf("")
        }

        var confirmPassword by remember {
            mutableStateOf("")
        }


        var showError by remember {
            mutableStateOf(false)
        }

        val isButtonEnabled by derivedStateOf {
            email.isNotEmpty() && password.isNotEmpty()
        }

        val passwordMatch by derivedStateOf {
            confirmPassword ==  password
        }

        val requiredPasswordLength by derivedStateOf {
            password.length >= 4
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
                modifier = Modifier.fillMaxSize(0.2f),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "SignUp",
                modifier = Modifier.padding(8.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
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
                    focusManager.moveFocus(FocusDirection.Down)
                }),

                placeholder = {
                    Text(text = "Email")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = color,
                    unfocusedBorderColor = color
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    showError = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Password, contentDescription = null)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
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



            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Password, contentDescription = null)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus(true)
                }),
                placeholder = {
                    Text(text = "Confirm Password")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = color,
                    unfocusedBorderColor = color
                ),
                singleLine = true
            )

            if (!passwordMatch){
                Text(
                    text = "Passwords don't match",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

           if(showError){
               if (!requiredPasswordLength){
                   Text(
                       text = "Password should be 4 digit or more",
                       color = Color.Red,
                       modifier = Modifier.padding(8.dp)
                   )
               }
           }

            Button(
                onClick = {
                    authViewModel.message.value = null
                    authViewModel.signUp(email, password)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                enabled = isButtonEnabled && passwordMatch && requiredPasswordLength,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Red
                )
            ) {
                Text(text = "Sign Up",  style = TextStyle.Default.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                ))
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Already have an account?,",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(NavHelper.LoginScreen.route) {
                                popUpTo(NavHelper.SignupScreen.route) {
                                    inclusive = true
                                }
                            }
                        }
                )
                Text(
                    text = "Login!!",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(NavHelper.LoginScreen.route) {
                                popUpTo(NavHelper.SignupScreen.route) {
                                    inclusive = true
                                }
                            }
                        },
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

        }

        HandleSignup(authViewModel,email, password,navController,userPrefManager)
    }
}

@Composable
fun HandleSignup(
    authViewModel: AuthViewModel,
    email: String,
    password: String,
    navController: NavController,
    userSharedPrefManager: UserSharedPrefManager
) {
    var showDialog by remember {
        mutableStateOf(true)
    }

    val state by authViewModel.message.observeAsState()

    when(state){
        is Resource.Success -> {
            val success = (state as Resource.Success<Message>).data?.message.toString()
            val showSnackbar by remember {
                mutableStateOf(true)
            }
            val snackbarHostState = remember {
                SnackbarHostState()
            }
            showDialog = false

            LaunchedEffect(showSnackbar){
                snackbarHostState.showSnackbar(
                    message = success,
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
            }

            navController.navigate(NavHelper.VerifyEmailScreen.route+"/${email}"){
                popUpTo(NavHelper.SignupScreen.route){
                    inclusive = true
                }
            }

            saveUserPref(email,password, userSharedPrefManager)

        }
        is Resource.Loading -> {
            LoadingDialog()
        }

        is Resource.Error -> {
            showDialog = false
            val error = (state as Resource.Error<Message>).message
            var showDialog2 by remember {
                mutableStateOf(true)
            }
            if (showDialog2){
                ShowAuthAlertDialog(
                    title = "Error",
                    desc = error.toString(),
                    positive = {
                        authViewModel.message.value = null
                        authViewModel.signUp(email,password)
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

