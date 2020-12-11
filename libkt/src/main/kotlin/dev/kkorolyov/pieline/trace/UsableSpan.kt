package dev.kkorolyov.pieline.trace

import io.opentracing.Scope
import io.opentracing.Span
import io.opentracing.Tracer
import io.opentracing.tag.Tags
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Starts a new active span with a given [operationName], [kind], and [parent] span.
 */
fun Tracer.span(operationName: String, kind: String = Tags.SPAN_KIND_SERVER, parent: Span? = null): UsableSpan =
	buildSpan(operationName)
		.withTag(Tags.SPAN_KIND, kind)
		.asChildOf(parent)
		.start()
		.let {
			UsableSpan(it, this)
		}

/**
 * A convenience [Span] wrapper for tracing the execution of a block.
 */
class UsableSpan(private val span: Span, private val tracer: Tracer) : Span by span {
	/**
	 * Activates this span within a new scope and returns the scope.
	 * This is an alternative to [use] that provides finer control of span lifetime.
	 */
	fun activate(): Scope = tracer.activateSpan(this)

	/**
	 * [activate] this span in a new scope, traces the execution of a [block] and returns its result.
	 * Closes the scope and finishes this span afterward.
	 * If any exceptions occur within [block], this span tags as `error`, logs the exception stack trace, and re-throws the exception.
	 */
	fun <R> use(block: (UsableSpan) -> R): R =
		try {
			block(this)
		} catch (e: Exception) {
			error(e)
			throw e
		} finally {
			finish()
		}

	/**
	 * Tags `this` span as [Tags.ERROR] and logs [e].
	 */
	fun error(e: Exception) {
		setTag(Tags.ERROR, true)
		log(
			mapOf(
				"event" to "error",
				"error.kind" to e::class.qualifiedName,
				"error.object" to e,
				"message" to e.message,
				"stack" to StringWriter().also {
					e.printStackTrace(PrintWriter(it))
				}.toString()
			)
		)
	}
}
