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

package com.matugr.di

import com.matugr.authorization_request.oauth.SecureRandomGenerator
import dagger.Module
import dagger.Provides
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import javax.inject.Named

const val digestSHA256 = "SHA-256"
const val state_length = "16"
const val codeVerifier_length = "64"

@Module
class AuthModelModule {

    @Provides
    fun providesSecureRandom(): SecureRandom {
        return SecureRandom()
    }

    @Provides
    @Named(digestSHA256)
    fun providesDigestSHA256(): MessageDigest? {
        return try {
            MessageDigest.getInstance(digestSHA256)
        }
        catch (e: NoSuchAlgorithmException) {
            return null
        }
    }

    @Provides
    @Named(state_length)
    internal fun providesStateSecureRandomBase64(secureRandom: SecureRandom, base64Encoder: Base64.Encoder): SecureRandomGenerator {
        return SecureRandomGenerator(secureRandom, state_length.toInt(), base64Encoder)
    }

    /**
     * URL-safe encoder with no padding or new lines.
     */
    @Provides
    fun provideBase64Encoder(): Base64.Encoder {
        return Base64.getUrlEncoder().withoutPadding()
    }

    @Provides
    @Named(codeVerifier_length)
    internal fun providesCodeVerifierSecureRandomBase64(secureRandom: SecureRandom, base64Encoder: Base64.Encoder): SecureRandomGenerator {
        return SecureRandomGenerator(secureRandom, codeVerifier_length.toInt(), base64Encoder)
    }
}
