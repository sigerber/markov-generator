package ports.requires

import ports.provides.ModelIdentifier

interface ModelStoragePort {
    fun storeModel(persistentModel: PersistentModel)
}

data class PersistentModel(val id: ModelIdentifier)