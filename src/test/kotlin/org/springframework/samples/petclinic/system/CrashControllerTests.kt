package org.springframework.samples.petclinic.system

import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class CrashControllerTests {

    private val testee = CrashController()

    @Test
    fun triggerException() {
        assertThatExceptionOfType(RuntimeException::class.java).isThrownBy { testee.triggerException() }
            .withMessageContaining("Expected: controller used to showcase what happens when an exception is thrown")
    }

}