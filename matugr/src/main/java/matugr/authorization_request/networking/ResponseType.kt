package matugr.authorization_request.networking

import com.squareup.moshi.Json
import matugr.common.oauth.CODE

/**
 * Networking class type leveraged by moshi
 *
 * See README: Response Type
 */
enum class ResponseType(val jsonValue: String) {
    @Json(name = CODE) Code(CODE)
}
