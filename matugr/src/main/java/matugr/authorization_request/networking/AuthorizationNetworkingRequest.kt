package matugr.authorization_request.networking

/**
 * Networking class that holds authorization request parameters.
 * Required parameters are non-null. Optional parameters are nullable.
 *
 * See README: Authorization Request
 */
data class AuthorizationNetworkingRequest(
    val clientId: String,
    val responseType: ResponseType,
    val codeChallenge: String,
    val codeChallengeMethod: String,
    val redirectUri: String? = null,
    val scope: String? = null,
    val state: String? = null
)
