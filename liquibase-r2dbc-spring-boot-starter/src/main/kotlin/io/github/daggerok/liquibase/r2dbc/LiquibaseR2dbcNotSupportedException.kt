package io.github.daggerok.liquibase.r2dbc

class LiquibaseR2dbcNotSupportedException(url: String) : RuntimeException(
    "Liquibase R2DBC spring-boot starter doesn't support ${url.substringBefore("://")} yet. Please rise and issue here: " +
            "https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter/issues/new"
)
