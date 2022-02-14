package com.matugr.ui

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
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
        every { viewModelFactory.hint(AuthViewModel::class).create(AuthViewModel::class.java) } returns authViewModel

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