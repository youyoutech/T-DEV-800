package fr.amineomz.image_manager.services

import com.google.gson.JsonObject
import fr.amineomz.image_manager.models.User
import fr.amineomz.image_manager.models.UserResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserServices {

    @Headers("Content-Type: application/json")
    @POST(value = "register")
    fun createUser(@Body body: JsonObject): Call<UserResponse>

    @Headers("Content-Type: application/json")
    @POST(value = "login")
    fun loginUser(@Body body: JsonObject): Call<UserResponse>

    @GET(value="profile")
    fun getProfile(
        @Query("user_email") user_email: String
    ): Call<User>

    @Multipart
    @PUT(value="profile")
    fun putProfile(
        @Part(value = "email") email: RequestBody,
        @Part(value = "username") username: RequestBody,
        @Part(value = "phone_number") phone_number: RequestBody
    ): Call<UserResponse>

    @GET(value = "gallery")
    fun getGallery(
        @Query("user_email") user_email: String
    ): Call<UserResponse>
}