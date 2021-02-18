package adapters.routes

import adapters.IntegrationTestContext.withApp
import adapters.config.AppConfig
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import org.koin.ktor.ext.inject
import ports.input.util.DateSupplier
import ports.output.healthcheck.HealthCheckResponseDto

class HealthCheckRouteTest : DescribeSpec({

    describe("HTTP GET /health") {
        context("when application is healthy") {
            it("returns healthy status") {
                withApp {
                    with(handleRequest(HttpMethod.Get, "/health")) {
                        val dateSupplier: DateSupplier by application.inject()
                        val appConfig: AppConfig by application.inject()
                        val status = response.status()
                        status shouldBe HttpStatusCode.OK
                        val healthCheckResponse: HealthCheckResponseDto = jacksonObjectMapper().readValue(response.content!!)
                        with(healthCheckResponse) {
                            ready shouldBe true
                            appVersion shouldBe  appConfig.deployment.version
                            responseTimestamp.time shouldBe dateSupplier.currentTimeMillis()
                        }
                    }
                }
            }
        }
    }
})
