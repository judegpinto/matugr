/*
 * Copyright 2022 The Matugr Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
