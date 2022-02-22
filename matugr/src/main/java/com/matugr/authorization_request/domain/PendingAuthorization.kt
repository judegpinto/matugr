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

package com.matugr.authorization_request.domain

import kotlinx.coroutines.CancellableContinuation
import com.matugr.authorization_request.external.AuthorizationResult

/**
 * Data holder for information needed to process authorization response received by app
 */
internal data class PendingAuthorization(
    val authorizationContinuation: CancellableContinuation<AuthorizationResult>,
    val codeVerifier: String,
    val state: String?
)
