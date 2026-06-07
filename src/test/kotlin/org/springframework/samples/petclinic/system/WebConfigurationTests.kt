package org.springframework.samples.petclinic.system

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.Locale

class WebConfigurationTests {

    private val configuration = WebConfiguration()

    @Test
    fun shouldCreateLocaleResolverWithEnglishDefault() {
        val resolver = configuration.localeResolver() as SessionLocaleResolver
        assertThat(resolver).isNotNull()
        val request = MockHttpServletRequest()
        val resolved = resolver.resolveLocale(request)
        assertThat(resolved).isEqualTo(Locale.ENGLISH)
    }

    @Test
    fun shouldCreateLocaleChangeInterceptorWithLangParam() {
        val interceptor = configuration.localeChangeInterceptor() as LocaleChangeInterceptor
        assertThat(interceptor).isNotNull()
        assertThat(interceptor.paramName).isEqualTo("lang")
    }

}