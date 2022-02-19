package com.matugr.authorization_request.domain

import com.matugr.authorization_request.external.AuthorizationResult
import java.net.URI

interface AuthorizationResultManufacturer {
    fun processAuthorizationUri(uri: URI, state: String?, codeVerifier: String): AuthorizationResult
}