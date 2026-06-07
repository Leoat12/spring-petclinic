package org.springframework.samples.petclinic.system

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer

class CacheConfigurationTests {

    private val configuration = CacheConfiguration()

    @Test
    fun shouldCreateCacheConfigurationCustomizer() {
        val customizer = configuration.petclinicCacheConfigurationCustomizer()
        assertThat(customizer).isNotNull()
    }

}