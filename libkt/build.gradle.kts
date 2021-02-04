import org.gradle.api.JavaVersion.VERSION_14

tasks.wrapper {
	gradleVersion = File("gradle-version").useLines { it.firstOrNull() }
	distributionType = Wrapper.DistributionType.ALL
}

plugins {
	kotlin("jvm") version "1.4.30"
	`java-library`
	`maven-publish`
}

group = "dev.kkorolyov.pieline"
description = "Shared library utilities"

java {
	sourceCompatibility = VERSION_14
	targetCompatibility = VERSION_14

	withSourcesJar()
	withJavadocJar()
}

repositories {
	jcenter()
}

dependencies {
	val grpcVersion: String by project
	val opentracingVersion: String by project
	val opentracingGrpcVersion: String by project
	val jaegerVersion: String by project

	api("io.grpc:grpc-api:$grpcVersion")
	api("io.opentracing:opentracing-api:$opentracingVersion")
	api("io.opentracing.contrib:opentracing-grpc:$opentracingGrpcVersion")
	implementation("io.jaegertracing:jaeger-client:$jaegerVersion")

	dependencyLocking {
		lockAllConfigurations()
	}
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
