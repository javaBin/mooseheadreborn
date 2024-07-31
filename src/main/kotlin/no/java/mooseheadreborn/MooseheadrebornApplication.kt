package no.java.mooseheadreborn

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.web.server.*
import org.springframework.context.annotation.*

@SpringBootApplication
class MooseheadrebornApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Config.validateAndloadConfig(args)
            val application = SpringApplication(MooseheadrebornApplication::class.java)
            application.setAdditionalProfiles(SpringProfile.current().text)
            application.run(*args)
        }
    }

    @Bean
    fun webServerFactoryCustomizer(): WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
        return WebServerFactoryCustomizer { factory: ConfigurableWebServerFactory ->
            factory.setPort(
                Config.getIntValue(ConfigVariable.PORT)
            )
        }
    }

}

