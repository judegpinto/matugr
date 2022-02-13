package matugr.authorization_request.networking

import matugr.common.UriCharacter
import matugr.common.oauth.CODE
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthURITest {

    private val redirectQueryWithCode = "redirect://?code=abc&state=efg"
    private val redirectFragmentWithCode = "redirect://#code=abc&state=efg"
    private val redirectGarbageQueryAndFragmentWithCode = "redirect://?asdkfnas=sdalfas#code=abc&state=efg"

    @Test
    fun `when code in query then get auth params returns code key in map`() {
        val params = AuthURI(redirectQueryWithCode).getParams(UriCharacter.ParametersIdentifier.Query)
        assert(params.keys.contains(CODE))
    }

    @Test
    fun `when code value in query then get auth params contains correct code`() {
        val params = AuthURI(redirectQueryWithCode).getParams(UriCharacter.ParametersIdentifier.Query)
        assertEquals("abc", params[CODE])
    }

    @Test
    fun `when code in fragment then get auth params returns code key in map`() {
        val params = AuthURI(redirectFragmentWithCode).getParams(UriCharacter.ParametersIdentifier.Fragment)
        assert(params.keys.contains(CODE))
    }

    @Test
    fun `when code value in fragment then get auth params contains correct code`() {
        val params = AuthURI(redirectFragmentWithCode).getParams(UriCharacter.ParametersIdentifier.Fragment)
        assertEquals("abc", params[CODE])
    }

    @Test
    fun `given garbage query when code in fragment then get auth params returns code key in map`() {
        val params = AuthURI(redirectGarbageQueryAndFragmentWithCode)
                .getParams(UriCharacter.ParametersIdentifier.Fragment)
        assert(params.keys.contains(CODE))
    }

    @Test
    fun `given garbage query when code value in fragment then get auth params contains correct code`() {
        val params = AuthURI(redirectGarbageQueryAndFragmentWithCode)
                .getParams(UriCharacter.ParametersIdentifier.Fragment)
        assertEquals("abc", params[CODE])
    }
}