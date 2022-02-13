package matugr.authorization_request.oauth

import matugr.authorization_request.domain.StateOption
import javax.inject.Inject
import javax.inject.Named

const val state_length = "16"

/**
 * Generates a state based on client option
 *
 * See README: State
 */
internal class StateGenerator @Inject constructor(
    @Named(state_length) private val secureRandomGenerator: SecureRandomGenerator
) {
    fun generate(stateOption: StateOption): String? {
        return when(stateOption) {
            is StateOption.Exclude -> null
            is StateOption.Generate -> secureRandomGenerator.generateSecureRandomBase64()
            is StateOption.Input -> stateOption.state
        }
    }
}
