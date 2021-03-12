package adapters.pact.provider

import adapters.IntegrationTestContext.withApp
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.Consumer
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.ktor.ext.inject
import ports.provides.ModelIdentifier
import ports.provides.ModelManagerPort

@Provider("markov-generator")
@Consumer("markov-client")
@PactBroker(host = "localhost", port = "9292", tags = ["dev", "prod"])
class MarkovClientPactTest {

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext) {
        withApp {
            context.verifyInteraction()
        }
    }

    @State("no model exists")
    fun setupNoModelDetails() {
        withApp {
            val mockModelManager: ModelManagerPort by application.inject()

            whenever(mockModelManager.createModel())
                .thenReturn(ModelIdentifier("1234"))
        }
    }

}