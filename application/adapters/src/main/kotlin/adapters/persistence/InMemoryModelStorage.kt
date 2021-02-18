package adapters.persistence

import ports.requires.ModelStoragePort
import ports.requires.PersistentModel

class InMemoryModelStorage : ModelStoragePort {
    override fun storeModel(persistentModel: PersistentModel) {
        TODO("Not yet implemented")
    }
}