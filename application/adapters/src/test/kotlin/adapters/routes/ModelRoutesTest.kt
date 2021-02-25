package adapters.routes

import adapters.IntegrationTestContext.withApp
import adapters.remoting.HttpClientFactory
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import org.koin.ktor.ext.inject
import ports.provides.ModelManagerPort

class ModelRoutesTest : DescribeSpec({

    withApp {
        val modelManager: ModelManagerPort by application.inject()
        val clientFactory: HttpClientFactory by application.inject()
        val client = clientFactory.httpClient()

        beforeEach {
            reset(modelManager)
        }

        describe("HTTP POST /model") {
            context("when no models exist") {
                it("creates a model") {

                    client.post<String>(path = "/model", host="localhost", port = 8080) {}

                    verify(modelManager).createModel()
                }
            }
        }
    }
})
