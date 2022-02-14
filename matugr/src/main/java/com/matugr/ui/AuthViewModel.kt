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

class AuthViewModel(
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
