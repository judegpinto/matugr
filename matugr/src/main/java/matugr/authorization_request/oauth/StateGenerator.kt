package matugr.authorization_request.oauth

import matugr.authorization_request.StateOption
import javax.inject.Inject
import javax.inject.Named

const val state_length = "16"

internal class StateGenerator @Inject constructor(
    @Named(state_length) private val secureRandomGenerator: SecureRandomGenerator
) {
    fun state(stateOption: StateOption): String? {
        return when(stateOption) {
            is StateOption.Exclude -> null
            is StateOption.Generate -> secureRandomGenerator.generateSecureRandomBase64()
            is StateOption.Input -> stateOption.state
        }
    }
}