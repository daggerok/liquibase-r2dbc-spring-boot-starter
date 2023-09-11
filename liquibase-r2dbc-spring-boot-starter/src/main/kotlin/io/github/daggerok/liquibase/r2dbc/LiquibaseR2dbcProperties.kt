package io.github.daggerok.liquibase.r2dbc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.liquibase", ignoreUnknownFields = true)
data class LiquibaseR2dbcProperties(
    @Suppress("UNUSED") val enabled: Boolean = true,
    val changeLog: String = "",
    val defaultSchema: String = "",
    val liquibaseSchema: String = "",
)
