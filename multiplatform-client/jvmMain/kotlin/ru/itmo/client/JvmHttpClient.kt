package ru.itmo.client

import kotlinx.coroutines.future.await
import java.net.URI
import java.net.http.HttpClient as JavaHttpClient
import java.net.http.HttpRequest as JavaHttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse as JavaHttpResponse

class JvmHttpClient : HttpClient {
    private val client =  JavaHttpClient.newHttpClient()
    override suspend fun request(method: HttpMethod, request: HttpRequest): HttpResponse {
        val httpRequestBuilder = JavaHttpRequest.newBuilder()
            .uri(URI.create(request.url))
            .method(
                method.name,
                when (request.body) {
                    null -> BodyPublishers.noBody()
                    else -> BodyPublishers.ofByteArray(request.body)
                }
            )

        request.headers.value.forEach { (key, value) ->
            httpRequestBuilder.header(key, value)
        }

        val httpRequest = httpRequestBuilder.build()

        val response = client.sendAsync(httpRequest, JavaHttpResponse.BodyHandlers.ofByteArray())
            .await()

        return HttpResponse(
            HttpStatus(response.statusCode()),
            HttpHeaders(
                response.headers().map().mapValues { it.value.joinToString { ", " } }
            ),
            response.body()
        )
    }

    override fun close() {
        // auto closable
    }

}