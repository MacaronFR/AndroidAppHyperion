package fr.macaron_dev.hyperion

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject

class API {
    private val endpoint = "https://api.hyperion.dev.macaron-dev.fr"
    var token: String = ""
        private set
    private val clientId = "1234"
    private val clientSecret = "9876"
    private val httpClient = HttpClient(CIO)

    fun connect(mail: String, password: String): Boolean{
        val res = runBlocking {
            request("/token/$clientId/$clientSecret/$mail/$password")
        }
        return if((res.get("status") as JSONObject).get("code") == 200){
            token = (res.get("content") as JSONObject).get("token").toString()
            true
        }else
            false
    }

    suspend fun request(uri: String):JSONObject{
        val url = "$endpoint$uri"
        return try {
            val response: HttpResponse = httpClient.get(url)
            JSONObject(response.readText())
        }catch (e: ClientRequestException){
            try {
                JSONObject(e.response.readText())
            }catch (e: JSONException){
                JSONObject("{\"status\":{\"code\": \"500\", \"message\": \"Internal Server Error\"}}")
            }
        }
    }
}