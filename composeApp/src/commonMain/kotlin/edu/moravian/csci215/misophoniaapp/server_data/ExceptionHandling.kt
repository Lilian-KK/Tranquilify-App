import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import kotlinx.serialization.Serializable
import kotlin.jvm.Transient

@Serializable
data class ErrorResponse(val code: String, val message: String)

open class RequestException(
    message: String, val status: HttpStatusCode? = null,
    @Transient val response: HttpResponse? = null
): IllegalStateException(if (status !== null) status.value.toString() + ": " + message else message)

class BadRequestException(message: String, response: HttpResponse? = null): // 400
    RequestException(message, HttpStatusCode.BadRequest, response)

class UnauthorizedException(message: String, response: HttpResponse? = null): // 401
    RequestException(message, HttpStatusCode.Unauthorized, response)

class ForbiddenException(message: String, response: HttpResponse? = null): // 403
    RequestException(message, HttpStatusCode.Forbidden, response)

class NotFoundException(message: String, response: HttpResponse? = null): // 404
    RequestException(message, HttpStatusCode.NotFound, response)

class RequestTimeoutException(message: String, response: HttpResponse? = null): // 408
    RequestException(message, HttpStatusCode.RequestTimeout, response)

class ConflictException(message: String, response: HttpResponse? = null): // 409
    RequestException(message, HttpStatusCode.Conflict, response)

class TooManyRequestsException(message: String, response: HttpResponse? = null): // 429
    RequestException(message, HttpStatusCode.TooManyRequests, response)

@Suppress("UNUSED_PARAMETER")
suspend fun responseValidator(exception: Throwable, request: HttpRequest) {
    val clientException = exception as? ClientRequestException ?: throw exception // all other exceptions (like 5xx) are propagated as-is
    val response = clientException.response
    val message = try { response.body<ErrorResponse>().message }
    catch (ex: JsonConvertException) { response.bodyAsText() }
    catch (ex: NoTransformationFoundException) { response.bodyAsText() }
    println("message: " + message)
    when (response.status) {
        HttpStatusCode.BadRequest -> throw BadRequestException(message, response)
        HttpStatusCode.Unauthorized -> throw UnauthorizedException(message, response)
        HttpStatusCode.Forbidden -> throw ForbiddenException(message, response)
        HttpStatusCode.NotFound -> throw NotFoundException(message, response)
        HttpStatusCode.Conflict -> throw ConflictException(message, response)
        HttpStatusCode.RequestTimeout -> throw RequestTimeoutException(message, response)
        HttpStatusCode.TooManyRequests -> throw TooManyRequestsException(message, response)
        else -> throw RequestException(message, response.status, response)
    }
}

