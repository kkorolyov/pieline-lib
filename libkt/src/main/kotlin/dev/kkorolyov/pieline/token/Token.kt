package dev.kkorolyov.pieline.token

import io.grpc.Context
import io.grpc.Contexts
import io.grpc.Metadata
import io.grpc.Metadata.Key
import io.grpc.ServerCall
import io.grpc.ServerCall.Listener
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor

/**
 * This context's authorization token.
 */
val Context.token: String?
	get() = TOKEN_KEY.get(this)

/**
 * Server interceptor which applies incoming header authorization token to the current [Context].
 * Services have access to this token through [Context.token].
 */
val TOKEN_INTERCEPTOR = object : ServerInterceptor {
	override fun <ReqT : Any?, RespT : Any?> interceptCall(
		call: ServerCall<ReqT, RespT>,
		headers: Metadata,
		next: ServerCallHandler<ReqT, RespT>
	): Listener<ReqT> {
		return Contexts.interceptCall(
			Context.current().withValue(
				TOKEN_KEY,
				headers.get(Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER))?.removePrefix("Bearer ")
			),
			call,
			headers,
			next
		)
	}
}

/**
 * Internal context key for storing tokens.
 */
private val TOKEN_KEY = Context.key<String>("token")
