package org.springframework.samples.petclinic.vet

import java.util.HashSet

import jakarta.xml.bind.annotation.XmlElement

import org.springframework.samples.petclinic.model.NamedEntity
import org.springframework.samples.petclinic.model.Person

open class Vet : Person() {

 private var specialtiesInternal: MutableSet<Specialty>? = null

 private fun getSpecialtiesInternal(): MutableSet<Specialty> {
  if (specialtiesInternal == null) {
   specialtiesInternal = HashSet()
  }
  return specialtiesInternal!!
 }

 @XmlElement
 fun getSpecialties(): List<Specialty> =
  getSpecialtiesInternal().sortedBy { it.name }

 fun getNrOfSpecialties(): Int = getSpecialtiesInternal().size

 fun addSpecialty(specialty: Specialty) {
  getSpecialtiesInternal().add(specialty)
 }

}