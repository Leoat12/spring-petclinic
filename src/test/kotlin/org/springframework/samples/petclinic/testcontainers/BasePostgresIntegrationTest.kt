package org.springframework.samples.petclinic.testcontainers

import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.junit.jupiter.api.Assumptions.assumeTrue

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("postgres")
@AutoConfigureTestRestTemplate
@Testcontainers
abstract class BasePostgresIntegrationTest {

    companion object {
        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:18.3")

        @BeforeAll
        @JvmStatic
        fun dockerAvailable() {
            assumeTrue(org.testcontainers.DockerClientFactory.instance().isDockerAvailable, "Docker not available")
        }
    }

}