package org.springframework.samples.petclinic.owner

import java.time.LocalDate
import java.util.LinkedHashSet

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.samples.petclinic.model.NamedEntity

open class Pet : NamedEntity() {

 @DateTimeFormat(pattern = "yyyy-MM-dd")
 var birthDate: LocalDate? = null

 var typeId: Int? = null

 var type: PetType? = null
  set(value) {
   field = value
   if (value != null) {
    typeId = value.id
   }
  }

 private val _visits: MutableSet<Visit> = LinkedHashSet()

 fun getVisits(): MutableSet<Visit> = _visits

 fun addVisit(visit: Visit) {
  _visits.add(visit)
 }

}