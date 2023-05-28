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

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.matugr.R
import com.matugr.common.external.AuthAdapterFactory
import com.matugr.di.AuthComponent
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The nature of the Android framework requires redirects to happen via [android.app.Activity].
 * Therefore an Activity is required to receive the authorization redirect.
 * However, managing that Activity requires some logic.
 * These tests capture the logic required in the Activity.
 *
 * If possible, these can be moved to an Espresso test, but they should be captured in the
 * Actions workflow.
 */
@RunWith(AndroidJUnit4::class)
class AuthActivityLogicTest {
    private val testUri: Uri = mockk()
    private val externalAuthComponent: AuthComponent = mockk()
    private val viewModelFactory: ViewModelFactory = mockk()
    private val authLayoutId: Int = R.layout.auth_loading_debug
    private val processingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    private val authViewModel = mockk<AuthViewModel>(relaxed = true)

    private val activityStartIntent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, AuthActivity::class.java).apply {
        data = testUri
    }

    @Before
    fun before() {
        // connect AuthActivity ==> AuthViewModel
        mockkStatic(ViewModelProviders::class)
        val viewModelProvider: ViewModelProvider = mockk()
        every { ViewModelProviders.of(any<FragmentActivity>(), viewModelFactory) } returns viewModelProvider
        every { viewModelProvider[AuthViewModel::class.java] } returns authViewModel

        mockkObject(AuthAdapterFactory)
        every { AuthAdapterFactory.getComponent() } returns externalAuthComponent
        every { externalAuthComponent.inject(any()) } answers {
            (args[0] as AuthActivity).viewModelFactory = viewModelFactory
            (args[0] as AuthActivity).authLayoutId = authLayoutId
        }

        every { authViewModel.processingFinished } returns processingLiveData
    }

    @After
    fun after() {
        unmockkAll()
        unmockkStatic(ViewModelProviders::class)
    }

    @Test
    fun `when intent has data then data passed to redirect port`() {
        launch<AuthActivity>(activityStartIntent)
        verify { authViewModel.processIntent(testUri) }
    }

    @Test
    fun `content view displays`() {
        launch<AuthActivity>(activityStartIntent)
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }

    @Test
    fun `activity finishes when live data signals processing done`() {
        val scenario = launch<AuthActivity>(activityStartIntent)
        verify { authViewModel.processIntent(testUri) }
        processingLiveData.value = true
        scenario.onActivity {
            assertTrue(it.isFinishing)
        }
    }
}