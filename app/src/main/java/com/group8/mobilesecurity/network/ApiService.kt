package com.group8.mobilesecurity.network

import com.group8.mobilesecurity.data.model.PostDto
import com.group8.mobilesecurity.data.model.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // Normal, harmless request
    @GET("posts")
    suspend fun getPosts(): List<PostDto>

    // IDOR-style: fetching user profile directly by ID
    // Changing {id} reveals another user's profile
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserDto
}
