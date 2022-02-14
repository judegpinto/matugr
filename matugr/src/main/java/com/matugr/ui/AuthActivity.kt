package com.matugr.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.matugr.common.external.AuthAdapterFactory
import javax.inject.Inject

class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @JvmField
    @Inject
    var authLayoutId: Int? = null

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthAdapterFactory.getComponent()?.inject(this) ?: finish()
        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]
        authLayoutId?.let {
            setContentView(it)
        }
        authViewModel.processingFinished.observe(this, ::end)
        authViewModel.processIntent(intent.data)
    }

    private fun end(processingFinished: Boolean) {
        if (processingFinished) finish()
    }
}