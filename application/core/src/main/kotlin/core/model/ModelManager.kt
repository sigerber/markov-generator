package core.model

import ports.provides.ModelIdentifier
import ports.provides.ModelManagerPort
import ports.requires.ModelStoragePort
import ports.requires.PersistentModel
import java.util.*

class ModelManager(private val modelStorage: ModelStoragePort) : ModelManagerPort {
    override fun createModel(): ModelIdentifier {
        val identifier = ModelIdentifier(UUID.randomUUID().toString())
        modelStorage.storeModel(PersistentModel(identifier))

        return identifier
    }
}