package adapters

import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.KoinContextHandler
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.SLF4JLogger
import java.util.*

object IntegrationTestContext {
    private val application: TestApplicationEngine by lazy {
        val testEnvironment = createTestEnvironment()
        testEnvironment.start()
        val engine = TestApplicationEngine(testEnvironment)
        engine.start()
        initApp(testEnvironment.application)
        engine
    }

    fun <R> withApp(test: suspend TestApplicationEngine.() -> R) =
        runBlocking {
            test(application)
        }

    private fun initApp(application: Application) {
        System.setProperty("APP_DEPLOYMENT_ENV", "test")
        System.setProperty("APP_VERSION", "0.0")
        System.setProperty("APP_BUILD_NUMBER", "0")
        val mainConfigProperties = Properties().apply {

        }
        val config = ConfigFactory.parseProperties(mainConfigProperties)
        KoinContextHandler.stop()
        application.install(Koin) {
            SLF4JLogger()
            modules(
                module {
                    single { application }
                    single { config }
                },
                adapterModule,
                envTestModule,
                mockedCoreModule
            )
        }
    }
}
