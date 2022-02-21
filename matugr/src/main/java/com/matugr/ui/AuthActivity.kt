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