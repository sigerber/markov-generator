package adapters.routes

import adapters.IntegrationTestContext.withApp
import adapters.config.AppConfig
import adapters.remoting.HttpClientFactory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import org.koin.ktor.ext.inject
import ports.input.util.DateSupplier
import ports.output.healthcheck.HealthCheckResponseDto

class HealthCheckRouteTest : DescribeSpec({

    describe("HTTP GET /health") {
        context("when application is healthy") {
            it("returns healthy status") {
                withApp {
                    val clientFactory: HttpClientFactory by application.inject()
                    val client = clientFactory.httpClient()
                    val dateSupplier: DateSupplier by application.inject()
                    val appConfig: AppConfig by application.inject()
                    val response = client.get<HealthCheckResponseDto>(path = "/health", port = 8080) {}
                    with(response) {
                        ready shouldBe true
                        appVersion shouldBe appConfig.deployment.version
                        responseTimestamp.time shouldBe dateSupplier.currentTimeMillis()
                    }
                }
            }
        }
    }
})
