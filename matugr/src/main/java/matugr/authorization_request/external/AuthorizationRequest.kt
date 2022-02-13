package matugr.authorization_request.external

import matugr.authorization_request.external.option.CodeVerifierOption
import matugr.authorization_request.external.option.StateOption

/**
 * API for client to specify authorization request parameters, including OAuth specific parameters,
 * as well as dictate options for OAuth configuration, i.e. state and code verifier.
 */
data class AuthorizationRequest(
    val authUrl: String,
    val clientId: String,
    val responseType: AuthorizationResponseType,
    val redirectUri: String? = null,
    val scope: String? = null,
    val stateOption: StateOption = StateOption.Generate,
    val codeVerifierOption: CodeVerifierOption = CodeVerifierOption.Generate,
    val customParameters: Map<String, String> = emptyMap()
)
