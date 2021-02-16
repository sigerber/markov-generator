package app

import adapters.adapterModule
import com.typesafe.config.ConfigFactory
import core.coreModule
import io.ktor.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.KoinContextHandler
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.SLF4JLogger
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Root
import java.util.*

/**
 * Derive your objects from this class if you want to initialize TestContainers database
 * and test REST interfaces.
 */
abstract class AppRouteSpek(val appRoot: Root.() -> Unit) : Spek({

    beforeGroup {
    }

    appRoot()
}) {
    companion object {
        fun <R> withApp(test: suspend TestApplicationEngine.() -> R) = withTestApplication {
            runBlocking {
                initApp(application)
                try {
                    test.invoke(this@withTestApplication)
                } finally {
                    KoinContextHandler.stop()
                }
            }
        }

        private fun initApp(application: Application) {
            System.setProperty("APP_DEPLOYMENT_ENV", "test")
            System.setProperty("APP_VERSION", "0.0")
            System.setProperty("APP_BUILD_NUMBER", "0")
            val mainConfigProperties = Properties().apply {
                // Add any test-specific properties here to be merged with config-common.conf, e.g.
                //    put("app-config.main-db.hikari.autoCommit", true)
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
                    envTestModule,
                    adapterModule,
                    coreModule
                )
            }
        }
    }
}
