package io.github.daggerok.liquibase.r2dbc

class LiquibaseR2dbcNotSupportedException(url: String) : RuntimeException(
    "Liquibase R2DBC spring-boot starter doesn't support '$url' R2DBC URL yet. " +
            "Please contribute or rise new issue: https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter/issues/new"
)
