package io.github.daggerok.liquibase.r2dbc

import javax.sql.DataSource
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.Database
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.integration.spring.SpringResourceAccessor
import org.apache.logging.log4j.kotlin.logger
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader

/**
 * Liquibase R2DBC spring-boot starter
 * @since 0.0.1
 */
@Configuration
@ConditionalOnProperty(
    prefix = "spring.liquibase",
    name = ["enabled"], havingValue = "true", matchIfMissing = true,
)
@ConditionalOnBean(DataSource::class)
@ComponentScan(basePackageClasses = [R2dbcProperties::class])
@EnableConfigurationProperties(LiquibaseR2dbcProperties::class)
@AutoConfigureAfter(R2dbcAutoConfiguration::class, DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class)
class LiquibaseR2dbcAutoConfiguration(private val props: LiquibaseR2dbcProperties) {

    @Bean
    @ConditionalOnMissingBean
    fun liquibaseR2dbcResourceAccessor(resourceLoader: ResourceLoader): SpringResourceAccessor =
        SpringResourceAccessor(resourceLoader)
            .also { log.debug { "liquibaseR2dbcResourceAccessor bean refers to: $it" } }

    @Bean
    @ConditionalOnMissingBean
    fun liquibaseR2dbcDataSource(r2dbcProperties: R2dbcProperties): DataSource =
        DataSourceBuilder.create()
            .url(
                when {
                    // Proxy, see: http://r2dbc.io/r2dbc-proxy/docs/current/docs/html/#setup_connection-factory-discovery_url-based
                    r2dbcProperties.url.startsWith("r2dbc:proxy:", ignoreCase = true) ->
                        r2dbcProperties.url.lowercase()
                            .replaceFirst("r2dbc:proxy:pool:", "jdbc:")
                            .replaceFirst("r2dbc:proxy:", "jdbc:")
                    // MySQL, MariaDB, Postgresql, MS SQL Server
                    listOf("r2dbc:mysql://", "r2dbc:mariadb://", "r2dbc:postgresql://", "r2dbc:mssql://",
                        "r2dbc:pool:mysql://", "r2dbc:pool:mariadb://", "r2dbc:pool:postgresql://", "r2dbc:pool:mssql://")
                        .any { r2dbcProperties.url.startsWith(it, ignoreCase = true) } ->
                        r2dbcProperties.url.lowercase()
                            .replaceFirst("r2dbc:pool:mssql://", "jdbc:sqlserver://")
                            .replaceFirst("r2dbc:mssql://", "jdbc:sqlserver://")
                            .replaceFirst("r2dbc:pool:", "jdbc:")
                            .replaceFirst("r2dbc:", "jdbc:")
                    // H2, see: https://github.com/r2dbc/r2dbc-h2#getting-started
                    r2dbcProperties.url.startsWith("r2dbc:h2:mem:///", ignoreCase = true) ->
                        r2dbcProperties.url.lowercase()
                            .replaceFirst("r2dbc:h2:mem:///", "jdbc:h2:mem:")
                            .apply { "$this;${r2dbcProperties.toH2QueryParams()}" }
                    r2dbcProperties.url.startsWith("r2dbc:h2:file:///", ignoreCase = true) ->
                        r2dbcProperties.url.lowercase()
                            .replaceFirst("r2dbc:h2:file:///", "jdbc:h2:file:")
                            .apply { "$this;${r2dbcProperties.toH2QueryParams()}" }
                    r2dbcProperties.url.startsWith("r2dbc:h2:tcp", ignoreCase = true) ->
                        r2dbcProperties.url.lowercase()
                            .replaceFirst("r2dbc:", "jdbc:")
                            .apply { "$this;${r2dbcProperties.toH2QueryParams()}" }
                    else -> throw LiquibaseR2dbcNotSupportedException(r2dbcProperties.url)
                }
            )
            .username(r2dbcProperties.username)
            .password(r2dbcProperties.password)
            .build()
            .also { log.debug { "liquibaseR2dbcDataSource bean refers to: $it. Liquibase JDBC URL: ${it.connection.metaData.url}" } }

    private fun R2dbcProperties.toH2QueryParams(): String =
        properties.entries.joinToString(separator = ";") {
            "${it.key}=${it.value}"
        }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "close")
    fun liquibaseR2dbcJdbcConnection(liquibaseR2dbcDataSource: DataSource): JdbcConnection =
        JdbcConnection(liquibaseR2dbcDataSource.connection)
            .also { log.debug { "liquibaseR2dbcJdbcConnection bean refers to: $it" } }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "close")
    fun liquibaseR2dbcDatabase(liquibaseR2dbcJdbcConnection: JdbcConnection): Database =
        DatabaseFactory.getInstance().findCorrectDatabaseImplementation(liquibaseR2dbcJdbcConnection)
            .apply { if (props.liquibaseSchema.isNotBlank()) liquibaseSchemaName = props.liquibaseSchema }
            .apply { if (props.defaultSchema.isNotBlank()) defaultSchemaName = props.defaultSchema }
            .also { log.debug { "liquibaseR2dbcDatabase bean refers to: $it" } }

    @Bean
    @ConditionalOnMissingBean
    fun liquibaseR2dbcUpdate(liquibaseR2dbcResourceAccessor: SpringResourceAccessor, liquibaseR2dbcDatabase: Database) =
        ApplicationRunner {
            Liquibase(props.changeLog, liquibaseR2dbcResourceAccessor, liquibaseR2dbcDatabase)
                .also { log.debug { "liquibaseR2dbcUpdate bean refers to: $it" } }
                .use {
                    it.update(Contexts(), LabelExpression(), true)
                    log.info { "R2DBC liquibase update ${it.changeLogFile} completed" }
                }
        }

    private companion object {
        val log = logger()
    }
}
