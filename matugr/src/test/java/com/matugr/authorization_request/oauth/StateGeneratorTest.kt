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

import io.mockk.every
import io.mockk.mockk
import com.matugr.authorization_request.external.option.StateOption
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StateGeneratorTest {
    private val generatedState = "generated state"
    private val inputState = "input state"
    private val secureRandomBase64: SecureRandomGenerator = mockk()

    private lateinit var stateGenerator: StateGenerator

    @Before
    fun setup() {
        every { secureRandomBase64.generateSecureRandomBase64() } returns generatedState
        stateGenerator = StateGenerator(secureRandomBase64)
    }

    @Test
    fun `when state option is Exclude then state is null`() {
        val stateOption = StateOption.Exclude
        assertEquals(null, stateGenerator.generate(stateOption))
    }

    @Test
    fun `when state option is Generate then state is generated string`() {
        val stateOption = StateOption.Generate
        assertEquals(generatedState, stateGenerator.generate(stateOption))
    }

    @Test
    fun `when state option is Input then state is generated base 64 string`() {
        val stateOption = StateOption.Input(inputState)
        assertEquals(inputState, stateGenerator.generate(stateOption))
    }
}
