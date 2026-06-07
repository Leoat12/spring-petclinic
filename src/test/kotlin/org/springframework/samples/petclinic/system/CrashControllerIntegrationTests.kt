package org.springframework.samples.petclinic.system

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = ["spring.web.error.include-message=ALWAYS", "management.endpoints.access.default=none"]
)
@AutoConfigureTestRestTemplate
class CrashControllerIntegrationTests {

    @Value("\${local.server.port}")
    private var port: Int = 0

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun triggerExceptionJson() {
        val resp: ResponseEntity<Map<String, Any>> = rest.exchange(
            RequestEntity.get("http://localhost:$port/oups").build(),
            object : ParameterizedTypeReference<Map<String, Any>>() {}
        )
        assertThat(resp).isNotNull()
        assertThat(resp.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(resp.body).containsKey("timestamp")
        assertThat(resp.body).containsKey("status")
        assertThat(resp.body).containsKey("error")
        assertThat(resp.body).containsEntry("message", "Expected: controller used to showcase what happens when an exception is thrown")
        assertThat(resp.body).containsEntry("path", "/oups")
    }

    @Test
    fun triggerExceptionHtml() {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.TEXT_HTML)
        val resp: ResponseEntity<String> = rest.exchange(
            "http://localhost:$port/oups", HttpMethod.GET,
            HttpEntity<Void>(headers), String::class.java
        )
        assertThat(resp).isNotNull()
        assertThat(resp.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(resp.body).isNotNull()
        assertThat(resp.body).containsSubsequence("<body>", "<h2>", "Something happened...", "</h2>", "<p>",
            "Expected:", "controller", "used", "to", "showcase", "what", "happens", "when", "an", "exception", "is",
            "thrown", "</p>", "</body>")
        assertThat(resp.body).doesNotContain("Whitelabel Error Page", "This application has no explicit mapping for")
    }

    @SpringBootApplication(exclude = [DataSourceAutoConfiguration::class, DataSourceTransactionManagerAutoConfiguration::class])
    internal class TestConfiguration

}