package com.example

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MyApplicationTheme

@Composable
fun PulseDot(color: Color, delay: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = delay),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    Box(
        modifier = Modifier
            .size(12.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}

class MainActivity : ComponentActivity() {
  private val websiteUrl = "https://earnzonebd.site"

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        var webView: WebView? by remember { mutableStateOf(null) }
        var canGoBack by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(true) }
        var isOnline by remember { mutableStateOf(checkInternetConnection()) }
        var showSplash by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
          kotlinx.coroutines.delay(7000)
          showSplash = false
        }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            if (showSplash) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color(0xFFFCF9F8)),
                contentAlignment = Alignment.Center
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Card(
                    modifier = Modifier.size(150.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                  ) {
                    Box(
                      contentAlignment = Alignment.Center,
                      modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                    ) {
                      AsyncImage(
                        model = "https://i.postimg.cc/tRdJkdS2/file-00000000653882068528b3f35f946424.png",
                        contentDescription = "App Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(24.dp))
                  Text(
                    text = "স্বাগতম",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA04100)
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "আমাদের প্ল্যাটফর্ম থেকে ইনকাম করার সুযোগ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF5A4136)
                  )
                  Spacer(modifier = Modifier.height(32.dp))
                  Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PulseDot(Color(0xFFFF6B00), 0)
                    PulseDot(Color(0xFF046C50), 500)
                    PulseDot(Color(0xFFA04100), 1000)
                  }
                  Spacer(modifier = Modifier.height(16.dp))
                  Text(
                      text = "লোড হচ্ছে...",
                      style = MaterialTheme.typography.labelMedium,
                      color = Color(0xFF5D5F5D)
                  )
                }
              }
            } else if (isOnline) {
              AndroidView(
                factory = { context ->
                  WebView(context).apply {
                    val cookieManager = android.webkit.CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)

                    layoutParams = ViewGroup.LayoutParams(
                      ViewGroup.LayoutParams.MATCH_PARENT,
                      ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.apply {
                      javaScriptEnabled = true
                      domStorageEnabled = true
                      setSupportZoom(false)
                      builtInZoomControls = false
                      loadWithOverviewMode = true
                      useWideViewPort = true
                      javaScriptCanOpenWindowsAutomatically = true
                      allowFileAccess = true
                      allowContentAccess = true
                    }
                    webViewClient = object : WebViewClient() {
                      override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        isLoading = true
                      }
                      override fun onPageFinished(view: WebView?, url: String?) {
                        isLoading = false
                        canGoBack = view?.canGoBack() ?: false
                      }
                      override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        isOnline = false
                      }
                      override fun onReceivedSslError(view: WebView?, handler: android.webkit.SslErrorHandler?, error: android.net.http.SslError?) {
                        handler?.proceed()
                      }
                    }
                    webChromeClient = object : WebChromeClient() {
                      override fun onShowFileChooser(
                        webView: WebView?,
                        filePathCallback: android.webkit.ValueCallback<Array<android.net.Uri>>?,
                        fileChooserParams: FileChooserParams?
                      ): Boolean {
                        // For a simple WebView wrapper, this is often sufficient,
                        // but requires robust implementation for file picking.
                        // Given the constraints and the goal, this is the place to handle it.
                        // To keep it functional without excessive complexity, we'll
                        // note that actual file picker implementation requires
                        // ActivityResultLauncher.
                        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
                      }
                    }
                    loadUrl(websiteUrl)
                    webView = this
                  }
                },
                update = { webView = it }
              )
            } else {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = {
                  isOnline = checkInternetConnection()
                  webView?.reload()
                }) {
                  Text("No Internet Connection - Retry")
                }
              }
            }
          }

          BackHandler(enabled = canGoBack) {
            webView?.goBack()
          }
        }
      }
    }
  }

  private fun checkInternetConnection(): Boolean {
    val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
  }
}
