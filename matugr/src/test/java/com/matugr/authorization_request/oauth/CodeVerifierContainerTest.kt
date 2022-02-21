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

import org.junit.Test
import java.lang.IllegalArgumentException

class CodeVerifierContainerTest {

    @Test(expected = IllegalArgumentException::class)
    fun `when less then min length then exception thrown`() {
            CodeVerifierContainer("a", "", CodeChallengeMethod.S256)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when greater than max length then exception thrown`() {
        CodeVerifierContainer("a".repeat(10000), "", CodeChallengeMethod.S256)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when regex violated then exception thrown`() {
        CodeVerifierContainer("&".repeat(10000), "", CodeChallengeMethod.S256)
    }

    @Test
    fun `when regex matches then no exception thrown`() {
        CodeVerifierContainer("Aa1".repeat(23), "", CodeChallengeMethod.S256)
    }

    @Test
    fun `when proper length then no exception thrown`() {
        CodeVerifierContainer("A".repeat(100), "", CodeChallengeMethod.S256)
    }
}