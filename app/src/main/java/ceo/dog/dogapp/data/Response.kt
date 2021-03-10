package ceo.dog.dogapp.data

import com.squareup.moshi.Json
import retrofit2.HttpException

interface Response {
    val message: Any
    val status: Status
}

data class AllBreedResponse(
    @Json(name = "message") override val message: Map<String, List<String>>,
    @Json(name = "status") override val status: Status
) : Response

data class BreedImagesResponse(
    @Json(name = "message") override val message: List<String>,
    @Json(name = "status") override val status: Status
) : Response

enum class Status {
    @Json(name = "success")
    SUCCESS,

    @Json(name = "error")
    ERROR
}

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> = Resource(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(message: String?): Resource<T> =
            Resource(status = Status.ERROR, data = null, message = message)
    }
}

inline fun <R> call(requestBlock: () -> Response): Resource<R> = try {
    val res = requestBlock.invoke()
    if (res.status == Status.ERROR) throw RuntimeException("Api response error ${res.message as String}")
    Resource.success(res.message as R)
} catch (e: Exception) {
    var message = e.message
    if (e is HttpException) {
        message = "${e.message}"
    }
    Resource.error(message)
}

fun <T, R> Resource<T>.mapValue(mapper: (T?) -> R?): Resource<R> {
    return Resource(
        status = status,
        data = mapper(data),
        message = message
    )
}