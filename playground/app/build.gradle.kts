import me.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import me.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.GIT_COMMIT_COUNT

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.android)
}

apply(plugin = "me.moallemi.advanced-build-version")

configure<AdvancedBuildVersionConfig> {
  nameOptions {
    versionMajor(1)
    versionMinor(3)
    versionPatch(6)
    versionBuild(8)
  }
  codeOptions {
    versionCodeType(GIT_COMMIT_COUNT)
  }
  outputOptions {
    renameOutput(true)
    nameFormat("\${appName}-\${buildType}-\${versionName}-\${versionCode}")
  }
}

val advancedVersioning = project.extensions.getByType(AdvancedBuildVersionConfig::class.java)


android {
  namespace = "com.example.gradaleadvancebuildversionplayground"
  compileSdk {
    version = release(36)
  }

  defaultConfig {
    applicationId = "com.example.gradaleadvancebuildversionplayground"
    minSdk = 24
    targetSdk = 36
    versionCode = advancedVersioning.versionCode
    versionName = advancedVersioning.versionName

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
  }
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}