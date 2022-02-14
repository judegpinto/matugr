package com.matugr.authorization_request.oauth

import com.matugr.authorization_request.external.option.StateOption
import com.matugr.di.state_length
import javax.inject.Inject
import javax.inject.Named

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
