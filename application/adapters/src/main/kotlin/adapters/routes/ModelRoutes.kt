package adapters.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import ports.provides.ModelManagerPort

class ModelRoutes(application: Application, private val modelManager: ModelManagerPort) {

    init {
        application.routing {

            post("/model") {
                val modelIdentifier = modelManager.createModel()
                call.respond(HttpStatusCode.OK, modelIdentifier)
            }
        }
    }
}
