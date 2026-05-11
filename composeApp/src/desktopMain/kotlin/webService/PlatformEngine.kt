package webService

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun getHttpClientEngine(): HttpClientEngineFactory<*> = CIO
