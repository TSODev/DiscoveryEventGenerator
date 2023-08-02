package api


import com.google.gson.JsonObject
import io.github.oshai.kotlinlogging.KotlinLogging
import network.RetrofitClient

private val logger = KotlinLogging.logger {}

interface ServiceApi {

    companion object {

        fun apiGetToken(serverUrl: String, username: String, password: String, unsafe: Boolean): String? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiService = retrofit.create(DiscoveryApi::class.java)

            return apiService.authenticateUser("password", username, password).execute().body()?.token

        }

        fun apiSendEvent(serverUrl: String, source: String, type: String, params: JsonObject, unsafe: Boolean): String? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiService = retrofit.create(DiscoveryApi::class.java)
            val data = JsonObject()
            data.addProperty("source", source)
            data.addProperty("type", type)
            data.add("params", params)
            val id = apiService.apiSendEvent(data).execute()
            logger.debug("id : $id, data: $data")
            return id.body()
        }


    }
}




