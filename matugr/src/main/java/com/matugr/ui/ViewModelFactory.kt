package com.matugr.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.matugr.authorization_request.domain.AuthRedirectPort
import com.matugr.common.tools.DispatcherProvider
import java.lang.IllegalStateException
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
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