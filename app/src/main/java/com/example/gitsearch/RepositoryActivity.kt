package com.example.gitsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        modifier = Modifier.fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.error_icon),
                contentDescription = "Error Icon",
                modifier = Modifier.size(64.dp),
                tint = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = TextStyle(
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}


@Composable
fun RepositoryScreen(viewModel: MainViewModel, username: String) {
    val state = viewModel.state.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()
        .background(Color.LightGray)) {
        RepositoryHeader(username = username)

        when (state) {
            is MainViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }
            is MainViewModel.UiState.Error -> {
                DisplayErrorMessage(state.message)
            }
            is MainViewModel.UiState.Success -> {
                if (state.repos.isEmpty()) {
                    DisplayErrorMessage("No repositories found for user \"$username\".")
                } else {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        items(state.repos) { repo ->
                            RepositoryCard(repo)
                        }
                    }
                }
            }
            else -> {
                DisplayErrorMessage("No data available. Please try again.")
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
                color = Color.Black
            )
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome, $username",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Explore your GitHub repositories below",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            )
        }
    }
}

@Composable
fun RepositoryCard(repo: Repository) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = repo.name,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color = getLanguageColor(repo.language), shape = MaterialTheme.shapes.small)
                )
                Text(
                    text = " ${repo.language ?: "Unknown"}",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "Stars",
                    modifier = Modifier.size(18.dp),
                   tint = Color(0xFFFFC107)
                )
                Text(
                    text = " ${repo.stars}",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                )
            }
            Text(
                text = repo.description ?: "",
                style = TextStyle(fontSize = 14.sp, color = Color.Gray)
            )
        }
    }
}

fun getLanguageColor(language: String?): Color {
    return when (language?.lowercase()) {
        "kotlin" -> Color(0xFF20EBE4)
        "java" -> Color(0xFF0C65FF)
        "python" -> Color(0xFFE60EFD)
        "javascript" -> Color(0xFFFC7500)
        "c" -> Color(0xFF00FF38)
        "c++" -> Color(0xFF450D5A)
        else -> Color.LightGray
    }
}

