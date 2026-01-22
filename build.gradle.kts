plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradlePublish)
    id("java-gradle-plugin")

    alias(libs.plugins.spotless)
    alias(libs.plugins.test.logger)

    id("maven-publish")
    id("signing")
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.android.gradlePlugin)

    implementation(libs.android.gradlePlugin.api)
    implementation(gradleKotlinDsl())

    testImplementation(gradleTestKit())
    testImplementation(libs.junit)
    testImplementation(libs.hamerchat)
    testImplementation(libs.mockk)
}

apply(from = "gradle/spotless.gradle")
apply(from = "gradle/jacoco.gradle")
apply(from = "gradle/publish.gradle")
apply(from = "gradle/test-setup.gradle")