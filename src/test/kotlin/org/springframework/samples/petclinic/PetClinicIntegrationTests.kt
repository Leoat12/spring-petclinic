package org.springframework.samples.petclinic

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.restclient.RestTemplateBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.vet.VetRepository
import org.springframework.web.client.RestTemplate

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = ["logging.level.sql=DEBUG"])
class PetClinicIntegrationTests {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var vets: VetRepository

    @Autowired
    private lateinit var builder: RestTemplateBuilder

    @Test
    fun findAll() {
        vets.findAll()
        vets.findAll()
    }

    @Test
    fun ownerDetails() {
        val template: RestTemplate = builder.rootUri("http://localhost:$port").build()
        val result: ResponseEntity<String> = template.exchange(RequestEntity.get("/owners/1").build(), String::class.java)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun ownerList() {
        val template: RestTemplate = builder.rootUri("http://localhost:$port").build()
        val result: ResponseEntity<String> = template.exchange(RequestEntity.get("/owners?lastName=").build(), String::class.java)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    }

}