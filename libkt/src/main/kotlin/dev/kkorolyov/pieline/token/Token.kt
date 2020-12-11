package dev.kkorolyov.pieline.token

import io.grpc.Context
import io.grpc.Metadata

/**
 * Internal context key for storing tokens.
 */
private val tokenKey = Context.key<String>("token")

/**
 * Returns a new context with access [token].
 */
fun Context.withToken(token: String?): Context = token?.let {
	withValue(tokenKey, it)
} ?: this

/**
 * This context's access token.
 */
val Context.token: String?
	get() = tokenKey.get(this)

/**
 * `Authorization` header's access token.
 */
val Metadata.token: String?
	get() = get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER))?.removePrefix("Bearer ")
