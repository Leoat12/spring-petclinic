package org.springframework.samples.petclinic.owner

import java.util.ArrayList

import org.springframework.core.style.ToStringCreator
import org.springframework.samples.petclinic.model.Person
import org.springframework.util.Assert

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

open class Owner : Person() {

 @NotBlank
 var address: String? = null

 @NotBlank
 var city: String? = null

 @NotBlank
 @Pattern(regexp = "\\d{10}", message = "{telephone.invalid}")
 var telephone: String? = null

 private val _pets: MutableList<Pet> = ArrayList()

 fun getPets(): MutableList<Pet> = _pets

 fun addPet(pet: Pet) {
  if (pet.isNew()) {
   _pets.add(pet)
  }
 }

 fun getPet(name: String): Pet? = getPet(name, false)

 fun getPet(id: Int): Pet? {
  for (pet in _pets) {
   if (!pet.isNew() && pet.id == id) {
    return pet
   }
  }
  return null
 }

 fun getPet(name: String, ignoreNew: Boolean): Pet? {
  for (pet in _pets) {
   val compName = pet.name
   if (compName != null && compName.equals(name, ignoreCase = true)) {
    if (!ignoreNew || !pet.isNew()) {
     return pet
    }
   }
  }
  return null
 }

 override fun toString(): String = ToStringCreator(this)
  .append("id", id)
  .append("new", isNew())
  .append("lastName", lastName)
  .append("firstName", firstName)
  .append("address", address)
  .append("city", city)
  .append("telephone", telephone)
  .toString()

 fun addVisit(petId: Int?, visit: Visit?) {
  Assert.notNull(petId, "Pet identifier must not be null!")
  Assert.notNull(visit, "Visit must not be null!")
  val pet = getPet(petId!!)
  Assert.notNull(pet, "Invalid Pet identifier!")
  pet!!.addVisit(visit!!)
 }

}