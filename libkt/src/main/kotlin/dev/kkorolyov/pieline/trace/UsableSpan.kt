package dev.kkorolyov.pieline.trace

import io.opentracing.Scope
import io.opentracing.Span
import io.opentracing.tag.Tags
import java.io.PrintWriter
import java.io.StringWriter

/**
 * A convenience [Span] wrapper for tracing the execution of a block.
 */
class UsableSpan(private val span: Span, private val scope: Scope) : Span by span, AutoCloseable {
	/**
	 * Traces the execution of a [block] and returns its result.
	 * Closes this span and its scope afterward.
	 * If any exceptions occur within [block], this span tags as `error`, logs the exception stack trace, and re-throws the exception.
	 */
	fun <R> use(block: (UsableSpan) -> R): R =
		try {
			block(this)
		} catch (e: Exception) {
			error(e)
			throw e
		} finally {
			close()
		}

	/**
	 * Tags `this` span as [Tags.ERROR] and logs [e].
	 */
	private fun error(e: Exception) {
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

	override fun close() {
		finish()
		scope.close()
	}
}
