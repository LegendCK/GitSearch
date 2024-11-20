package com.example.gitsearch.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

data class Repository(
    @SerializedName("name") val name: String,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("language") val language: String?,
    @SerializedName("description") val description: String?
)

interface GitHubApiService {
    @GET("users/{username}/repos") // Use {username} to dynamically insert the username
    suspend fun getUserRepos(@Path("username") username: String): List<Repository>
}
