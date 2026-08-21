package webService

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun getHttpClientEngine(): HttpClientEngineFactory<*> = Darwin
