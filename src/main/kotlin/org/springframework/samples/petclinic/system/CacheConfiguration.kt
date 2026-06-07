package org.springframework.samples.petclinic.system

import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.cache.configuration.MutableConfiguration

@Configuration(proxyBeanMethods = false)
@EnableCaching
class CacheConfiguration {

    @Bean
    fun petclinicCacheConfigurationCustomizer(): JCacheManagerCustomizer {
        return JCacheManagerCustomizer { cm -> cm.createCache("vets", cacheConfiguration()) }
    }

    private fun cacheConfiguration(): MutableConfiguration<Any, Any> {
        return MutableConfiguration<Any, Any>().setStatisticsEnabled(true)
    }

}