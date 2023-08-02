package api

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import models.*
import retrofit2.Call
import retrofit2.http.*
import java.util.Objects

interface DiscoveryApi {

    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    @POST("/api/token")
    fun authenticateUser(
        @Field("grant_type") grant_type: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<AuthentificationResponse>

    @Headers(
        "Content-Type: application/json"
    )
    @POST("events")
    fun apiSendEvent(
        @Body event: JsonObject,

    ): Call<String>


}