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

package com.matugr.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.matugr.authorization_request.domain.AuthRedirectPort
import com.matugr.common.tools.DispatcherProvider
import java.lang.IllegalStateException
import javax.inject.Inject

internal class ViewModelFactory @Inject constructor(
    private val authRedirectPort: AuthRedirectPort,
    private val dispatcherProvider: DispatcherProvider
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass) {
            AuthViewModel::class.java -> AuthViewModel(authRedirectPort, dispatcherProvider) as T
            else -> throw IllegalStateException("ViewModel not supported")
        }
    }
}