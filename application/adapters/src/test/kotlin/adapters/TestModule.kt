package adapters

import adapters.config.EnvironmentVariables
import com.nhaarman.mockitokotlin2.mock
import org.koin.dsl.module
import ports.input.util.DateSupplier
import ports.provides.ModelManagerPort

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
    single<ModelManagerPort> {
        mock()
    }
}

