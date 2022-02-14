package com.matugr.common.external

import com.matugr.authorization_request.domain.AuthorizationRequestModel
import com.matugr.authorization_request.domain.AuthorizationRequestPort
import com.matugr.token_request.domain.TokenRequestModel
import com.matugr.token_request.domain.TokenRequestPort
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Business logic around authorization requests and token requests.
 * Separate ports and models handle individual logic.
 */
@Singleton
internal class AuthModel @Inject constructor(
    private val authorizationRequestModel: AuthorizationRequestModel,
    private val tokenRequestModel: TokenRequestModel
): RequestPort,
    AuthorizationRequestPort by authorizationRequestModel,
    TokenRequestPort by tokenRequestModel