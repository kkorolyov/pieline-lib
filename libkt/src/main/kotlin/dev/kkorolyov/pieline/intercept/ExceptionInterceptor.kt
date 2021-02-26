package dev.kkorolyov.pieline.intercept

import io.grpc.ForwardingServerCall
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCall.Listener
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status

/**
 * Intercepts exceptions occurring in gRPC servers.
 */
class ExceptionInterceptor(
	/** Invoked with intercepted exceptions */
	private val handler: (Throwable) -> Unit
) : ServerInterceptor {
	override fun <ReqT, RespT> interceptCall(
		call: ServerCall<ReqT, RespT>,
		headers: Metadata?,
		next: ServerCallHandler<ReqT, RespT>
	): Listener<ReqT> = next.startCall(ExceptionLoggingServerCall(call), headers)

	private inner class ExceptionLoggingServerCall<ReqT, RespT>(delegate: ServerCall<ReqT, RespT>) :
		ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(delegate) {
		override fun close(status: Status, trailers: Metadata?) {
			status.cause?.let { handler(it) }
			super.close(status, trailers)
		}
	}
}
