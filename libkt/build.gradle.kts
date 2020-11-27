import org.gradle.api.JavaVersion.VERSION_14

tasks.wrapper {
	gradleVersion = File("gradle-version").useLines { it.firstOrNull() }
	distributionType = Wrapper.DistributionType.ALL
}

plugins {
	kotlin("jvm") version "1.4.20"
	`java-library`
	`maven-publish`
}

group = "dev.kkorolyov.pieline"
version = "0.1"
description = "Shared library utilities"

java {
	sourceCompatibility = VERSION_14
	targetCompatibility = VERSION_14
}

repositories {
	jcenter()
}

dependencies {
	val opentracingVersion: String by project
	val opentracingGrpcVersion: String by project
	val jaegerVersion: String by project

	api("io.opentracing:opentracing-api:$opentracingVersion")
	implementation("io.opentracing.contrib:opentracing-grpc:$opentracingGrpcVersion")
	implementation("io.jaegertracing:jaeger-client:$jaegerVersion")
}

publishing {
	publications {
		create<MavenPublication>("mvn") {
			from(components["java"])
		}
	}

	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/kkorolyov/pieline-lib")
			credentials {
				username = System.getenv("GITHUB_ACTOR")
				password = System.getenv("GITHUB_TOKEN")
			}
		}
	}
}
