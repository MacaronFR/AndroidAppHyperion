package fr.macaron_dev.hyperion

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.nio.channels.UnresolvedAddressException

class API: Serializable {
    private val endpoint = "https://api.hyperion.dev.macaron-dev.fr"
    private lateinit var token: String
    private val clientId = "1234"
    private val clientSecret = "9876"
    @Transient
    private val httpClient = HttpClient(CIO)

    suspend fun connect(mail: String, password: String): Int{
        val res = request("/token/$clientId/$clientSecret/$mail/$password")
        return when((res.get("status") as JSONObject).getInt("code")){
            0 -> 1
            200 -> {
                token = (res.get("content") as JSONObject).get("token").toString()
                0
            }
            else -> 2
        }
    }

    private suspend fun request(uri: String):JSONObject{
        val url = "$endpoint$uri"
        return try {
            val response: HttpResponse
            try {
                response = httpClient.get(url)
            }catch (e: UnresolvedAddressException){
                return JSONObject("{\"status\":{\"code\": 0, \"message\": \"Connection to Server cannot be established\"}}")
            }
            JSONObject(response.readText())
        }catch (e: ClientRequestException){
            try {
                JSONObject(e.response.readText())
            }catch (e: JSONException){
                JSONObject("{\"status\":{\"code\": 500, \"message\": \"Internal Server Error\"}}")
            }
        }
    }

    suspend fun getProfile(): JSONObject{
        val res = request("/me/$token")
        return if ((res.get("status") as JSONObject).get("code") == 200){
            res.get("content") as JSONObject
        }else{
            res
        }
    }

    suspend fun getLatestProject(): JSONArray{
        val res = request("/project/nologo/latest/0")
        return if((res.get("status") as JSONObject).get("code") == 200){
            res.get("content") as JSONArray
        }else{
            JSONArray("[500, \"Internal Server Error\"]")
        }
    }

    suspend fun getLogo(id: Int): JSONObject{
        val res = request("/project/logo/$id")
        return if ((res.get("status") as JSONObject).get("code") == 200){
            res.get("content") as JSONObject
        }else{
            res
        }
    }
}