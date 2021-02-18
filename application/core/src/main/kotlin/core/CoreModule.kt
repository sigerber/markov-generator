package core

import core.model.ModelManager
import org.koin.dsl.module
import ports.provides.ModelManagerPort

// Core module for Dependency Injection
val coreModule = module(createdAtStart = true) {

    single<ModelManagerPort> { ModelManager(modelStorage = get()) }
}
