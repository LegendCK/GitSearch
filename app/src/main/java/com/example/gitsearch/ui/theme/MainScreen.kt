package com.example.gitsearch.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gitsearch.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    var username by remember { mutableStateOf(TextFieldValue()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Input field for username
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter GitHub Username") },
            placeholder = { Text("e.g., Ajay-patidarO") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Optionally handle onDone action */ }
            )
        )

        // Search Button
        Button(onClick = {
            if (username.text.isNotBlank()) {
                viewModel.fetchRepositories(username.text)
                errorMessage = null // Reset error message on new search
            } else {
                errorMessage = "Please enter a valid username."
            }
        }) {
            Text("Search")
        }

        // Display loading indicator or error message
        when (val state = viewModel.state.collectAsState().value) {
            is MainViewModel.UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
            is MainViewModel.UiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(top = 16.dp),
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
            is MainViewModel.UiState.Success -> {
                LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                    items(state.repos) { repo ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Name: ${repo.name}")
                            Text("Stars: ${repo.stars}")
                            Text("Language: ${repo.language ?: "N/A"}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            else -> Text("Search for a GitHub user.", modifier = Modifier.padding(top = 16.dp))
        }

        // Error message for empty input
        errorMessage?.let {
            Text(
                text = it,
                color = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
