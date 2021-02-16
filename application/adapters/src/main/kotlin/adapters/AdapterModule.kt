package adapters

import adapters.config.*
import adapters.primary.web.routes.healthcheck.HealthCheckRoute
import adapters.remoting.HttpClientFactory
import adapters.remoting.HttpClientFactoryImpl
import adapters.services.healthcheck.HealthCheckService
import adapters.util.DateSupplierSystemTimeImpl
import org.koin.dsl.module
import ports.input.util.DateSupplier

// Environment-specific configuration
val envModule = module(createdAtStart = true) {
    single<EnvironmentVariables> {
        EnvironmentVariablesImpl()
    }
    single<DateSupplier> {
        DateSupplierSystemTimeImpl()
    }
}

// Adapter modules
val adapterModule = module(createdAtStart = true) {
    // Configuration
    single<ConfigRepository> {
        ConfigRepositoryImpl(envVars = get())
    }
    single {
        AppConfig(configRepository = get())
    }
    single {
        AppBootstrap(application = get())
    }

    // Remote clients
    single<HttpClientFactory> {
        HttpClientFactoryImpl()
    }

    // Web routes
    single { HealthCheckRoute(application = get()) }

    // Internal adapter services
    single {
        HealthCheckService(appConfig = get(), dateSupplier = get())
    }
}
