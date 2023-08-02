package api


import com.google.gson.JsonObject
import io.github.oshai.kotlinlogging.KotlinLogging
import models.AuthentificationResponse
import network.RetrofitClient
import network.TokenHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

private val logger = KotlinLogging.logger {}

interface ServiceApi {

    companion object {

        fun apiGetToken(serverUrl: String, username: String, password: String, unsafe: Boolean): String? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiService = retrofit.create(DiscoveryApi::class.java)

            var token: String? = null
            val response = apiService.authenticateUser("password", username, password).execute()
            if (response.isSuccessful) {
                token = response.body()?.token!!
            } else {
                throw HttpException(response)
            }
            return token

        }

        fun apiSendEvent(serverUrl: String, source: String, type: String, params: JsonObject, unsafe: Boolean): String? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiService = retrofit.create(DiscoveryApi::class.java)
            var id = ""
            val data = JsonObject()
            data.addProperty("source", source)
            data.addProperty("type", type)
            data.add("params", params)
            val response = apiService.apiSendEvent(data).execute()
            if (response.isSuccessful) {
                id = response.body().toString()
            } else {
                throw HttpException(response)
            }
            return id
        }


    }
}




