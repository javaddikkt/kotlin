package ru.itmo.client

import kotlinx.coroutines.await
import kotlinx.browser.window
import org.khronos.webgl.Int8Array
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.Promise

class JsHttpClient : HttpClient {
    override suspend fun request(method: HttpMethod, request: HttpRequest): HttpResponse {
        val headers = Headers()
        request.headers.value.forEach { (key, value) ->
            headers.append(key, value)
        }

        val body: dynamic = if (method == HttpMethod.GET) {
            undefined
        } else {
            request.body?.let { Int8Array(it.toTypedArray()).buffer }
        }

        val init = RequestInit(
            method.name,
            headers,
            body
        )

        val response = if (platform == Platform.Node) {
            nodeFetch(request.url, init.asNodeOptions()) as Promise<Response>
        } else {
            window.fetch(request.url, init)
        }.await()

        val responseHeaders = mutableMapOf<String, String>()
        response.headers.asDynamic().forEach { key, value ->
            responseHeaders[key as String] = value as String
            Unit
        }

        val responseBody = Int8Array(response.arrayBuffer().await()).unsafeCast<ByteArray>()

        return HttpResponse(
            HttpStatus(response.status.toInt()),
            HttpHeaders(responseHeaders),
            responseBody
        )
    }

    override fun close() {
        // auto closable
    }
}

private enum class Platform { Node, Browser }

private val platform: Platform
    get() {
        val hasNodeApi = js(
            """
            (typeof process !== 'undefined' 
                && process.versions != null 
                && process.versions.node != null) ||
            (typeof window !== 'undefined' 
                && typeof window.process !== 'undefined' 
                && window.process.versions != null 
                && window.process.versions.node != null)
            """
        ) as Boolean
        return if (hasNodeApi) Platform.Node else Platform.Browser
    }

private val nodeFetch: dynamic
    get() = js("eval('require')('node-fetch')")

private fun RequestInit.asNodeOptions(): dynamic =
    js("Object").assign(js("Object").create(null), this)
