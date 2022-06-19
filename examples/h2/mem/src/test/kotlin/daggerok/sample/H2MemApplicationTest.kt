package daggerok.sample

import org.apache.logging.log4j.kotlin.logger
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import reactor.test.StepVerifier

@SpringBootTest(webEnvironment = NONE)
class H2MemApplicationTest(@Autowired val messages: Messages) {

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
