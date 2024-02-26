package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.AuthViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun VerifyForgotPasswordToken(
    navController: NavController,
    email: String
){
    val authViewModel: AuthViewModel = hiltViewModel()
    authViewModel.message.value = null
    authViewModel.userDetails.value = null
    authViewModel.verifyPasswordDetails.value = null
    Scaffold(modifier = Modifier.fillMaxSize()) {

            val context = LocalContext.current

            var otpValue by remember {
                mutableStateOf(0)
            }
            val color = Blue

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
                Text(
                    text = "Reset Password",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "verify code sent to $email to continue",
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Enter 4 digit Code",
                    style = TextStyle.Default.copy(
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { }
                )

                OtpTextField(
                    otpText = otpValue.toString(),
                    onOtpChange = { value, _ ->
                        try{
                            otpValue = value.toInt()
                        } catch (e:NumberFormatException){
                            Toast.makeText(context,e.message.toString(),Toast.LENGTH_LONG).show()
                        }
                    },
                    onTextComplete = {
                        authViewModel.verifyForgotPasswordToken(email,otpValue.toString())
                        authViewModel.verifyPasswordDetails.value = null
                    },
                    context = context
                )

                Spacer(modifier = Modifier.height(42.dp))
                Text(
                    text = "Didn't receive the Code?,",
                    modifier = Modifier
                        .padding(8.dp)
                )

                Button(
                    onClick = {
                        authViewModel.forgotPassword(email)
                        authViewModel.verifyPasswordDetails.value = null
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Red,
                        containerColor = color
                    )
                ) {
                    Text(
                        text = "Resend Code", style = TextStyle.Default.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }

            }
            PerformVerifyForgotPasswordToken(
                authViewModel,
                email,
                otpValue,
                navController,
                clearOtp = {
                    otpValue = 0
                }
            )
            PerformSendForgotToken(authViewModel,email)

        }
    }




@Composable
fun PerformVerifyForgotPasswordToken(
    authViewModel: AuthViewModel,
    email: String,
    token : Int,
    navController: NavController,
    clearOtp: () -> Unit
) {
    var showDialog by remember {
        mutableStateOf(true)
    }

    val state by authViewModel.verifyPasswordDetails.observeAsState()

    when(state){
        is Resource.Success -> {
            authViewModel.verifyPasswordDetails.value = null
            navController.navigate(NavHelper.CompleteForgotPasswordScreen.route+"/${email}")
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
                        authViewModel.verifyPasswordDetails.value = null
                        authViewModel.verifyForgotPasswordToken(email,token.toString())
                    },
                    negative = { showDialog2 = !showDialog2
                        authViewModel.verifyPasswordDetails.value = null
                        clearOtp()},
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

@Composable
fun PerformSendForgotToken(
    authViewModel: AuthViewModel,
    email: String
) {
    var showDialog by remember {
        mutableStateOf(true)
    }

    val state by authViewModel.userDetails.observeAsState()

    when (state) {
        is Resource.Success -> {
            val user = (state as Resource.Success<User>).data!!.user
            val message = "Token sent to ${user.email} successfully"
            showDialog = false
            var showDialog2 by remember {
                mutableStateOf(true)
            }

            if (showDialog2) {
                ShowAuthAlertDialog(
                    title = "Success",
                    desc = message,
                    positive = { showDialog2 = !showDialog2
                        authViewModel.userDetails.value = null},
                    negative = { showDialog2 = !showDialog2
                        authViewModel.userDetails.value = null},
                    positiveText = "ok",
                    negativeText = "close",
                    icon = Icons.Default.CheckCircleOutline
                )
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
            if (showDialog2) {
                ShowAuthAlertDialog(
                    title = "Error",
                    desc = error.toString(),
                    positive = {
                        authViewModel.forgotPassword(email)
                        authViewModel.userDetails.value = null
                    },
                    negative = { showDialog2 = !showDialog2
                        authViewModel.userDetails.value = null},
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
