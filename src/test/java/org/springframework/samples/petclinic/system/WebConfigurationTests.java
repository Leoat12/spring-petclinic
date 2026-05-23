package org.springframework.samples.petclinic.system;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

class WebConfigurationTests {

	private final WebConfiguration configuration = new WebConfiguration();

	@Test
	void shouldCreateLocaleResolverWithEnglishDefault() {
		SessionLocaleResolver resolver = (SessionLocaleResolver) configuration.localeResolver();
		assertThat(resolver).isNotNull();
		MockHttpServletRequest request = new MockHttpServletRequest();
		Locale resolved = resolver.resolveLocale(request);
		assertThat(resolved).isEqualTo(Locale.ENGLISH);
	}

	@Test
	void shouldCreateLocaleChangeInterceptorWithLangParam() {
		LocaleChangeInterceptor interceptor = (LocaleChangeInterceptor) configuration.localeChangeInterceptor();
		assertThat(interceptor).isNotNull();
		assertThat(interceptor.getParamName()).isEqualTo("lang");
	}

}