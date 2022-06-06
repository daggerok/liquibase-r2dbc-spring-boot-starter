package daggerok.sample

import org.apache.logging.log4j.kotlin.logger
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

class PostgresApplicationTest(@Autowired val messages: Messages) : AbstractTestPostgreSQLContainerTest() {

    @Test
    fun `should create message`() {
        // given
        messages.deleteAll().subscribe {
            log.info { "messages table cleanup..." }
        }

        // when
        StepVerifier.create(messages.save(Message(body = "Hello, World")))
            .expectNextCount(1)
            .verifyComplete()

        // then
        StepVerifier.create(messages.findAll())
            .consumeNextWith { log.info { it } }
            .verifyComplete()
    }

    companion object {
        val log = logger()
    }
}
