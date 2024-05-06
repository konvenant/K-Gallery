package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.UserSharedPrefManager
import com.example.k_gallery.presentation.viewmodel.AuthViewModel
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrEditProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    email: String,
    userSharedViewModel: UserSharedViewModel,
) {
    val authViewModel: AuthViewModel = hiltViewModel()


    val sheetState = rememberModalBottomSheetState()
    val changePasswordSheetState = rememberModalBottomSheetState()

    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var isChangePasswordSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val user by  userSharedViewModel.userData.observeAsState()

    var country by remember {
        mutableStateOf(user!!.user.country)
    }

    var city by remember {
        mutableStateOf(user!!.user.city)
    }
    var name by remember {
        mutableStateOf(user!!.user.name)
    }
    var phone by remember {
        mutableStateOf(user!!.user.phone)
    }

    val context = LocalContext.current

    val isButtonEnabled by derivedStateOf {
        country.isNotEmpty() && city.isNotEmpty() && phone.isNotEmpty() && name.isNotEmpty()
    }

    val focusManager = LocalFocusManager.current

    val color = if (isButtonEnabled) Blue else Color.Red

    var password by remember {
        mutableStateOf("")
    }

    var handleResult by remember { mutableStateOf(false) }

    val isPasswordButtonEnabled by derivedStateOf {
        password.isNotEmpty()
    }


    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }



    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        selectedImageUri = uri
    }


    val scaffoldState = rememberBottomSheetScaffoldState()

    val updateImageState by userViewModel.updateImage.observeAsState()

    var isLoading by remember {
        mutableStateOf(false)
    }

    val userPrefManager = remember {
        UserSharedPrefManager(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "My Profile")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIos,
                            contentDescription = null,
                            tint = Blue
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(top = 42.dp, bottom = 32.dp, start = 8.dp, end = 8.dp)
        ){
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Box(modifier = Modifier
                        .padding(8.dp)){
                        AsyncImage(
                            model = user!!.user.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape),
                            placeholder = painterResource(id = R.drawable.full_logo),
                            error = painterResource(id = R.drawable.logo)
                        )
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Blue,
                                contentColor = Color.White
                            ),
                            onClick = {
                                getContent.launch("image/*")
                            },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    selectedImageUri?.let { uri ->
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Gray,
                                contentColor = Blue
                            ),
                            onClick = {
                                selectedImageUri = null
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                tint = Blue
                            )
                        }
                       AsyncImage(
                           model = uri,
                           contentDescription = null,
                           modifier = Modifier
                               .size(100.dp)
                               .clip(RoundedCornerShape(8.dp)),
                           contentScale = ContentScale.Crop
                       )
                        Spacer(modifier = Modifier.height(2.dp))
                        Button(onClick = {
                            selectedImageUri?.let { uri ->
                               userViewModel.updateImage(email,uri,context)
                                handleResult = true
                                selectedImageUri = null
                            }
                        }) {
                            Text("Update Image")
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Hi, ${user!!.user.name}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = user!!.user.email,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                ){
                   IconButton(onClick = { isSheetOpen = true }, modifier = Modifier.align(Alignment.End)) {
                       Icon(
                           imageVector = Icons.Default.Edit,
                           contentDescription = null,
                           tint = Blue
                       )
                   }
                    Spacer(modifier = Modifier.height(2.dp))
                    MyProfileItem(title = "Name", desc = user!!.user.name) {
                    }
                    MyProfileItem(title = "Phone Number", desc = user!!.user.phone) {
                    }
                    MyProfileItem(title = "City", desc = user!!.user.city) {
                    }
                    MyProfileItem(title = "Country", desc = user!!.user.country) {
                    }

                }
            }

            item {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Update Password", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Button(
                        onClick = { isChangePasswordSheetOpen = true },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "Change Password")
                    }
                }
            }
        }
        if (handleResult){

            when (updateImageState) {
                is Resource.Error -> {
                    isLoading = false
                    val err = (updateImageState as Resource.Error<User>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    handleResult = false
                }

                is Resource.Success -> {

                    isLoading = false
                    val userUpdated = (updateImageState as Resource.Success<User>).data!!
                    userSharedViewModel.userData.postValue(userUpdated)
                    handleResult = false
                    Toast.makeText(context,"Image Updated Successfully", Toast.LENGTH_LONG).show()
                }

                is Resource.Loading -> {
                    isLoading = true
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading) {
                            LinearLoadingDialog()
                        }
                    }
                }

                else -> {

                }

            }
        }


        if (isSheetOpen){
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { isSheetOpen = false }
            ) {
                Column(
                    modifier = Modifier
                        .background(Milk)
                        .padding(16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(
                        text = "Update Details",
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
                        Text(text = "Update",  style = TextStyle.Default.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ))
                    }
                }
            }
        }

        if(isChangePasswordSheetOpen){
            ModalBottomSheet(
                onDismissRequest = { isChangePasswordSheetOpen = false },
                sheetState = changePasswordSheetState
            ) {
                Column(
                    modifier = Modifier
                        .background(Milk)
                        .padding(16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(
                        text = "Change Password",
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = color,
                            unfocusedBorderColor = color,
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            authViewModel.userDetailsDuplicate.value = null
                            authViewModel.completeForgotPassword(email, password)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        enabled = isPasswordButtonEnabled,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Red
                        )
                    ) {
                        Text(text = "Update",  style = TextStyle.Default.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ))
                    }
                }

            }
        }
        HandleUpdateUserDetails(
            authViewModel,
            email,
            name,
            phone,
            city,
            country,
            userSharedViewModel,
            context
        )

        HandleChangePassword(authViewModel, email, password,userSharedViewModel,userPrefManager,context)

    }


}



@Composable
fun MyProfileItem(
    title: String,
    desc:String,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .clickable {

            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
         Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
        Text(text = desc, style = MaterialTheme.typography.bodyMedium)

    }
    Spacer(modifier = Modifier.height(2.dp))
    Divider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = Color.LightGray
    )
}






@Composable
fun HandleUpdateUserDetails(
    authViewModel: AuthViewModel,
    email: String,
    name: String,
    phone: String,
    city: String,
    country: String,
    sharedViewModel: UserSharedViewModel,
    context: Context
){
    val state by authViewModel.userDetails.observeAsState()
    var showDialog by remember {
        mutableStateOf(true)
    }
    when(state){
        is Resource.Success -> {
            val user = (state as Resource.Success<User>).data


           if (showDialog){
               ShowAuthAlertDialog(
                   title = "User Details Updated",
                   desc = "Successfully updated user details ",
                   positive = { showDialog = false
                       authViewModel.userDetails.value = null},
                   negative = { showDialog = false
                       authViewModel.userDetails.value = null},
                   positiveText = "ok",
                   negativeText = "close",
                   icon = Icons.Default.CheckCircleOutline
               )
           }
            sharedViewModel.userData.postValue(user)
            Toast.makeText(context, "User Details Updated Successfully",Toast.LENGTH_SHORT).show()

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

@Composable
fun HandleChangePassword(
    authViewModel: AuthViewModel,
    email: String,
    password:String,
    sharedViewModel: UserSharedViewModel,
    userSharedPrefManager: UserSharedPrefManager,
    context: Context
){
    val state by authViewModel.userDetailsDuplicate.observeAsState()
    var showDialog by remember {
        mutableStateOf(true)
    }


    when(state){
        is Resource.Success -> {
            val user = (state as Resource.Success<User>).data!!
            sharedViewModel.userData.postValue(user)

         if (showDialog){
             ShowAuthAlertDialog(
                 title = "Password Changed",
                 desc = "User Password Updated Successfully",
                 positive = { showDialog = false
                     authViewModel.userDetailsDuplicate.value = null},
                 negative = { showDialog = false
                     authViewModel.userDetailsDuplicate.value = null},
                 positiveText = "ok",
                 negativeText = "close",
                 icon = Icons.Default.CheckCircleOutline
             )
             saveUserPref(email,password,userSharedPrefManager)
             Toast.makeText(context, "User Password Updated Successfully",Toast.LENGTH_SHORT).show()
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
                        authViewModel.userDetailsDuplicate.value = null
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


