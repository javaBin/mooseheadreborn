package no.java.mooseheadreborn

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DefaultDSLContext
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.*
import javax.sql.DataSource

@Configuration
class JooqConfig {
    @Bean
    fun getDataSource(): DataSource {
        return DataSourceBuilder.create()
            .driverClassName("org.postgresql.Driver")
            .url(ConfigVariable.POSTGRES_URL.readValue())
            .username(ConfigVariable.POSTGRES_USER.readValue())
            .password(ConfigVariable.POSTGRES_PASSWORD.readValue())
            .build()
    }

    @Bean
    fun dslContext(dataSource: DataSource): DSLContext {
        return DefaultDSLContext(dataSource, SQLDialect.POSTGRES)
    }
}