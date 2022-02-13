package com.matugr.authorization_request.oauth

import org.junit.Test
import java.lang.IllegalArgumentException

class CodeVerifierContainerTest {

    @Test(expected = IllegalArgumentException::class)
    fun `when less then min length then exception thrown`() {
            CodeVerifierContainer("a", "", CodeChallengeMethod.S256)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when greater than max length then exception thrown`() {
        CodeVerifierContainer("a".repeat(10000), "", CodeChallengeMethod.S256)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when regex violated then exception thrown`() {
        CodeVerifierContainer("&".repeat(10000), "", CodeChallengeMethod.S256)
    }

    @Test
    fun `when regex matches then no exception thrown`() {
        CodeVerifierContainer("Aa1".repeat(23), "", CodeChallengeMethod.S256)
    }

    @Test
    fun `when proper length then no exception thrown`() {
        CodeVerifierContainer("A".repeat(100), "", CodeChallengeMethod.S256)
    }
}