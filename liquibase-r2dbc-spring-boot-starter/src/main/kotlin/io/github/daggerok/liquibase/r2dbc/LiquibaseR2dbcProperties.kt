package io.github.daggerok.liquibase.r2dbc

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("spring.liquibase", ignoreUnknownFields = true)
data class LiquibaseR2dbcProperties @ConstructorBinding constructor(
    @Suppress("UNUSED") val enabled: Boolean = true,
    val changeLog: String = "",
    val defaultSchema: String = "",
    val liquibaseSchema: String = "",
)
