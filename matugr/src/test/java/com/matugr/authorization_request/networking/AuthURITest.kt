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

package com.matugr.authorization_request.networking

import com.matugr.common.external.UriCharacter
import com.matugr.common.oauth.CODE
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthURITest {

    private val redirectQueryWithCode = "redirect://?code=abc&state=efg"
    private val redirectFragmentWithCode = "redirect://#code=abc&state=efg"
    private val redirectGarbageQueryAndFragmentWithCode = "redirect://?asdkfnas=sdalfas#code=abc&state=efg"

    @Test
    fun `when code in query then get auth params returns code key in map`() {
        val params = AuthURI(redirectQueryWithCode).getParams(UriCharacter.ParametersIdentifier.Query)
        assert(params.keys.contains(CODE))
    }

    @Test
    fun `when code value in query then get auth params contains correct code`() {
        val params = AuthURI(redirectQueryWithCode).getParams(UriCharacter.ParametersIdentifier.Query)
        assertEquals("abc", params[CODE])
    }

    @Test
    fun `when code in fragment then get auth params returns code key in map`() {
        val params = AuthURI(redirectFragmentWithCode).getParams(UriCharacter.ParametersIdentifier.Fragment)
        assert(params.keys.contains(CODE))
    }

    @Test
    fun `when code value in fragment then get auth params contains correct code`() {
        val params = AuthURI(redirectFragmentWithCode).getParams(UriCharacter.ParametersIdentifier.Fragment)
        assertEquals("abc", params[CODE])
    }

    @Test
    fun `given garbage query when code in fragment then get auth params returns code key in map`() {
        val params = AuthURI(redirectGarbageQueryAndFragmentWithCode)
                .getParams(UriCharacter.ParametersIdentifier.Fragment)
        assert(params.keys.contains(CODE))
    }

    @Test
    fun `given garbage query when code value in fragment then get auth params contains correct code`() {
        val params = AuthURI(redirectGarbageQueryAndFragmentWithCode)
                .getParams(UriCharacter.ParametersIdentifier.Fragment)
        assertEquals("abc", params[CODE])
    }
}