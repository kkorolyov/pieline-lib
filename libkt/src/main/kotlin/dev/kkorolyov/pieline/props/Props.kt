package dev.kkorolyov.pieline.props

import java.io.InputStream
import java.util.Properties

/**
 * Provides access to overridable system properties.
 * Loads default properties from a file.
 * When retrieving properties, attempts to find a matching environment variable, else defers to the default properties resource.
 */
class Props(
	/** Default properties resource */
	resource: InputStream
) {
	private val props = Properties().apply {
		load(resource)
	}

	/**
	 * Returns the overridable property value for [key].
	 */
	operator fun get(key: String): String = System.getenv(key) ?: props.getProperty(key)
}
