package webService

import io.ktor.client.engine.HttpClientEngineFactory

expect fun getHttpClientEngine(): HttpClientEngineFactory<*>
