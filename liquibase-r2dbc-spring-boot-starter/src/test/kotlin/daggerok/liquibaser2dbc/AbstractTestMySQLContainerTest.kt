package daggerok.liquibaser2dbc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootApplication
@Import(LiquibaseR2dbcAutoConfiguration::class)
internal class AbstractTestMySQLContainerTestApp

@Testcontainers
@SpringBootTest(webEnvironment = NONE)
abstract class AbstractTestMySQLContainerTest {

    companion object {
        data class TestMySQLContainer(val image: String = "mysql:8.0.24") : MySQLContainer<TestMySQLContainer>(image)

        @Container
        val mysqlContainer: TestMySQLContainer = TestMySQLContainer().withDatabaseName("database")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") {
                val host = mysqlContainer.host
                val port = mysqlContainer.getMappedPort(MySQLContainer.MYSQL_PORT)
                val databaseName = mysqlContainer.databaseName
                "r2dbc:mysql://$host:$port/$databaseName"
            }
            registry.add("spring.r2dbc.username", mysqlContainer::getUsername)
            registry.add("spring.r2dbc.password", mysqlContainer::getPassword)
        }
    }
}
