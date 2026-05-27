package org.springframework.samples.petclinic.model

import java.io.Serializable

open class BaseEntity : Serializable {

 var id: Int? = null

 fun isNew(): Boolean = id == null

}