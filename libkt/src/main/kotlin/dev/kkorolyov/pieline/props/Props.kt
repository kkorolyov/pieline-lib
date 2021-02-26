package dev.kkorolyov.pieline.props

import java.util.Properties

/**
 * Provides access to overridable system properties.
 * Loads default properties from a file.
 * When retrieving properties, attempts to find a matching environment variable, else defers to the default properties resource.
 */
class Props(
	/** Path to default properties resource */
	resource: String
) {
	private val props = Properties().apply {
		javaClass.getResourceAsStream(resource).use {
			load(it)
		}
	}

	/**
	 * Returns the overridable property value for [key].
	 */
	operator fun get(key: String): String = System.getenv(key) ?: props.getProperty(key)
}
