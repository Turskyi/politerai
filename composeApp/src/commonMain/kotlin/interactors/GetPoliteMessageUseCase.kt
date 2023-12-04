package interactors

import entity.Entity
import webService.RestClient

class GetPoliteMessageUseCase {

    private val api = RestClient()

    @Throws(Exception::class)
    suspend fun getPoliteMessage(
        prompt: String,
    ): Entity = api.getPoliteMessage(prompt = prompt)
}