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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.UserPreferences
import com.example.k_gallery.presentation.util.UserSharedPrefManager
import com.example.k_gallery.presentation.viewmodel.AuthViewModel
import com.example.k_gallery.presentation.viewmodel.SharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red
import java.time.format.TextStyle


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    sharedViewModel: UserSharedViewModel
){
    val authViewModel: AuthViewModel = hiltViewModel()
    authViewModel.userDetails.value = null
    Scaffold (
        modifier = Modifier.fillMaxSize()
            ) {
        var email by remember {
            mutableStateOf("")
        }

        var password by remember {
            mutableStateOf("")
        }

        val showError by remember {
            mutableStateOf(false)
        }

        val isButtonEnabled by derivedStateOf {
            email.isNotEmpty() && password.isNotEmpty()
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
                modifier = Modifier.fillMaxSize(0.3f),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Login",
                modifier = Modifier
                    .padding(8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
                        OutlinedTextField (
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
                   if (isButtonEnabled){
                       authViewModel.login(email, password)
                       authViewModel.userDetails.value = null
                   }
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

            if (showError){
                Text(
                    text = "Fields cannot be empty",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Text(
                text = "Forgot Password?",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(NavHelper.ForgotPasswordScreen.route) }
                    .padding(16.dp, 8.dp),
                textAlign = TextAlign.Right
            )
            Button(
                onClick = {
                    authViewModel.userDetails.value = null
                    authViewModel.login(email, password)
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
                Text(text = "Login",  style = androidx.compose.ui.text.TextStyle.Default.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                ))
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Don't have an account?,",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(NavHelper.SignupScreen.route) {
                                popUpTo(NavHelper.LoginScreen.route) {
                                    inclusive = true
                                }
                            }
                        }
                )
                Text(
                    text = "Signup now!!",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(NavHelper.SignupScreen.route) {
                                popUpTo(NavHelper.LoginScreen.route) {
                                    inclusive = true
                                }
                            }
                        },
                    style = androidx.compose.ui.text.TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }


            HandleLogin(
                authViewModel ,
                email ,
                password  ,
                userPrefManager,
                navController,
                sharedViewModel
            )
        }
    }


}

@Composable
private fun HandleLogin(
    authViewModel: AuthViewModel,
    email: String,
    password: String,
    userSharedPrefManager: UserSharedPrefManager,
    navController: NavController,
    sharedViewModel: UserSharedViewModel
) {
    val state by authViewModel.userDetails.observeAsState()
    var showDialog by remember {
        mutableStateOf(true)
    }
    when(state){
        is Resource.Success -> {
            val success = (state as Resource.Success<User>).data?.user!!
            val user = (state as Resource.Success<User>).data!!
            sharedViewModel.userData.postValue(user)

            showDialog = false

            if (success.isEmailVerified){
                    if (success.country == "" || success.city == ""){
                        navController.navigate(NavHelper.CompleteRegistrationScreen.route+"/${email}")
                        authViewModel.userDetails.value = null
                    } else{
                        navController.navigate(NavHelper.HomeDashBoardScreenScreen.route){
                            popUpTo(NavHelper.LoginScreen.route){
                                inclusive = true
                            }
                        }
                        authViewModel.userDetails.value = null
                    }
            } else{
                authViewModel.sendToken(email)
                navController.navigate(NavHelper.VerifyEmailScreen.route+"/${email}")
                authViewModel.userDetails.value = null
            }

            saveUserPref(email,password,userSharedPrefManager)

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
                        authViewModel.login(email, password)
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

fun saveUserPref(email: String, password: String,userSharedPrefManager: UserSharedPrefManager) {
    val userPref = UserPreferences(email,password,true)
    userSharedPrefManager.setLoggedInPrefs(userPref)
}

@Composable
fun ShowAuthAlertDialog(
    title: String,
    desc: String,
    positive: () -> Unit,
    negative: () -> Unit,
    positiveText: String,
    negativeText: String,
    icon: ImageVector
) {
    AlertDialog(
        onDismissRequest = {  },
        title = {Text(title)},
        text = { Text(desc, textAlign = TextAlign.End)},
        confirmButton = {
            Button(
                onClick = positive
            ) {
                Text(text = positiveText)
            }
        },
        dismissButton = {
            Button(onClick = { negative() }) {
                Text(text = negativeText)
            }
        },
        icon = {
            Icon(imageVector = icon, contentDescription = null)
        }
    )
}

@Composable
fun LoadingDialog() {
        AlertDialog(onDismissRequest = {  }, title = {
            Text(text = "Loading")
        }, text = {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.End))
                Text(text = "Please wait...")
            }
        }, confirmButton = {

        }, properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        ))

}
