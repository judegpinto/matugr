package com.matugr.authorization_request.oauth

import io.mockk.*
import com.matugr.authorization_request.external.option.CodeVerifierOption
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

class CodeChallengeGeneratorTest {
    private val mockBase64Value: String = "a".repeat(100)
    private val encodedValue: String = "b".repeat(100)
    private val digestResult = "abcd".toByteArray()
    private val mockMessageDigest: MessageDigest = mockk()
    private val mockSecureRandomGenerator: SecureRandomGenerator = mockk()
    private val mockBase64Encoder: Base64.Encoder = mockk()
    private lateinit var codeChallengeGenerator: CodeChallengeGenerator

    @Before
    fun setup() {
        every { mockSecureRandomGenerator.generateSecureRandomBase64() }  returns mockBase64Value
        justRun { mockMessageDigest.update(mockBase64Value.toByteArray()) }
        every { mockMessageDigest.digest() } returns digestResult
        every { mockBase64Encoder.encodeToString(digestResult) } returns encodedValue
        codeChallengeGenerator = CodeChallengeGenerator(mockMessageDigest, mockSecureRandomGenerator, mockBase64Encoder)
    }

    @Test
    fun `when message digest is null then plain method used`() {
        val codeChallengeNullDigest = CodeChallengeGenerator(null, mockSecureRandomGenerator, mockBase64Encoder)
        val codeVerifier = codeChallengeNullDigest.codeVerifierAndChallenge(CodeVerifierOption.Generate)
        assertEquals(CodeChallengeMethod.PLAIN, codeVerifier.codeChallengeMethod)
    }

    @Test
    fun `when message digest is null then code challenge is code verifier`() {
        val codeChallengeNullDigest = CodeChallengeGenerator(null, mockSecureRandomGenerator, mockBase64Encoder)
        val codeVerifier = codeChallengeNullDigest.codeVerifierAndChallenge(CodeVerifierOption.Generate)
        assertEquals(codeVerifier.codeVerifier, codeVerifier.codeChallenge)
    }

    @Test
    fun `when hash computation completed then result base64 encoded`() {
        codeChallengeGenerator.codeVerifierAndChallenge(CodeVerifierOption.Generate)
        verify { mockBase64Encoder.encodeToString(digestResult) }
    }

    @Test
    fun `message digest updates with code verifier byte array`() {
        codeChallengeGenerator.codeVerifierAndChallenge(CodeVerifierOption.Generate)
        verify { mockMessageDigest.update(mockBase64Value.toByteArray(StandardCharsets.ISO_8859_1)) }
    }
}
