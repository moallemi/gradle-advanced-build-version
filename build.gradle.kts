plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.gradle.plugin-publish") version "0.20.0"
    id("java-gradle-plugin")

    alias(libs.plugins.spotless)
    id("com.adarshr.test-logger") version "4.0.0"

    id("maven-publish")
    id("signing")
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    implementation("com.android.tools.build:gradle:8.3.0")

    implementation("com.android.tools.build:gradle-api:8.3.0")
    implementation(gradleKotlinDsl())

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("io.mockk:mockk:1.13.10")
}

apply(from = "gradle/spotless.gradle")
apply(from = "gradle/jacoco.gradle")
apply(from = "gradle/publish.gradle")
apply(from = "gradle/test-setup.gradle")