package org.springframework.samples.petclinic.system;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;

class CacheConfigurationTests {

	private final CacheConfiguration configuration = new CacheConfiguration();

	@Test
	void shouldCreateCacheConfigurationCustomizer() {
		JCacheManagerCustomizer customizer = configuration.petclinicCacheConfigurationCustomizer();
		assertThat(customizer).isNotNull();
	}

}