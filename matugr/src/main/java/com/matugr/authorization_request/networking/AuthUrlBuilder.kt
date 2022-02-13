package com.matugr.authorization_request.networking

import com.matugr.common.external.UriCharacter
import com.matugr.common.oauth.*
import javax.inject.Inject

/**
 * Networking class that constructs the authorization url from
 * [AuthorizationNetworkingRequest]
 *
 * See README: Authorization Request
 */
internal class AuthUrlBuilder @Inject constructor() {
    fun generate(baseUrl: String,
                 authorizationNetworkingRequest: AuthorizationNetworkingRequest,
                 uriParameterLocation: UriCharacter.ParametersIdentifier,
                 customParameters: Map<String, String>? = null): String {
        val parametersMap = createKeyValueMap(authorizationNetworkingRequest)

        val keyValueMap =
            if (customParameters == null) parametersMap
            else parametersMap + customParameters

        val urlAppendage = createUrl(
            keyValueMap,
            UriCharacter.Separator.character,
            uriParameterLocation.identifier)
        return baseUrl.plus(urlAppendage)
    }

    private fun createUrl(keyValues: Map<String, String?>, separator: String, prefix: String): String {
        return keyValues.filterValues { it != null && it.isNotEmpty()  }
            .toList()
            .joinToString(separator = separator, prefix = prefix) { "${it.first}=${it.second}" }
    }

    private fun createKeyValueMap(
        authorizationNetworkingRequest: AuthorizationNetworkingRequest
    ): Map<String, String?> {
        val responseTypeValue = authorizationNetworkingRequest.responseType.jsonValue
        val requiredParameters = mapOf(
            CLIENT_ID to authorizationNetworkingRequest.clientId,
            RESPONSE_TYPE to responseTypeValue,
            CODE_CHALLENGE to authorizationNetworkingRequest.codeChallenge,
            CODE_CHALLENGE_METHOD to authorizationNetworkingRequest.codeChallengeMethod
        )

        val optionalParameters = getOptionalParameters(authorizationNetworkingRequest)

        return requiredParameters + optionalParameters
    }

    private fun getOptionalParameters(
        authorizationNetworkingRequest: AuthorizationNetworkingRequest
    ): Map<String, String> {
        val optionalParameters = mutableMapOf<String, String>()
        authorizationNetworkingRequest.redirectUri?.let {
            optionalParameters.put(REDIRECT_URI, it)
        }
        authorizationNetworkingRequest.scope?.let {
            optionalParameters.put(SCOPE, it)
        }
        authorizationNetworkingRequest.state?.let {
            optionalParameters.put(STATE, it)
        }
        return optionalParameters
    }
}