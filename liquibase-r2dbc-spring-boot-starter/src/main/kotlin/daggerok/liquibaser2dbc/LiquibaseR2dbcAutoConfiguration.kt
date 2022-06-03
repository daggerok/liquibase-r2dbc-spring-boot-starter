package daggerok.liquibaser2dbc

import javax.sql.DataSource
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.Database
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.apache.logging.log4j.kotlin.logger
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader

@Configuration
@ConditionalOnProperty(
    prefix = "spring.liquibase",
    name = ["enabled"], havingValue = "true", matchIfMissing = true,
)
@AutoConfigureAfter(R2dbcAutoConfiguration::class)
@ComponentScan(basePackageClasses = [R2dbcProperties::class])
@EnableConfigurationProperties(LiquibaseR2dbcProperties::class)
class LiquibaseR2dbcAutoConfiguration(private val props: LiquibaseR2dbcProperties) {

    @Bean
    @ConditionalOnMissingBean
    fun liquibaseR2dbcResourceAccessor(resourceLoader: ResourceLoader): ClassLoaderResourceAccessor =
        ClassLoaderResourceAccessor(resourceLoader.classLoader)
            .also { log.debug { "liquibaseR2dbcResourceAccessor bean refers to: $it" } }

    @Bean
    @ConditionalOnMissingBean
    fun liquibaseR2dbcDataSource(r2dbcProperties: R2dbcProperties): DataSource =
        DataSourceBuilder.create()
            .url(
                r2dbcProperties.url.replaceFirst("r2dbc:pool:", "jdbc:")
                    .replaceFirst("r2dbc:", "jdbc:")
            )
            .username(r2dbcProperties.username)
            .password(r2dbcProperties.password)
            .build()
            .also { log.debug { "liquibaseR2dbcDataSource bean refers to: $it" } }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "close")
    fun liquibaseR2dbcJdbcConnection(liquibaseR2dbcDataSource: DataSource): JdbcConnection =
        JdbcConnection(liquibaseR2dbcDataSource.connection)
            .also { log.debug { "liquibaseR2dbcJdbcConnection bean refers to: $it" } }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "close")
    fun liquibaseR2dbcDatabase(liquibaseR2dbcJdbcConnection: JdbcConnection): Database =
        DatabaseFactory.getInstance().findCorrectDatabaseImplementation(liquibaseR2dbcJdbcConnection)
            .also { log.debug { "liquibaseR2dbcDatabase bean refers to: $it" } }

    @Bean
    @ConditionalOnMissingBean
    fun liquibaseR2dbcDatabaseInitialization(liquibaseR2dbcResourceAccessor: ClassLoaderResourceAccessor, liquibaseR2dbcDatabase: Database) =
        ApplicationRunner {
            Liquibase(props.changeLog, liquibaseR2dbcResourceAccessor, liquibaseR2dbcDatabase)
                .also { log.debug { "liquibaseR2dbcDatabaseInitialization bean refers to: $it" } }
                .use {
                    it.update(Contexts(), LabelExpression(), true)
                    log.debug { "R2DBC liquibase database initialized by ${it.changeLogFile}" }
                }
        }

    private companion object {
        val log = logger()
    }
}
