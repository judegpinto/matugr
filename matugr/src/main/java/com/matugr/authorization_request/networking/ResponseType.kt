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

package com.matugr.authorization_request.networking

import com.squareup.moshi.Json
import com.matugr.common.oauth.CODE

/**
 * Networking class type leveraged by moshi
 *
 * See README: Response Type
 */
internal enum class ResponseType(val jsonValue: String) {
    @Json(name = CODE) Code(CODE)
}
