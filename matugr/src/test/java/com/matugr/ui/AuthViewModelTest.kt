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

package com.matugr.ui

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.matugr.authorization_request.domain.AuthRedirectPort
import com.matugr.tools.CoroutineTestRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URI

@ExperimentalCoroutinesApi
class AuthViewModelTest {
    @get:Rule val coroutinesTestRule = CoroutineTestRule()

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    private val uriString = "abc"
    private val javaURI = URI.create(uriString)

    private val uri: Uri = mockk()
    private val authRedirectPort: AuthRedirectPort = mockk()

    private val authViewModel: AuthViewModel =
        AuthViewModel(authRedirectPort, coroutinesTestRule.testDispatcherProvider)

    @Before
    fun setup() {
        every { authRedirectPort.handleRedirectUri(javaURI) } returns Unit
        every { uri.toString() } returns uriString
    }

    @Test
    fun `given uri then activity redirect connection receives response`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            authViewModel.processIntent(uri)

            verify { authRedirectPort.handleRedirectUri(javaURI) }
    }

    @Test
    fun `given auth view model process intent twice then activity redirect connection fed response only once`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            authViewModel.processIntent(uri)
            authViewModel.processIntent(uri)

            verify(exactly = 1) { authRedirectPort.handleRedirectUri(javaURI) }
    }
}
