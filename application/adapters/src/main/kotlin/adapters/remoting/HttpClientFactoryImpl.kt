package adapters.remoting

import adapters.primary.web.util.RestExternalServiceCallException
import com.github.michaelbull.logging.InlineLogger
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.pwall.json.ktor.JSONKtor
import net.pwall.json.ktor.client.JSONKtorClient
import net.pwall.json.parseJSON
import shared.util.e
import java.nio.ByteBuffer
import java.nio.charset.Charset

internal class HttpClientFactoryImpl : HttpClientFactory {

    private val logger = InlineLogger()

    private val _httpClient by lazy {
        HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                }
            }
            install(JsonFeature) {
                serializer = JSONKtorClient()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            HttpResponseValidator {
                validateResponse { response ->
                    val statusCode = response.status.value
                    if (statusCode in 300..399) {
                        throw RedirectResponseException(response)
                    } else if (statusCode >= 400) {
                        response.throwException()
                    }
                }
            }
        }
    }

    override fun httpClient() = _httpClient

    /**
     * Map HTTP response status to RestExternalServiceCallException with response payload (if possible).
     */
    private suspend fun HttpResponse.throwException(): Nothing {
        val errorMap = try {
            // At first we will try to exract response payload and map it to JSON structure of plain text
            val body = readBytes()
            if (contentType()?.contentType?.contains(ContentType.Application.Json.contentType) == true) {
                val bodyText = contentType()?.charset()?.decode(ByteBuffer.wrap(body)) ?: body.toString(Charset.defaultCharset())
                bodyText.parseJSON<Map<String, Any?>>()!!
            } else {
                mapOf("responseBody" to String(body))
            }
        } catch (e: Throwable) {
            logger.e("HttpResponse.throwException()") { "Failed to map error response" }
            // If previous attempt of mapping failed - we fallback to default behavior
            when (status.value) {
                in 300..399 -> throw RedirectResponseException(this)
                in 400..499 -> throw ClientRequestException(this)
                in 500..599 -> throw ServerResponseException(this)
            }
            throw ResponseException(this)
        }
        throw RestExternalServiceCallException(
            status = status,
            specifics = errorMap
        )
    }
}
