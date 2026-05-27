package org.springframework.samples.petclinic.model

import jakarta.validation.constraints.NotBlank

open class NamedEntity : BaseEntity() {

 @NotBlank
 var name: String? = null

 override fun toString(): String = name ?: "<null>"

}