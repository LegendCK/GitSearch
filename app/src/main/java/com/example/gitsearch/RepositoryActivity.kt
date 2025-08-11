 package com.example.gitsearch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.gitsearch.data.Repository
import com.example.gitsearch.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepositoryActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra("username") ?: ""
        if (username.isEmpty()) {
            setContent {
                DisplayErrorMessage("Please enter a valid username to proceed.")
            }
        } else {
            viewModel.fetchRepositories(username)
            setContent {
                RepositoryScreen(viewModel = viewModel, username = username)
            }
        }
    }
}

 @Composable
 fun DisplayErrorMessage(message: String) {
     Box(
         modifier = Modifier
             .fillMaxSize()
             .background(
                 brush = Brush.verticalGradient(
                     colors = listOf(Color(0xFFFFEAEA), Color(0xFFFFF5F5))
                 )
             ),
         contentAlignment = Alignment.Center
     ) {
         Column(
             horizontalAlignment = Alignment.CenterHorizontally,
             modifier = Modifier
                 .padding(24.dp)
                 .background(
                     color = Color.White,
                     shape = RoundedCornerShape(16.dp)
                 )
                 .border(
                     width = 1.dp,
                     color = Color.Red.copy(alpha = 0.5f),
                     shape = RoundedCornerShape(16.dp)
                 )
                 .padding(24.dp)

         ) {
             Icon(
                 painter = painterResource(id = R.drawable.error_icon),
                 contentDescription = "Error Icon",
                 modifier = Modifier.size(72.dp),
                 tint = Color(0xFFD32F2F)
             )
             Spacer(modifier = Modifier.height(16.dp))
             Text(
                 text = "Oops! Something went wrong",
                 style = MaterialTheme.typography.titleMedium.copy(
                     color = Color(0xFFD32F2F),
                     fontWeight = FontWeight.Bold
                 )
             )
             Spacer(modifier = Modifier.height(8.dp))
             Text(
                 text = message,
                 style = MaterialTheme.typography.bodyMedium.copy(
                     color = Color.DarkGray,
                     textAlign = TextAlign.Center
                 ),
                 modifier = Modifier.padding(horizontal = 8.dp)
             )
         }
     }
 }


 @Composable
 fun RepositoryScreen(viewModel: MainViewModel, username: String) {
     val state = viewModel.state.collectAsState().value

     Box(
         modifier = Modifier
             .fillMaxSize()
             .background(
                 brush = Brush.verticalGradient(
                     colors = listOf(Color(0xFFf8f9fa), Color(0xFFe0e0e0))
                 )
             )
     ) {
         Column(modifier = Modifier.fillMaxSize()) {
             RepositoryHeader(username = username)

             when (state) {
                 is MainViewModel.UiState.Loading -> {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         CircularProgressIndicator(
                             color = Color(0xFF0C65FF),
                             strokeWidth = 4.dp
                         )
                     }
                 }

                 is MainViewModel.UiState.Error -> {
                     DisplayErrorMessage(state.message)
                 }

                 is MainViewModel.UiState.Success -> {
                     if (state.repos.isEmpty()) {
                         DisplayErrorMessage("No repositories found for \"$username\".")
                     } else {
                         LazyColumn(
                             contentPadding = PaddingValues(16.dp),
                             verticalArrangement = Arrangement.spacedBy(12.dp)
                         ) {
                             items(state.repos) { repo ->
                                 RepositoryCard(repo)
                             }
                         }
                     }
                 }

                 else -> {
                     DisplayErrorMessage("Something went wrong. Please try again.")
                 }
             }
         }
     }
 }


 @Composable
 fun RepositoryHeader(username: String) {
     Box(
         modifier = Modifier
             .fillMaxWidth()
             .background(
                 brush = Brush.horizontalGradient(
                     colors = listOf(Color(0xFFD6B0FC), Color(0xFF85BFFA))
                 )
             )
             .padding(vertical = 12.dp)
     ) {
         Column(
             horizontalAlignment = Alignment.CenterHorizontally,
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(horizontal = 16.dp)
         ) {
             Text(
                 text = "Welcome, $username 👋",
                 style = TextStyle(
                     color = Color.White,
                     fontSize = 28.sp,
                     fontWeight = FontWeight.ExtraBold,
                     letterSpacing = 0.8.sp,
                     textAlign = TextAlign.Center,
                     shadow = Shadow(
                         color = Color.Black.copy(alpha = 0.3f),
                         offset = Offset(2f, 2f),
                         blurRadius = 4f
                     )
                 ),
                 modifier = Modifier.fillMaxWidth()
             )
             Spacer(modifier = Modifier.height(6.dp))
             Text(
                 text = "Explore your GitHub repositories below",
                 style = TextStyle(
                     color = Color.White.copy(alpha = 0.92f),
                     fontSize = 18.sp,
                     fontWeight = FontWeight.SemiBold,
                     letterSpacing = 0.5.sp,
                     textAlign = TextAlign.Center,
                     shadow = Shadow(
                         color = Color.Black.copy(alpha = 0.2f),
                         offset = Offset(1f, 1f),
                         blurRadius = 2f
                     )
                 ),
                 modifier = Modifier.fillMaxWidth()
             )
         }
     }
 }



 @Composable
 fun RepositoryCard(repo: Repository) {
     val interactionSource = remember { MutableInteractionSource() }
     val isPressed by interactionSource.collectIsPressedAsState()
     val animatedElevation by animateDpAsState(
         targetValue = if (isPressed) 8.dp else 4.dp,
         label = "elevation"
     )


     val context = LocalContext.current
     val intent = remember {
         Intent(Intent.ACTION_VIEW, repo.url.toUri())
     }

     Card(
         modifier = Modifier
             .fillMaxWidth()
             .padding(vertical = 8.dp)
             .clickable(
                 interactionSource = interactionSource,
                 indication = rememberRipple()
             ) { /* Handle click */ },
         shape = RoundedCornerShape(16.dp),
         elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
         colors = CardDefaults.cardColors(containerColor = Color.White)
     ) {
         Column(modifier = Modifier.padding(16.dp)) {
             //AlertDialog

             var showDialog by remember { mutableStateOf(false) }
             if (showDialog) {
                 AlertDialog(
                     onDismissRequest = { showDialog = false },
                     title = { Text("Open Repository") },
                     text = { Text("Do you want to open ${repo.name}?") },
                     confirmButton = {
                         TextButton(
                             onClick = {
                                 showDialog = false
                                 context.startActivity(intent)
                             }
                         ) {
                             Text("Open")
                         }
                     },
                     dismissButton = {
                         TextButton(onClick = { showDialog = false }) {
                             Text("Cancel")
                         }
                     }
                 )
             }
             // Repository name and open icon
             Row(
                 verticalAlignment = Alignment.CenterVertically,
                 modifier = Modifier.fillMaxWidth()
             ) {
                 Text(
                     text = repo.name,
                     style = MaterialTheme.typography.titleLarge.copy(
                         fontWeight = FontWeight.Bold,
                         fontSize = 20.sp,
                         color = Color(0xFF333333)
                     ),
                     modifier = Modifier.weight(1f)
                 )

                 Icon(
                     imageVector = Icons.Default.OpenInNew,
                     contentDescription = "Open Repository",
                     tint = Color.Gray,
                     modifier = Modifier
                         .size(20.dp)
                         .clickable {
                             showDialog=true

                         }
                 )
             }

             // Language and Stars Row
             Row(
                 verticalAlignment = Alignment.CenterVertically,
                 modifier = Modifier.padding(bottom = 8.dp)
             ) {
                 // Language Chip
                 Box(
                     modifier = Modifier
                         .background(
                             color = getLanguageColor(repo.language).copy(alpha = 0.15f),
                             shape = RoundedCornerShape(50)
                         )
                         .padding(horizontal = 10.dp, vertical = 4.dp)
                 ) {
                     Row(verticalAlignment = Alignment.CenterVertically) {
                         Box(
                             modifier = Modifier
                                 .size(8.dp)
                                 .background(
                                     color = getLanguageColor(repo.language),
                                     shape = CircleShape
                                 )
                         )
                         Text(
                             text = " ${repo.language ?: "Unknown"}",
                             style = MaterialTheme.typography.bodySmall.copy(
                                 fontWeight = FontWeight.SemiBold,
                                 color = Color.DarkGray
                             )
                         )
                     }
                 }

                 Spacer(modifier = Modifier.width(12.dp))

                 // Stars
                 Icon(
                     painter = painterResource(id = R.drawable.star),
                     contentDescription = "Stars",
                     modifier = Modifier.size(16.dp),
                     tint = Color(0xFFFFC107)
                 )
                 Text(
                     text = " ${repo.stars}",
                     style = MaterialTheme.typography.bodySmall.copy(
                         fontWeight = FontWeight.Medium
                     ),
                     modifier = Modifier.padding(start = 4.dp)
                 )
             }

             // Repository description
             repo.description?.takeIf { it.isNotBlank() }?.let {
                 Text(
                     text = it,
                     style = MaterialTheme.typography.bodyMedium.copy(
                         color = Color.Gray,
                         fontStyle = FontStyle.Italic
                     )
                 )
             }
         }
     }
 }


 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun TopBarWithActions(username: String, onRefresh: () -> Unit) {
     TopAppBar(
         title = {
             Text("Repositories of $username")
         },
         actions = {
             IconButton(onClick = { onRefresh() }) {
                 Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
             }
         },
         colors = TopAppBarDefaults.topAppBarColors(
             containerColor = Color.Black,
             titleContentColor = Color.White
         )
     )
 }

 fun getLanguageColor(language: String?): Color {
     return when (language?.lowercase()) {
         "kotlin" -> Color(0xFF7F52FF)
         "java" -> Color(0xFFB07219)
         "python" -> Color(0xFF3572A5)
         "javascript" -> Color(0xFFF1E05A)
         "typescript" -> Color(0xFF2B7489)
         "c" -> Color(0xFF555555)
         "c++" -> Color(0xFFF34B7D)
         "c#" -> Color(0xFF178600)
         "go" -> Color(0xFF00ADD8)
         "dart" -> Color(0xFF00B4AB)
         "swift" -> Color(0xFFFFAC45)
         "php" -> Color(0xFF4F5D95)
         "ruby" -> Color(0xFF701516)
         "rust" -> Color(0xFFDEA584)
         "shell" -> Color(0xFF89E051)
         "html" -> Color(0xFFE34C26)
         "css" -> Color(0xFF563D7C)
         "scss" -> Color(0xFFCD6799)
         "sql" -> Color(0xFF003B57)
         else -> Color.LightGray
     }
 }
