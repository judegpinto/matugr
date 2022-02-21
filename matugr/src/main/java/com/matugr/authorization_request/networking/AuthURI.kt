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

package com.matugr.authorization_request.networking

import com.matugr.common.external.UriCharacter
import java.net.URI

/**
 * [AuthURI] is created from authorization response parameters.
 * Unwraps and provides parameters.
 */
internal class AuthURI(private val uri: URI) {

    private val keyValueDelimiter = "="

    constructor(uri: String): this(URI.create(uri))

    fun getParams(parametersIdentifier: UriCharacter.ParametersIdentifier): Map<String, String> {
        return when(parametersIdentifier) {
            is UriCharacter.ParametersIdentifier.Query -> getParamsFromSegment(uri.query)
            is UriCharacter.ParametersIdentifier.Fragment -> getParamsFromSegment(uri.fragment)
        }
    }

    private fun getParamsFromSegment(segment: String): Map<String, String> {
        val keyValues = segment.split(delimiters = arrayOf(UriCharacter.Separator.character))
        return keyValues.map { it.substringBefore(keyValueDelimiter) to it.substringAfter(keyValueDelimiter) }.toMap()
    }
}