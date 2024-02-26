package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
import com.example.k_gallery.presentation.viewmodel.AuthViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreen(
    navController: NavController,
    email: String
) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        val authViewModel: AuthViewModel = hiltViewModel()
        authViewModel.message.value = null
        authViewModel.userDetails.value = null
        Scaffold (
            modifier = Modifier.fillMaxSize()
        ){

            val token by remember {
                mutableIntStateOf(0)
            }

            var showError by remember {
                mutableStateOf(false)
            }

            val context = LocalContext.current

            var otpValue by remember {
                mutableIntStateOf(0)
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
                    text = "Verify Email",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "verify $email",
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
                    otpText = otpValue.toString() ?: "",
                    onOtpChange = { value, _ ->
                        try{
                            otpValue = value.toInt()
                        } catch (e:NumberFormatException){
                            Toast.makeText(context,e.message.toString(),Toast.LENGTH_LONG).show()
                        }
                    },
                    onTextComplete = {
                        authViewModel.verifyEmail(email,otpValue)
                        authViewModel.userDetails.value = null
                    },
                    context = context
                )




                Spacer(modifier = Modifier.height(42.dp))

                Text(
                    text = "Didn't receive Verification Code?,",
                    modifier = Modifier
                        .padding(8.dp)
                )

                Button(
                    onClick = {
                        authViewModel.sendToken(email)
                        authViewModel.userDetails.value = null
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
            PerformVerifyEmail(
                authViewModel,
                email,
                otpValue,
                navController,
                clearOtp = {
                    otpValue = 0
                }
            )
            PerformSendToken(authViewModel, email )
        }
    }
}
@Composable
fun PerformVerifyEmail(
    authViewModel: AuthViewModel,
    email: String,
    token : Int,
    navController: NavController,
    clearOtp: () -> Unit
) {
    var showDialog by remember {
        mutableStateOf(true)
    }

    val state by authViewModel.userDetails.observeAsState()

    when(state){
        is Resource.Success -> {
            navController.navigate(NavHelper.CompleteRegistrationScreen.route+"/${email}") {
                popUpTo(NavHelper.VerifyEmailScreen.route){
                    inclusive = true
                }
            }
            authViewModel.userDetails.value = null
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
                        authViewModel.verifyEmail(email,token)
                    },
                    negative = { showDialog2 = !showDialog2
                        authViewModel.userDetails.value = null
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
fun PerformSendToken(
    authViewModel: AuthViewModel,
    email: String
) {
    var showDialog by remember {
        mutableStateOf(true)
    }

    val state by authViewModel.message.observeAsState()

    when(state){
        is Resource.Success -> {
            val message = (state as Resource.Success<Message>).data!!.message
            showDialog = false
            var showDialog2 by remember {
                mutableStateOf(true)
            }
            if (showDialog2){
            ShowAuthAlertDialog(
                title = "Success",
                desc = message,
                positive = {
                    authViewModel.message.value = null
                    showDialog2 = !showDialog2
                           },
                negative = {
                    authViewModel.message.value = null
                    showDialog2 = !showDialog2
                           },
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
                        authViewModel.sendToken(email)
                    },
                    negative = {  },
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
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount:Int = 4,
    onOtpChange: (String,Boolean) -> Unit,
    onTextComplete: () -> Unit,
    context:Context
) {
    LaunchedEffect(Unit){
        if (otpText.length > otpCount){
            Toast.makeText(context,"code value must not exceed $otpCount",Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(otpText){
        if (otpText.length == otpCount){
            onTextComplete()
        }
    }


    BasicTextField(
        value = TextFieldValue(otpText, TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpCount){
                onOtpChange.invoke(it.text,it.text.length == otpCount)
            }
            if (otpText.length == otpCount){
                onTextComplete()
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(otpCount){index ->
                    CharView(
                        index = index,
                        text = otpText,
                        count = otpCount
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        },
        modifier = modifier
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String,
    count:Int
){
    val isFocused = text.length == index
    val char = when{
        index == text.length -> "-"
        index > text.length -> ""
        else -> text[index].toString()
    }

    Text(
        text = char,
        modifier = Modifier
            .width(40.dp)
            .border(
                1.dp,
                when {
                    isFocused -> Color.LightGray
                    text.length == count -> Blue
                    else -> Red
                }, RoundedCornerShape(8.dp)
            )
            .padding(2.dp),
        style = MaterialTheme.typography.headlineMedium,
        color = if (isFocused) Blue else Red,
        textAlign = TextAlign.Center
    )
}