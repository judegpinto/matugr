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

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matugr.authorization_request.domain.AuthRedirectPort
import com.matugr.common.tools.DispatcherProvider
import kotlinx.coroutines.launch
import java.net.URI

/**
 * Pairs with the matugr redirect activity to process the response (once) and illicit action when
 * processing is finished.
 */
internal class AuthViewModel(
    private val authRedirectPort: AuthRedirectPort,
    private val dispatcherProvider: DispatcherProvider
): ViewModel() {
    private var cachedUri: Uri? = null
    private val processingFinishedLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val processingFinished: LiveData<Boolean> = processingFinishedLiveData

    fun processIntent(uri: Uri?) {
        viewModelScope.launch(dispatcherProvider.default()) {
            if (existingUriCached(uri)) {
                return@launch
            }
            authRedirectPort.handleRedirectUri(URI.create(uri.toString()))
            launch(dispatcherProvider.main()) {
                processingFinishedLiveData.value = true
            }
        }
    }

    private fun existingUriCached(uri: Uri?): Boolean {
        if (( uri == null) || (cachedUri != null)) {
            return true
        }
        cachedUri = uri
        return false
    }
}
