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
