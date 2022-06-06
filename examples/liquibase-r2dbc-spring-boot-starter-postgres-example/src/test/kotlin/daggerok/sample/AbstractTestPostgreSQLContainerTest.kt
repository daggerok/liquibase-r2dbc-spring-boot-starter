package daggerok.sample

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = NONE)
abstract class AbstractTestPostgreSQLContainerTest {

    companion object {
        data class TestPostgreSQLContainer(val image: String = "postgres:14.3-alpine3.16") : PostgreSQLContainer<TestPostgreSQLContainer>(image)

        @Container
        val mysqlContainer: TestPostgreSQLContainer = TestPostgreSQLContainer().withDatabaseName("database")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { mysqlContainer.jdbcUrl.replaceFirst("jdbc:", "r2dbc:") }
            registry.add("spring.r2dbc.username", mysqlContainer::getUsername)
            registry.add("spring.r2dbc.password", mysqlContainer::getPassword)
        }
    }
}
