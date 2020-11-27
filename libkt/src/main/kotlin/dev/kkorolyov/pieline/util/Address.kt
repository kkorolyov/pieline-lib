package dev.kkorolyov.pieline.util

/**
 * Convenience address encapsulation.
 */
data class Address(val host: String, val port: Int) {
	companion object {
		/**
		 * Returns a new [Address] for a system [env] variable following the format `env="<host>:<port>"`.
		 */
		fun forEnv(env: String): Address {
			val (host, port) = System.getenv(env)?.split(":") ?: throw IllegalArgumentException("no value for env[$env]")
			return Address(host, Integer.parseInt(port))
		}
	}
}
