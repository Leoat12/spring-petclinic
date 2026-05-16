/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

class ValidatorTests {

	private Validator validator;

	@BeforeEach
	void setUp() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		validator = localValidatorFactoryBean;
	}

	@Test
	void shouldNotValidateWhenFirstNameEmpty() {
		Person person = new Person();
		person.setFirstName("");
		person.setLastName("smith");

		Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Person> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath()).hasToString("firstName");
		assertThat(violation.getMessage()).isEqualTo("must not be blank");
	}

	@Test
	void shouldNotValidateWhenLastNameEmpty() {
		Person person = new Person();
		person.setFirstName("john");
		person.setLastName("");

		Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("lastName");
	}

	@Test
	void shouldValidateWhenAllPersonFieldsPresent() {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Smith");

		Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);

		assertThat(constraintViolations).isEmpty();
	}

	@Test
	void shouldNotValidateOwnerWithEmptyAddress() {
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Smith");
		owner.setAddress("");
		owner.setCity("City");
		owner.setTelephone("1234567890");

		Set<ConstraintViolation<Owner>> violations = validator.validate(owner);

		assertThat(violations.stream().map(v -> v.getPropertyPath().toString())).contains("address");
	}

	@Test
	void shouldNotValidateOwnerWithEmptyCity() {
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Smith");
		owner.setAddress("123 Street");
		owner.setCity("");
		owner.setTelephone("1234567890");

		Set<ConstraintViolation<Owner>> violations = validator.validate(owner);

		assertThat(violations.stream().map(v -> v.getPropertyPath().toString())).contains("city");
	}

	@Test
	void shouldNotValidateOwnerWithInvalidTelephone() {
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Smith");
		owner.setAddress("123 Street");
		owner.setCity("City");
		owner.setTelephone("abc");

		Set<ConstraintViolation<Owner>> violations = validator.validate(owner);

		assertThat(violations.stream().map(v -> v.getPropertyPath().toString())).contains("telephone");
	}

	@Test
	void shouldNotValidateOwnerWithShortTelephone() {
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Smith");
		owner.setAddress("123 Street");
		owner.setCity("City");
		owner.setTelephone("12345");

		Set<ConstraintViolation<Owner>> violations = validator.validate(owner);

		assertThat(violations.stream().map(v -> v.getPropertyPath().toString())).contains("telephone");
	}

	@Test
	void shouldValidateValidOwner() {
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Smith");
		owner.setAddress("123 Street");
		owner.setCity("City");
		owner.setTelephone("1234567890");

		Set<ConstraintViolation<Owner>> violations = validator.validate(owner);

		assertThat(violations).isEmpty();
	}

	@Test
	void shouldNotValidatePetTypeWithEmptyName() {
		PetType petType = new PetType();
		petType.setName("");

		Set<ConstraintViolation<PetType>> violations = validator.validate(petType);

		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getPropertyPath()).hasToString("name");
	}

	@Test
	void shouldNotValidateVisitWithBlankDescription() {
		Visit visit = new Visit();
		visit.setDescription("");

		Set<ConstraintViolation<Visit>> violations = validator.validate(visit);

		assertThat(violations.stream().map(v -> v.getPropertyPath().toString())).contains("description");
	}

}
