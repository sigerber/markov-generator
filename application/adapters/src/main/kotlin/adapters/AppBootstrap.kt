package adapters

import adapters.primary.web.util.RestGenericException
import adapters.primary.web.util.toRestErrorResponse
import com.github.michaelbull.logging.InlineLogger
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import net.pwall.json.ktor.JSONKtorFunctions.jsonKtor
import ports.output.errors.DomainException
import shared.util.e
import java.util.*

class AppBootstrap(
    application: Application
) {
    private val logger = InlineLogger()

    init {
        application.apply {
            install(AutoHeadResponse)

            // ktor 0.9.5 added MDC support for coroutines and this allows us to print call request id for the entire
            // execution context. This is great, because we can return that call request id back to a client
            // in a header and in case of error, user can provide us with a call request id (let's say we might
            // print it on a screen or in JavaScript console) and we could track the entire execution path even
            // in a very busy logs (e.g. on file system or in Splunk).
            // In order to print call request id we use %X{mdc-callid} specifier in resources/logback.xml
            install(CallLogging) {
                callIdMdc("mdc-callid")
            }
            install(CallId) {
                // Unique id will be generated in form of "callid-UUID" for a CallLogging feature described above
                generate {
                    val requestId = it.request.header(HttpHeaders.XRequestId)
                    if (requestId.isNullOrEmpty()) {
                        "${UUID.randomUUID()}"
                    } else {
                        requestId
                    }
                }
                retrieve { call ->
                    call.request.header(HttpHeaders.XRequestId)
                }
                verify { callId: String ->
                    callId.isNotEmpty()
                }
                // Allows to process the call to modify headers or generate a request from the callId
                reply { call: ApplicationCall, callId: String ->
                    call.response.header(HttpHeaders.XRequestId, callId)
                }
            }

            // Some frameworks such as Angular require additional CORS configuration
            install(CORS) {
                method(HttpMethod.Options)
                method(HttpMethod.Put)
                method(HttpMethod.Delete)
                header("*")
                allowCredentials = true
                allowSameOrigin = true
                anyHost()
            }

            // Content conversions - here we setup serialization and deserialization of JSON objects
            install(ContentNegotiation) {
                jsonKtor()
            }

            // Return proper HTTP error: https://ktor.io/features/status-pages.html
            // In this block we are mapping Domain and Adapter exceptions into proper HTTP error response.
            install(StatusPages) {
                exception<DomainException> { ex ->
                    logger.e("StatusPages/DomainException", ex) { "REST error to be returned to a caller" }
                    val errorResponse = ex.toRestErrorResponse(path = call.request.uri)
                    call.respond(
                        status = HttpStatusCode.fromValue(errorResponse.status),
                        message = errorResponse
                    )
                }
            }
        }
    }
}
