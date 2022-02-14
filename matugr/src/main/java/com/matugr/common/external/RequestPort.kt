package com.matugr.common.external

import com.matugr.authorization_request.domain.AuthorizationRequestPort
import com.matugr.token_request.domain.TokenRequestPort

interface RequestPort: AuthorizationRequestPort, TokenRequestPort