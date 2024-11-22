package com.example.gitsearch.data

import javax.inject.Inject

class GitHubRepository @Inject constructor(private val apiService: GitHubApiService) {

    suspend fun getUserRepositories(username: String): Result<List<Repository>> {
        return try {
            val response = apiService.getUserRepos(username)
            if (response.isSuccessful) {
                val repos = response.body() ?: emptyList()
                Result.success(repos)
            } else {
                if (response.code() == 404) {
                    Result.failure(Throwable("User not found"))
                } else {
                    Result.failure(Throwable("Error: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Throwable("Network error"))
        }
    }
}
