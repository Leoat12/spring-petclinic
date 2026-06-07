package org.springframework.samples.petclinic.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.PetType
import org.springframework.samples.petclinic.owner.Visit
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import java.util.Locale

class ValidatorTests {

    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        LocaleContextHolder.setLocale(Locale.ENGLISH)
        val localValidatorFactoryBean = LocalValidatorFactoryBean()
        localValidatorFactoryBean.afterPropertiesSet()
        validator = localValidatorFactoryBean
    }

    @Test
    fun shouldNotValidateWhenFirstNameEmpty() {
        val person = Person()
        person.firstName = ""
        person.lastName = "smith"

        val constraintViolations: Set<ConstraintViolation<Person>> = validator.validate(person)

        assertThat(constraintViolations).hasSize(1)
        val violation = constraintViolations.iterator().next()
        assertThat(violation.propertyPath).hasToString("firstName")
        assertThat(violation.message).isEqualTo("must not be blank")
    }

    @Test
    fun shouldNotValidateWhenLastNameEmpty() {
        val person = Person()
        person.firstName = "john"
        person.lastName = ""

        val constraintViolations: Set<ConstraintViolation<Person>> = validator.validate(person)

        assertThat(constraintViolations).hasSize(1)
        assertThat(constraintViolations.iterator().next().propertyPath).hasToString("lastName")
    }

    @Test
    fun shouldValidateWhenAllPersonFieldsPresent() {
        val person = Person()
        person.firstName = "John"
        person.lastName = "Smith"

        val constraintViolations: Set<ConstraintViolation<Person>> = validator.validate(person)

        assertThat(constraintViolations).isEmpty()
    }

    @Test
    fun shouldNotValidateOwnerWithEmptyAddress() {
        val owner = Owner()
        owner.firstName = "John"
        owner.lastName = "Smith"
        owner.address = ""
        owner.city = "City"
        owner.telephone = "1234567890"

        val violations: Set<ConstraintViolation<Owner>> = validator.validate(owner)

        assertThat(violations.map { v -> v.propertyPath.toString() }).contains("address")
    }

    @Test
    fun shouldNotValidateOwnerWithEmptyCity() {
        val owner = Owner()
        owner.firstName = "John"
        owner.lastName = "Smith"
        owner.address = "123 Street"
        owner.city = ""
        owner.telephone = "1234567890"

        val violations: Set<ConstraintViolation<Owner>> = validator.validate(owner)

        assertThat(violations.map { v -> v.propertyPath.toString() }).contains("city")
    }

    @Test
    fun shouldNotValidateOwnerWithInvalidTelephone() {
        val owner = Owner()
        owner.firstName = "John"
        owner.lastName = "Smith"
        owner.address = "123 Street"
        owner.city = "City"
        owner.telephone = "abc"

        val violations: Set<ConstraintViolation<Owner>> = validator.validate(owner)

        assertThat(violations.map { v -> v.propertyPath.toString() }).contains("telephone")
    }

    @Test
    fun shouldNotValidateOwnerWithShortTelephone() {
        val owner = Owner()
        owner.firstName = "John"
        owner.lastName = "Smith"
        owner.address = "123 Street"
        owner.city = "City"
        owner.telephone = "12345"

        val violations: Set<ConstraintViolation<Owner>> = validator.validate(owner)

        assertThat(violations.map { v -> v.propertyPath.toString() }).contains("telephone")
    }

    @Test
    fun shouldValidateValidOwner() {
        val owner = Owner()
        owner.firstName = "John"
        owner.lastName = "Smith"
        owner.address = "123 Street"
        owner.city = "City"
        owner.telephone = "1234567890"

        val violations: Set<ConstraintViolation<Owner>> = validator.validate(owner)

        assertThat(violations).isEmpty()
    }

    @Test
    fun shouldNotValidatePetTypeWithEmptyName() {
        val petType = PetType()
        petType.name = ""

        val violations: Set<ConstraintViolation<PetType>> = validator.validate(petType)

        assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().propertyPath).hasToString("name")
    }

    @Test
    fun shouldNotValidateVisitWithBlankDescription() {
        val visit = Visit()
        visit.description = ""

        val violations: Set<ConstraintViolation<Visit>> = validator.validate(visit)

        assertThat(violations.map { v -> v.propertyPath.toString() }).contains("description")
    }

}