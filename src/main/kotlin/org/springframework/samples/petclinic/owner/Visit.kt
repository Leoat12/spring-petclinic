package org.springframework.samples.petclinic.owner

import java.time.LocalDate

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.samples.petclinic.model.BaseEntity

import jakarta.validation.constraints.NotBlank

open class Visit : BaseEntity() {

 @DateTimeFormat(pattern = "yyyy-MM-dd")
 var date: LocalDate = LocalDate.now()

 @NotBlank
 var description: String? = null

}