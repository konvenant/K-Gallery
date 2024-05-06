package com.example.k_gallery.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import com.example.k_gallery.R
import com.example.k_gallery.presentation.graph.MainScreen
import com.example.k_gallery.presentation.util.DisplayImage
import com.example.k_gallery.presentation.util.DisplayVideo
import com.example.k_gallery.presentation.viewmodel.IntentViewModel
import com.example.k_gallery.ui.theme.KGalleryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val intentViewModel by viewModels<IntentViewModel>()

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val coroutineExceptionHandle = CoroutineExceptionHandler { _, throwable ->

        }
        setContent {
            KGalleryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (intent?.action) {
                        Intent.ACTION_SEND -> {
                            if (intent.type?.startsWith("image/") == true) {
                                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                                    DisplayImage(uri = it)
                                }
                            } else if (intent.type?.startsWith("video/") == true) {
                                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                                    DisplayVideo(uri = it)
                                }
                            }
                        }

                        Intent.ACTION_SEND_MULTIPLE -> {

                        }

                        else -> {
                          ErrorBoundary {
                              MainScreen(this)
                          }
                        }
                    }

                }
            }
        }
    }
}



@Composable
fun ErrorBoundary(content : @Composable () -> Unit){
    val errorMessage = remember {
        mutableStateOf<String?>(null)
    }

    val context = LocalContext.current

    CompositionLocalProvider(LocalErrorHandler provides {message -> errorMessage.value = message}) {
       content()
        errorMessage.value?.let { errorMessage ->
          ErrorScreen(errorMessage) {
              restartApp(context)
          }
        }
    }
}
val LocalErrorHandler = compositionLocalOf<(String) -> Unit> { throw IllegalArgumentException("No error handler provided")}



@Composable
fun ErrorScreen(errorMessage: String, onDismiss: () -> Unit){
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.k_error2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

       Column(
           horizontalAlignment = Alignment.CenterHorizontally,
           modifier = Modifier
               .fillMaxWidth()
               .padding(16.dp)
               .align(Alignment.BottomCenter)) {
           Text(
               text = errorMessage,
               color = Color.Red
           )
           Spacer(modifier = Modifier.height(16.dp))
           Button(onClick = onDismiss) {
               Text(text = "Retry")
           }
       }
    }
}

fun restartApp(context: Context) {
   val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)

}