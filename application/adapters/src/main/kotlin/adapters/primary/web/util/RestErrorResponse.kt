package adapters.primary.web.util

import io.ktor.http.*
import ports.output.errors.DomainException
import ports.output.errors.ResourceNotFoundException

data class RestErrorResponse(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String?,
    val specifics: Map<String, Any?>? = null
)

internal fun DomainException.toRestErrorResponse(
    path: String
) = RestErrorResponse(
    type = errorType,
    title = title,
    status = guessHttpStatusCode().value,
    detail = detail,
    instance = path
)

private fun DomainException.guessHttpStatusCode(): HttpStatusCode =
    when (this) {
        is ResourceNotFoundException -> HttpStatusCode.NotFound
        else -> HttpStatusCode.InternalServerError
    }