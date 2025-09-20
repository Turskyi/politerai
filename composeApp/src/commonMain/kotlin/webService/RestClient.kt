package webService

import entity.Entity
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.encodeURLParameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RestClient {
    private val httpClient = HttpClient {
        install(plugin = ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    useAlternativeNames = false
                },
            )
        }
    }

    private val primaryBaseUrl = "https://politerai.com"
    private val fallbackBaseUrl = "https://politer.turskyi.com"

    suspend fun getPoliteMessage(
        prompt: String,
    ): Entity {
        val encodedPrompt: String = prompt.encodeURLParameter()
        return try {
            fetchPoliteMessage(primaryBaseUrl, encodedPrompt)
        } catch (e: Exception) {
            println(
                "Primary URL request failed: ${e.message}. Trying fallback...",
            )
            // If primary fails for any reason, try the fallback URL.
            try {
                fetchPoliteMessage(fallbackBaseUrl, encodedPrompt)
            } catch (fallbackException: Exception) {
                // If fallback also fails return a default error entity.
                handleException(fallbackException)
            }
        }
    }

    private suspend fun fetchPoliteMessage(
        baseUrl: String,
        encodedPrompt: String,
    ): Entity {
        try {
            return httpClient.get(
                urlString = "$baseUrl/api/polite?prompt=$encodedPrompt",
            ).body()
        } catch (e: NoTransformationFoundException) {
            // This specific exception means the network request likely
            // succeeded,  but the response body couldn't be converted to the
            // `Entity` type.
            // For now, this will be caught by the outer catch if it happens on
            // the primary URL, triggering a fallback.
            // If it happens on the fallback, it will be caught by the
            // fallback's catch block.
            // Re-throw to be caught by the appropriate handler above.
            throw e
        }
    }

    private fun handleException(e: Exception): Entity {
        println("Fallback URL request also failed: ${e.message}")

        e.printStackTrace()
        return Entity(
            politerMessage = "Looks like something went wrong. " +
                    "Please contact support!",
        )
    }
}