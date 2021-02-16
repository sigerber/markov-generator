package adapters

import adapters.config.EnvironmentVariables
import org.koin.dsl.module
import ports.input.util.DateSupplier

// Environment-specific configuration
val envTestModule = module(createdAtStart = true) {
    single<EnvironmentVariables> {
        EnvironmentVariablesTestImpl()
    }
    single<DateSupplier> {
        DateSupplierTestImpl()
    }
}

val mockedCoreModule = module(createdAtStart = true) {

}

