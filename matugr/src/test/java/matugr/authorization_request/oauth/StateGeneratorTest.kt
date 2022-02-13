package matugr.authorization_request.oauth

import io.mockk.every
import io.mockk.mockk
import matugr.authorization_request.domain.StateOption
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StateGeneratorTest {
    private val generatedState = "generated state"
    private val inputState = "input state"
    private val secureRandomBase64: SecureRandomGenerator = mockk()

    private lateinit var stateGenerator: StateGenerator

    @Before
    fun setup() {
        every { secureRandomBase64.generateSecureRandomBase64() } returns generatedState
        stateGenerator = StateGenerator(secureRandomBase64)
    }

    @Test
    fun `when state option is Exclude then state is null`() {
        val stateOption = StateOption.Exclude
        assertEquals(null, stateGenerator.generate(stateOption))
    }

    @Test
    fun `when state option is Generate then state is generated string`() {
        val stateOption = StateOption.Generate
        assertEquals(generatedState, stateGenerator.generate(stateOption))
    }

    @Test
    fun `when state option is Input then state is generated base 64 string`() {
        val stateOption = StateOption.Input(inputState)
        assertEquals(inputState, stateGenerator.generate(stateOption))
    }
}
