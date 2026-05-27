package org.springframework.samples.petclinic.model

import jakarta.validation.constraints.NotBlank

open class Person : BaseEntity() {

 @NotBlank
 var firstName: String? = null

 @NotBlank
 var lastName: String? = null

}