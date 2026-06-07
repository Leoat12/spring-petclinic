package org.springframework.samples.petclinic

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ImportRuntimeHints

@SpringBootApplication
@ImportRuntimeHints(PetClinicRuntimeHints::class)
class PetClinicApplication

fun main(args: Array<String>) {
    SpringApplication.run(PetClinicApplication::class.java, *args)
}