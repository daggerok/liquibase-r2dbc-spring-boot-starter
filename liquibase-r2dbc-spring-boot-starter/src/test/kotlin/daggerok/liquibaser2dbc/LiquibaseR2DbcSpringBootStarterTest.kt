package daggerok.liquibaser2dbc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient

class LiquibaseR2DbcSpringBootStarterTest(@Autowired val databaseClient: DatabaseClient) : AbstractTestMySQLContainerTest() {

    @Test
    fun `should test context`() {
        assertThat(databaseClient).isNotNull
    }
}
