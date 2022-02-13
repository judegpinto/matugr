package com.matugr.authorization_request.networking

import com.matugr.common.UriCharacter
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