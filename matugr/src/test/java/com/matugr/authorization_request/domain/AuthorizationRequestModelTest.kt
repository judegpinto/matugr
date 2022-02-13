package com.matugr.authorization_request.domain

import com.matugr.authorization_request.external.AuthorizationRequest
import com.matugr.authorization_request.external.AuthorizationResponseType
import com.matugr.authorization_request.networking.AuthUrlBuilder
import com.matugr.authorization_request.oauth.CodeChallengeGenerator
import com.matugr.authorization_request.oauth.CodeChallengeMethod
import com.matugr.authorization_request.oauth.CodeVerifierContainer
import com.matugr.authorization_request.oauth.CodeVerifierContainer.Companion.MIN_LENGTH
import com.matugr.authorization_request.oauth.StateGenerator
import com.matugr.authorization_request.tools.CoroutineTestRule
import com.matugr.common.external.UriCharacter
import com.matugr.common.external.UrlLauncherPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class AuthorizationRequestModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()
    private val testCoroutineScope: CoroutineScope =
        CoroutineScope(coroutinesTestRule.testDispatcher)

    // data
    private val clientId: String = UUID.randomUUID().toString()
    private val authUrl: String = "https://www.nothing.com/"
    private val responseType: AuthorizationResponseType = AuthorizationResponseType.Code
    private val customParameters: Map<String, String> = mapOf("this" to "that")
    private val authorizationUrl = "https://authorization.oauth.com/"

    // Code Challenge
    private val codeChallengeValue: String = "codeChallenge"
    private val codeChallengeMethod: CodeChallengeMethod = CodeChallengeMethod.S256
    private val codeVerifier =
        CodeVerifierContainer("c".repeat(MIN_LENGTH), codeChallengeValue, codeChallengeMethod)

    // State
    private val state = "state"

    private val stateGenerator: StateGenerator = mockk()
    private val codeChallengeGenerator: CodeChallengeGenerator = mockk()
    private val authUrlBuilder: AuthUrlBuilder = mockk()
    private val fakePendingAuthorizationStore: FakePendingAuthorizationStore =
        FakePendingAuthorizationStore()
    private val urlLauncher: UrlLauncherPort = mockk(relaxUnitFun = true)

    private lateinit var authorizationRequestModel: AuthorizationRequestModel

    @Before
    fun setup() {
        every { codeChallengeGenerator.codeVerifierAndChallenge(any()) } returns codeVerifier
        every { stateGenerator.generate(any()) } returns state
        authorizationRequestModel = AuthorizationRequestModel(
            coroutinesTestRule.testDispatcherProvider,
            stateGenerator,
            codeChallengeGenerator,
            authUrlBuilder,
            fakePendingAuthorizationStore
        )
    }

    @Test
    fun `when authorization parameters provided url builder receives parameters`() {
        val authRequest = AuthorizationRequest(
            authUrl = authUrl,
            clientId = clientId,
            responseType = responseType,
            customParameters = customParameters)
        every { authUrlBuilder.generate(authUrl, any(), UriCharacter.ParametersIdentifier.Query, customParameters) } returns authorizationUrl

        launchAuthorization(authRequest) { authorizationRequestModel.extinguishAuthFlow() }

        verify { authUrlBuilder.generate(authUrl, any(), UriCharacter.ParametersIdentifier.Query, customParameters) }
    }

    @Test
    fun `when authorization parameters provided then url launched with auth parameters`() {
        val authRequest = AuthorizationRequest(
            authUrl = authUrl,
            clientId = clientId,
            responseType = responseType,
            customParameters = customParameters)
        every { authUrlBuilder.generate(authUrl, any(), UriCharacter.ParametersIdentifier.Query, customParameters) } returns authorizationUrl

        launchAuthorization(authRequest) { authorizationRequestModel.extinguishAuthFlow() }

        verify { urlLauncher.launchUrl(authorizationUrl) }
    }

    @Test
    fun `when authorization request started then pending authorization is stored`() {
        val authRequest = AuthorizationRequest(
            authUrl = authUrl,
            clientId = clientId,
            responseType = responseType,
            customParameters = customParameters)
        every { authUrlBuilder.generate(authUrl, any(), UriCharacter.ParametersIdentifier.Query, customParameters) } returns authorizationUrl

        launchAuthorization(authRequest) { authorizationRequestModel.extinguishAuthFlow() }

        assert(fakePendingAuthorizationStore.pendingAuthorizationStored())
    }

    @Test
    fun `when authorization request cancelled then pending authorization is cancelled`() {
        val authRequest = AuthorizationRequest(
            authUrl = authUrl,
            clientId = clientId,
            responseType = responseType,
            customParameters = customParameters)
        every { authUrlBuilder.generate(authUrl, any(), UriCharacter.ParametersIdentifier.Query, customParameters) } returns authorizationUrl

        launchAuthorization(authRequest) { authorizationRequestModel.extinguishAuthFlow() }

        assert(fakePendingAuthorizationStore.pendingAuthorizationCancelled())
    }

    /**
     * [runBlockingTest] and [runBlocking] do not play well with the current implementation of
     * [AuthorizationRequestModel].
     *
     * [AuthorizationRequestModel.performAuthorizationRequest] returns [suspendCancellableCoroutine]
     *
     * [suspendCancellableCoroutine] does not complete immediately, because the implementation
     * relies on another process to resume execution.
     *
     * [runBlockingTest] will throw an exception, and [runBlocking] will suspend indefinitely.
     *
     * This function mimics that "multi-process behavior" by launching the authorization request
     * and then separately using [testAction] to invoke another process, e.g. cancellation.
     *
     * This function can be removed if a future version of kotlinx-coroutines-test supports
     * testing [suspendCancellableCoroutine]
     */
    private fun launchAuthorization(authRequest: AuthorizationRequest, testAction: () -> Unit) {
        val jobs = arrayListOf<Job>().apply {
            add(testCoroutineScope.launch {
                authorizationRequestModel.performAuthorizationRequest(authRequest, urlLauncher)
            })
            add(testCoroutineScope.launch {
                testAction.invoke()
            })
        }

        runBlocking {
            jobs.joinAll()
        }
    }
}