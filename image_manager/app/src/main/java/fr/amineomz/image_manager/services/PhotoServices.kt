package fr.amineomz.image_manager.services

import fr.amineomz.image_manager.models.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface PhotoServices {

    @Streaming
    @GET(value="image")
    fun getImageFile(
        @Query("user_email") user_email: String,
        @Query("name") name: String
    ): Call<String>

    @Multipart
    @POST(value = "create")
    fun uploadPhoto(
        @Part("user_email") user_email: RequestBody,
        @Part("name") name: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<UserResponse>

    @Multipart
    @PUT(value = "image")
    fun putPhoto(
        @Part("user_email") user_email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("new_name") new_name: RequestBody
    ): Call<UserResponse>

    @POST(value="delete")
    fun deletePhoto(
        @Query("user_email") user_email: String,
        @Query("name") name: String
    ): Call<UserResponse>
}