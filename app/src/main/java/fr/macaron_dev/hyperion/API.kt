package fr.macaron_dev.hyperion

import androidx.annotation.BoolRes
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
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
                    "PUT" -> httpClient.put(url){
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

    suspend fun getProjectToValid(page: Int, search: String?): JSONArray{
        var url = "/project/nologo/invalid/$page"
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

    suspend fun putValidate(project: Int): Boolean{
        val res = request("/project/valid/$token/$project", "PUT", null)
        return (res.get("status") as JSONObject).get("code") == 200
    }

    suspend fun postProject(name: String, desc: String, start: String, duration: Int, RNA: String): Boolean{
        val logo = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAABhWlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV9TtUUqDhYUdchQnSwUFXHUKhShQqgVWnUwufRDaNKQtLg4Cq4FBz8Wqw4uzro6uAqC4AeIm5uToouU+L+k0CLGg+N+vLv3uHsHCPUS06yOGKDpFTOViIuZ7IoYeEUXhhBEDP0ys4xZSUrCc3zdw8fXuyjP8j735+hRcxYDfCLxDDPMCvE68dRmxeC8TxxmRVklPiceM+mCxI9cV1x+41xwWOCZYTOdmiMOE4uFNlbamBVNjXiSOKJqOuULGZdVzluctVKVNe/JXxjK6ctLXKc5jAQWsAgJIhRUsYESKojSqpNiIUX7cQ//oOOXyKWQawOMHPMoQ4Ps+MH/4He3Vn5i3E0KxYHOF9v+GAECu0CjZtvfx7bdOAH8z8CV3vKX68D0J+m1lhY5Anq3gYvrlqbsAZc7wMCTIZuyI/lpCvk88H5G35QF+m6B7lW3t+Y+Th+ANHWVvAEODoHRAmWvebw72N7bv2ea/f0AhKhyrtWXvPkAAAAJcEhZcwAALiMAAC4jAXilP3YAAAAHdElNRQflBhsIMSx4kz9GAAAAGXRFWHRDb21tZW50AENyZWF0ZWQgd2l0aCBHSU1QV4EOFwAAAAxJREFUCNdj2M/IDAACRgDElnpKxwAAAABJRU5ErkJggg=="
        val jsonString = "{\"name\": \"$name\", \"description\": \"$desc\", \"start\": \"$start\", \"duration\": $duration, \"RNA\": \"$RNA\", \"logo\": {\"filename\": \"LOL\", \"type\": \"image/png\", \"content\": \"$logo\"}}"
        val res = request("/project/$token", "POST", jsonString)
        withContext(Dispatchers.Default) {
            println(jsonString)
        }
        return (res.get("status") as JSONObject).getInt("code") == 200
    }

    fun isConnected(): Boolean{
        return token != null
    }
}