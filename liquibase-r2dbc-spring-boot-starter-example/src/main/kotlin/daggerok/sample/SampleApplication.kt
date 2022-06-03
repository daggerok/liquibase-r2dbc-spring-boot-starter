package daggerok.sample

import java.time.Instant
import org.apache.logging.log4j.kotlin.logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.error.ErrorAttributeOptions.Include
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.relational.core.mapping.Table
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux

@EnableR2dbcAuditing
@SpringBootApplication
class SampleApplication

fun main(args: Array<String>) {
    runApplication<SampleApplication>(*args)
}

@Table("messages")
data class Message(
    val body: String = "",
    val addressTo: String = "all",
    val addressFrom: String = "anonymous",
    @Id val id: Long? = null,
    @LastModifiedDate
    @DateTimeFormat(iso = DATE_TIME)
    val sentAt: Instant? = null,
)

interface Messages : R2dbcRepository<Message, Long> {
    fun findAllByOrderByIdDesc(): Flux<Message>
}

@Configuration
class RoutesConfig(private val messages: Messages) {

    @Bean
    fun routes() = router {
        contentType(MediaType.APPLICATION_JSON)
        "/api".nest {
            GET("/messages") {
                ServerResponse.ok().body(
                    messages.findAllByOrderByIdDesc()
                        .doOnNext { log.info { "getting: $it" } }
                )
            }
            POST("/messages") {
                ServerResponse.ok().body(
                    it.bodyToMono<Message>()
                        .doOnNext { log.info { "posting: $it" } }
                        .flatMap(messages::save)
                )
            }
            path("/**") {
                val baseUrl = it.uri().toURL().run { "$protocol://$authority" }
                ServerResponse.ok().bodyValue(
                    mapOf(
                        "Save messages => http POST " to "$baseUrl/api/messages",
                        "Get all messages => http GET " to "$baseUrl/api/messages",
                    )
                )
            }
        }
    }

    private companion object {
        val log = logger()
    }
}

/**
 * This component customizes error response.
 *
 * Adds `api` map with supported endpoints
 */
@Component
class RestApiErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest?, options: ErrorAttributeOptions): MutableMap<String, Any> =
        super.getErrorAttributes(request, options.including(Include.MESSAGE, Include.EXCEPTION)).apply {
            val baseUrl = request?.uri()?.let { "${it.scheme}://${it.authority}" } ?: ""
            val api = mapOf(
                "Save messages => http POST " to "$baseUrl/api/messages",
                "Get all messages => http GET " to "$baseUrl/api/messages",
            )
            put("api", api)
        }
}
