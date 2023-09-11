package daggerok.sample

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = NONE)
abstract class AbstractTestMariaDBContainerTest {

    companion object {
        data class TestMariaDBContainer(val image: String = "mariadb:11.1.2-jammy") : MariaDBContainer<TestMariaDBContainer>(image)

        @Container
        val mysqlContainer: TestMariaDBContainer = TestMariaDBContainer().withDatabaseName("database")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { mysqlContainer.jdbcUrl.replaceFirst("jdbc:", "r2dbc:") }
            registry.add("spring.r2dbc.username", mysqlContainer::getUsername)
            registry.add("spring.r2dbc.password", mysqlContainer::getPassword)
        }
    }
}
