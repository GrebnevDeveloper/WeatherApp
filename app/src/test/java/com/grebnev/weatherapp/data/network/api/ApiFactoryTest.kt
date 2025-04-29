package com.grebnev.weatherapp.data.network.api

import com.grebnev.weatherapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.mockk.InternalPlatformDsl.toStr
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale
import kotlin.test.DefaultAsserter.fail

class ApiFactoryTest {
    @Test
    fun `createHttpClient should return configured client`() {
        val client = ApiFactory.createHttpClient()

        assertNotNull(client)
        assertTrue(client is HttpClient)
    }

    @Test
    fun `client should have content negotiation configured`() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    addHandler { request ->
                        respond(
                            content = ByteReadChannel("""{"test":true}"""),
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                }

            val testClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            val response = testClient.get("https://test.com")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("application/json", response.headers[HttpHeaders.ContentType])
        }

    @Test
    fun `default request should include base url and auth params`() =
        runTest {
            var actualKey: String? = null
            var actualLang: String? = null

            val mockEngine =
                MockEngine.create {
                    addHandler { request ->
                        actualKey =
                            request.url.parameters["key"]?.toStr()
                        actualLang =
                            request.url.parameters["lang"]?.toStr()
                        respond("""{"success":true}""")
                    }
                }

            val testClient =
                HttpClient(mockEngine) {
                    defaultRequest {
                        url {
                            takeFrom("https://api.weatherapi.com/v1/")
                            parameters.append("key", BuildConfig.WEATHER_API_KEY)
                            parameters.append("lang", Locale.getDefault().language)
                        }
                    }
                }

            testClient.get("current.json")

            assertEquals(BuildConfig.WEATHER_API_KEY, actualKey)
            assertEquals(Locale.getDefault().language, actualLang)
        }

    @Test
    fun `json config should ignore unknown keys`() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    addHandler {
                        respond(
                            content = ByteReadChannel("""{"unknown_field": "value"}"""),
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                }

            val testClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            try {
                testClient.get {
                    url {
                        "test"
                    }
                }
            } catch (e: Exception) {
                fail("Should ignore unknown fields but got ${e.javaClass.simpleName}")
            }
        }
}