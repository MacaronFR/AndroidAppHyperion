package fr.macaron_dev.hyperion

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.channels.UnresolvedAddressException

class API{
    private val endpoint = "https://api.hyperion.dev.macaron-dev.fr"
    private var token: String? = null
    private val clientId = "11122001"
    private val clientSecret = "31122004"
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

    private suspend fun request(uri: String): JSONObject{
        return request(uri, "GET", null)
    }

    private suspend fun request(uri: String, method: String, body: String?):JSONObject{
        val url = "$endpoint$uri"
        return try {
            val response: HttpResponse?
            try {
                response = when(method){
                    "GET" -> httpClient.get(url)
                    "POST" -> httpClient.post(url){
                        if(body != null){
                            this.body = body
                        }
                    }
                    else -> null
                }
            }catch (e: UnresolvedAddressException){
                return JSONObject("{\"status\":{\"code\": 0, \"message\": \"Connection to Server cannot be established\"}}")
            }
            try {
                JSONObject(response?.readText() ?: "{\"status\":{\"code\": 1, \"message\": \"Bad Method\"}}")
            }catch (e: JSONException){
                JSONObject("{\"status\":{\"code\": 2, \"message\": \"Not JSON Return\"}}")
            }
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

    suspend fun getPopularProject(): JSONArray{
        val res = request("/project/nologo/popular/0")
        return if((res.get("status") as JSONObject).get("code") == 200){
            res.get("content") as JSONArray
        }else{
            JSONArray("[500, \"Internal Server Error\"]")
        }
    }

    suspend fun getProject(page: Int, search: String?): JSONArray{
        var url = "/project/nologo/$page"
        if(search != null){
            url = "$url/search/$search"
        }
        val res = request(url)
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

    suspend fun postContribute(project: Int, amount: Int): Boolean{
        val jsonString = "{\"amount\": $amount, \"project\": $project}"
        val res = request("/project/contribute/$token", "POST", jsonString)
        return (res.get("status") as JSONObject).get("code") == 200
    }

    fun isConnected(): Boolean{
        return token != null
    }
}