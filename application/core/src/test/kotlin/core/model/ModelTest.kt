package core.model

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeUnique
import org.junit.jupiter.api.fail
import ports.provides.ModelIdentifier
import ports.provides.ModelManagerPort
import ports.requires.ModelStoragePort
import ports.requires.PersistentModel
import java.util.*

class ModelTest : DescribeSpec({
    describe("Model Manager") {
        val mockStorage: ModelStoragePort = mock()
        val modelManager = ModelManager(mockStorage)

        beforeEach {
            reset(mockStorage)
        }
        context("no model exists") {
            it("generate an identifier and persists it when creating a model") {
                val modelIdentifier = modelManager.createModel()

                val expectedModel = PersistentModel(modelIdentifier)
                verify(mockStorage).storeModel(expectedModel)
            }

            it("generates unique identifiers") {
                val bunchOfIdentifiers = (1..100).map { modelManager.createModel() }

                bunchOfIdentifiers.shouldBeUnique()
            }
        }
    }
})





