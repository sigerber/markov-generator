package ports.provides

interface ModelManagerPort {
    fun createModel(): ModelIdentifier
}

data class ModelIdentifier(val id: String)