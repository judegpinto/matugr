/*
 * Copyright 2022 The Matugr Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.matugr.token_request.networking

import com.matugr.common.oauth.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Networking class represents the expected OAuth token response
 *
 * Leveraged by moshi for networking
 *
 * See README: Token Response
 */
@JsonClass(generateAdapter = true)
internal data class TokenResponseBody(
    @Json(name = ACCESS_TOKEN) val accessToken: String,
    @Json(name = TOKEN_TYPE) val tokenType: String,
    @Json(name = REFRESH_TOKEN) val refreshToken: String?,
    @Json(name = EXPIRES_IN) val expiresIn: Long,
    @Json(name = SCOPE) val scope: String?,
)
