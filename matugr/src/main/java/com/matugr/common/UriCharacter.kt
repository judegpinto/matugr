package com.matugr.common

/**
 * Class that defines a point in the uri. Used to designate where to find uri parameters.
 */
sealed class UriCharacter(val character: String) {
    sealed class ParametersIdentifier(val identifier: String): UriCharacter(identifier) {
        object Query: ParametersIdentifier("?")
        object Fragment: ParametersIdentifier("#")
    }
    object Separator: UriCharacter("&")
}
