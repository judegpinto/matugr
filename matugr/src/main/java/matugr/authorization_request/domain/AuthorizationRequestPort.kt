package matugr.authorization_request.domain

import matugr.authorization_request.external.AuthorizationRequest
import matugr.authorization_request.external.AuthorizationResult
import matugr.common.external.UrlLauncherPort

/**
 * Domain class that separates authorization requests from other OAuth-related requests
 */
interface AuthorizationRequestPort {
    suspend fun performAuthorizationRequest(authorizationRequestExternal: AuthorizationRequest,
                                            urlLauncherPort: UrlLauncherPort
    ): AuthorizationResult
    /**
     * This only needs to be called when authorization was launched and the client side quits the
     * authorization flow, e.g. the client application is swipe-killed.
     */
    fun extinguishAuthFlow()
}
