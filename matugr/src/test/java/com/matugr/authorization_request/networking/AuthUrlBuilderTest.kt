package com.matugr.authorization_request.networking

import com.matugr.authorization_request.oauth.CodeChallengeMethod
import com.matugr.common.external.UriCharacter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AuthUrlBuilderTest {
    private val authUrl = "https://test.oauthserver.com/authorize"
    private val clientId = "ff97fbb4da3313004591cc3a291b47fd"
    private val responseType = ResponseType.Code
    private val redirectUri = "testauth://"
    private val codeChallenge = "a".repeat(80)
    private val codeChallengeMethod = CodeChallengeMethod.S256.toString()
    private val authorizationNetworkingRequest: AuthorizationNetworkingRequest =
        AuthorizationNetworkingRequest(
            clientId = clientId,
            responseType = responseType,
            codeChallenge = codeChallenge,
            codeChallengeMethod = codeChallengeMethod,
            redirectUri = redirectUri)
    private val authUrlBuilder: AuthUrlBuilder = AuthUrlBuilder()

    @Test
    fun `when json has parameters then parameters added to url`() {
        val expectedUrl = "$authUrl${UriCharacter.ParametersIdentifier.Query.identifier}client_id=$clientId&response_type=${responseType.jsonValue}&code_challenge=$codeChallenge&code_challenge_method=$codeChallengeMethod&redirect_uri=$redirectUri"
        val url = authUrlBuilder.generate(authUrl, authorizationNetworkingRequest, UriCharacter.ParametersIdentifier.Query)
        assertEquals(expectedUrl, url)
    }

    @Test
    fun `null parameters excluded`() {
        val url = authUrlBuilder.generate(authUrl, authorizationNetworkingRequest, UriCharacter.ParametersIdentifier.Query)
        assertFalse(url.contains("scope="))
    }


    @Test
    fun `when additional parameters then parameters included`() {
        val customParameters = mapOf("bleh" to "meh")
        val expectedUrl = "$authUrl${UriCharacter.ParametersIdentifier.Query.identifier}client_id=$clientId&response_type=${responseType.jsonValue}&code_challenge=$codeChallenge&code_challenge_method=$codeChallengeMethod&redirect_uri=$redirectUri&bleh=meh"
        val url = authUrlBuilder.generate(authUrl, authorizationNetworkingRequest, UriCharacter.ParametersIdentifier.Query, customParameters)
        assertEquals(expectedUrl, url)
    }

    @Test
    fun `when custom parameters are empty map then no parameters added`() {
        val customParameters = emptyMap<String, String>()
        val expectedUrl = "$authUrl${UriCharacter.ParametersIdentifier.Query.identifier}client_id=$clientId&response_type=${responseType.jsonValue}&code_challenge=$codeChallenge&code_challenge_method=$codeChallengeMethod&redirect_uri=$redirectUri"
        val url = authUrlBuilder.generate(authUrl, authorizationNetworkingRequest, UriCharacter.ParametersIdentifier.Query, customParameters)
        assertEquals(expectedUrl, url)
    }
}
