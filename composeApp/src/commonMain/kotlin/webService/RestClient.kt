package webService

import entity.Entity
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RestClient {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    useAlternativeNames = false
                },
            )
        }
    }

    suspend fun getPoliteMessage(
        prompt: String,
    ): Entity {
        return try {
            httpClient.get("https://www.politerai.com/api/polite?prompt=${prompt}").body()
        } catch (e: NoTransformationFoundException) {
            Entity(politerMessage = "Looks like something went wrong. Please contact support!")
        }
    }
}