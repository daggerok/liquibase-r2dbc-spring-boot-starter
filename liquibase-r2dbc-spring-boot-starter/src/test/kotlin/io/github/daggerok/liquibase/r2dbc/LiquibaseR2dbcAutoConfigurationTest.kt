package io.github.daggerok.liquibase.r2dbc

import java.nio.file.Files
import java.nio.file.Paths
import org.apache.logging.log4j.kotlin.logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties

class LiquibaseR2dbcAutoConfigurationTest {

    @Test
    fun `should support h2 relative file`() {
        // given
        runCatching { Files.deleteIfExists(Paths.get("target", "test")) }
            .onFailure { log.error { "wasn't able to remove ./target/test file..." } }
            .onSuccess { log.info { "file ./target/test removed." } }

        // and
        val liquibaseR2dbcProperties = LiquibaseR2dbcProperties()
        val liquibaseR2dbcAutoConfiguration = LiquibaseR2dbcAutoConfiguration(liquibaseR2dbcProperties)
        val r2dbcProperties = R2dbcProperties().apply {
            url = "r2dbc:h2:file:///./target/test"
        }

        // when
        val dataSource = liquibaseR2dbcAutoConfiguration.liquibaseR2dbcDataSource(r2dbcProperties)

        // then
        assertThat(dataSource.connection.metaData.url).isEqualTo("jdbc:h2:file:./target/test")
    }

    @Test
    fun `should support h2 absolute file`() {
        // given
        runCatching { Files.deleteIfExists(Paths.get("/tmp", "test")) }
            .onFailure { log.error { "wasn't able to remove /tmp/test file..." } }
            .onSuccess { log.info { "file /tmp/test removed." } }

        // and
        val liquibaseR2dbcProperties = LiquibaseR2dbcProperties()
        val liquibaseR2dbcAutoConfiguration = LiquibaseR2dbcAutoConfiguration(liquibaseR2dbcProperties)
        val r2dbcProperties = R2dbcProperties().apply {
            url = "r2dbc:h2:file:////tmp/test"
        }

        // when
        val dataSource = liquibaseR2dbcAutoConfiguration.liquibaseR2dbcDataSource(r2dbcProperties)

        // then
        assertThat(dataSource.connection.metaData.url).isEqualTo("jdbc:h2:file:/tmp/test")
    }

    @Test
    fun `should support h2 mem`() {
        val liquibaseR2dbcProperties = LiquibaseR2dbcProperties()
        val liquibaseR2dbcAutoConfiguration = LiquibaseR2dbcAutoConfiguration(liquibaseR2dbcProperties)
        val r2dbcProperties = R2dbcProperties().apply {
            url = "r2dbc:h2:mem:///test"
        }

        // when
        val dataSource = liquibaseR2dbcAutoConfiguration.liquibaseR2dbcDataSource(r2dbcProperties)

        // then
        assertThat(dataSource.connection.metaData.url).isEqualTo("jdbc:h2:mem:test")
    }

    @Test
    fun `should fail on not supported R2DBC URL`() {
        // given
        val liquibaseR2dbcProperties = LiquibaseR2dbcProperties()
        val liquibaseR2dbcAutoConfiguration = LiquibaseR2dbcAutoConfiguration(liquibaseR2dbcProperties)
        val r2dbcProperties = R2dbcProperties().apply {
            url = "r2dbc:driver:unknown:///test"
        }

        // when
        val exception = assertThrows<LiquibaseR2dbcNotSupportedException> {
            liquibaseR2dbcAutoConfiguration.liquibaseR2dbcDataSource(r2dbcProperties)
        }

        // then
        log.info { "exception: $exception" }
        assertThat(exception).hasMessageContaining(
            "Liquibase R2DBC spring-boot starter doesn't support",
            "r2dbc:driver:unknown:///test", "URL yet",
            "Please contribute or rise new issue",
        )
    }

    companion object {
        val log = logger()
    }
}
