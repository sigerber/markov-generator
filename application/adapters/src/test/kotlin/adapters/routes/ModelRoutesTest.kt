package adapters.routes

import adapters.IntegrationTestContext.withApp
import adapters.config.AppConfig
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import org.koin.ktor.ext.inject
import ports.input.util.DateSupplier
import ports.output.healthcheck.HealthCheckResponseDto
import ports.provides.ModelManagerPort

class ModelRoutesTest : DescribeSpec({

    withApp {
        val modelManager: ModelManagerPort by application.inject()

        beforeEach {
            reset(modelManager)
        }

        describe("HTTP POST /model") {
            context("when no models exist") {
                it("creates a model") {

                    with(handleRequest(HttpMethod.Post, "/model")) {
                        response.status() shouldBe HttpStatusCode.OK

                        verify(modelManager).createModel()
                    }
                }
            }
        }
    }
})
