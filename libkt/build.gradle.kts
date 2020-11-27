import org.gradle.api.JavaVersion.VERSION_14
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

tasks.wrapper {
	gradleVersion = File("gradle-version").useLines { it.firstOrNull() }
	distributionType = Wrapper.DistributionType.ALL
}

plugins {
	kotlin("jvm") version "1.3.72"
	`java-library`
}

subprojects {
	apply<KotlinPlatformJvmPlugin>()
	apply<JavaLibraryPlugin>()

	group = "dev.kkorolyov"
	version = "0.1"

	java {
		sourceCompatibility = VERSION_14
		targetCompatibility = VERSION_14
	}

	repositories {
		jcenter()
	}

	dependencies {
		implementation(kotlin("stdlib-jdk8"))
	}

	// TODO Publish
}

project(":tracing") {
	description = "Common tracing functionality"

	dependencies {
		val opentracingVersion: String by project
		val opentracingGrpcVersion: String by project
		val jaegerVersion: String by project

		api("io.opentracing:opentracing-api:$opentracingVersion")
		implementation("io.opentracing.contrib:opentracing-grpc:$opentracingGrpcVersion")
		implementation("io.jaegertracing:jaeger-client:$jaegerVersion")
	}
}

project(":util") {
	description = "Miscellaneous helpful utility constructs"
}
