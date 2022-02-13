package matugr.authorization_request.domain

import java.net.URI

/**
 * Port-adapter scheme class intended to provide a connection:
 * system components that receive the redirect -> domain classes that process the response
 */
interface AuthRedirectPort {
    fun handleRedirectUri(uri: URI)
}