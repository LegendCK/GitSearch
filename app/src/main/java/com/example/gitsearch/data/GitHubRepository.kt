package com.example.gitsearch.data

import javax.inject.Inject

class GitHubRepository @Inject constructor(private val apiService: GitHubApiService) {
    suspend fun getUserRepositories(username: String): Result<List<Repository>> {
        return try {
            val response = apiService.getUserRepos(username)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
