package dev.kkorolyov.pieline.trace

import io.grpc.ClientInterceptor
import io.grpc.ServerInterceptor
import io.jaegertracing.Configuration
import io.jaegertracing.Configuration.ReporterConfiguration
import io.jaegertracing.Configuration.SamplerConfiguration
import io.opentracing.Span
import io.opentracing.Tracer
import io.opentracing.contrib.grpc.TracingClientInterceptor
import io.opentracing.contrib.grpc.TracingServerInterceptor
import io.opentracing.tag.Tags
import io.opentracing.util.GlobalTracer

/**
 * Registers and returns a global tracer for [service].
 */
fun makeTracer(service: String): Tracer =
	Configuration(service)
		.withSampler(
			SamplerConfiguration.fromEnv()
				.withType("const")
				.withParam(1)
		)
		.withReporter(
			ReporterConfiguration.fromEnv()
				.withLogSpans(true)
		)
		.tracer.also { GlobalTracer.registerIfAbsent(it) }

/**
 * Returns a gRPC client interceptor.
 */
fun makeClientInterceptor(tracer: Tracer): ClientInterceptor =
	TracingClientInterceptor.newBuilder()
		.withTracer(tracer)
		.withStreaming()
		.withVerbosity()
		.build()

/**
 * Returns a gRPC server interceptor.
 */
fun makeServerInterceptor(tracer: Tracer): ServerInterceptor =
	TracingServerInterceptor.newBuilder()
		.withTracer(tracer)
		.withStreaming()
		.withVerbosity()
		.build()

/**
 * Starts a new active span with a given [operationName], [kind], and [parent] span.
 */
fun Tracer.span(operationName: String, kind: String = Tags.SPAN_KIND_SERVER, parent: Span? = null): UsableSpan =
	buildSpan(operationName)
		.withTag(Tags.SPAN_KIND, kind)
		.asChildOf(parent)
		.start()
		.let {
			UsableSpan(it, activateSpan(it))
		}
