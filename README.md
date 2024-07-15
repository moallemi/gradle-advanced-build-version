# Gradle Advanced Build Version Plugin

If you need automatic incremental Gradle versioning, this plugin helps you to generate the Android version code and version name automatically based on git commits number, date and [Semantic Versioning](https://semver.org/).

[![GitHub Workflow Status](https://github.com/moallemi/gradle-advanced-build-version/workflows/CI/badge.svg)](https://github.com/moallemi/gradle-advanced-build-version/actions?query=workflow%3ACI)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.moallemi.gradle/advanced-build-version/badge.svg)](https://search.maven.org/artifact/me.moallemi.gradle/advanced-build-version)
[![Coverage](https://codecov.io/gh/moallemi/gradle-advanced-build-version/branch/dev/graph/badge.svg)](https://codecov.io/gh/moallemi/gradle-advanced-build-version)

## Contents
1. [Installation](#installation)
2. [How to use](#how-to-use)
3. [Version Name Configuration](#version-name-configuration)
4. [Version Code Configuration](#version-code-configuration)
5. [File output options](#file-output-options)

## Installation

Add the advanced-build-version plugin to your build script and use the property `advancedVersioning.versionName` and
`advancedVersioning.versionCode` where you need:

| Gradle Advanced Build Version  | Minumum AGP Version |
|  :---: |  :---: |
| 3.0.0  | 8.1.0  |
| 2.0.2  | 8.0.0  |
| 2.0.0  | 7.0.0  |
| 1.7.3  | 3.0.0  |

Using the plugins DSL:
```groovy
plugins {
    id "me.moallemi.advanced-build-version" version "3.0.0"
}
```

Using legacy plugin application:
```groovy
buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath 'me.moallemi.gradle:advanced-build-version:3.0.0'
  }
}

apply plugin: 'me.moallemi.advanced-build-version'
```

## How to use

```
advancedVersioning {
  nameOptions { }
  codeOptions { }
  outputOptions { }
}

def appVersionName = advancedVersioning.versionName
def appVersionCode = advancedVersioning.versionCode
```

## Version Name Configuration

You can customize version name in your `build.gradle` file as follows:

```groovy
advancedVersioning {
  nameOptions {
    versionMajor 1
    versionMinor 3
    versionPatch 6
    versionBuild 8
  }
}
```
the above configuration will output `1.3.6.8`

there is no need to specify all params because they will be handled automatically. for example:

```groovy
advancedVersioning {
  nameOptions {
    versionMajor 1
    versionBuild 8
  }
}
```
will output `1.0.0.8` and

```groovy
advancedVersioning {
  nameOptions {
    versionMajor 1
    versionMinor 3
  }
}
```

will output `1.3`

## Version Code Configuration

To customize version code in your `build.gradle` file write:

Groovy:
```groovy
advancedVersioning {
  codeOptions {
    versionCodeType 'GIT_COMMIT_COUNT'
  }
}
```

Kotlin:
```kotlin
import me.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.*

advancedVersioning {
  codeOptions {
    versionCodeType(GIT_COMMIT_COUNT)
  }
}
```

`versionCodeType` can be one of following params:
 
 * `GIT_COMMIT_COUNT` will output total commits number in current branch
 * `AUTO_INCREMENT_STEP` will output e.g: 26

If you are using CIs like Jenkins, CircleCI or GitHub Actions, and you want to use `GIT_COMMIT_COUNT`, consider checking out repositories with `--depth=1` parameter. You should clone your repository with full history or unshallow an already existing one.

For GitHub Actions consider `fetch-depth: 0`:

```yaml
steps:
- name: Checkout
  uses: actions/checkout@v4
  with:
    fetch-depth: 0
```

`AUTO_INCREMENT_STEP` store AI_VERSION_CODE in `version.properties` file in build.gradle 
 directory, you may also change `dependsOnTasks` property to specify that on witch tasks should increase version code
 (default is every task that contains 'release' in its name)

`AUTO_INCREMENT_STEP` allows you to set a step different from 1:
```groovy
advancedVersioning {
  codeOptions {
    versionCodeType 'AUTO_INCREMENT_STEP'
    versionCodeStep 2 //default to 1
  }
}
``` 

## File output options
You can also rename the output generated apk file with this plugin. it can be done just by enabling 
the `renameOutput` option:

```groovy
advancedVersioning {
  outputOptions {
    renameOutput true
  }
}
```

If your app name is MyApp with 2.7 version name, and you are in debug mode, the output apk file name 
will be: `MyApp-2.7-debug.apk`

**NOTE for v 2.x.x Only:** Android Gradle Plugin 4.1.0 [drops support](https://developer.android.com/studio/known-issues#variant_output) for renaming apk. We are using a workaround to keep renaming option for gradle-advanced-build-version library.
So if you are using AGP 4.1.0+, you have to add `advancedVersioning.renameOutputApk()` after android configuration. The order is important:

```groovy
advancedVersioning {
  outputOptions {
    renameOutput true
  }
}
android {
  ...
}
advancedVersioning.renameOutputApk()
```

You can customize the output name by using this params:

* `${appName}`: name of main module
* `${projectName}`: name of root project
* `${flavorName}`: flavor name
* `${buildType}`: build type
* `${versionName}`: version name
* `${versionCode}`: version code

Groovy:
```groovy
advancedVersioning {
  outputOptions {
    renameOutput true
    nameFormat '${appName}-${buildType}-${versionName}'
  }
}
```

Kotlin:
```kotlin
advancedVersioning {
  outputOptions {
    renameOutput(true)
    nameFormat("\${appName}-\${buildType}-\${versionName}-\${versionCode}")
  }
}
```

And you can also use custom string in `nameFormat` like:

Groovy:
```groovy
advancedVersioning {
  outputOptions {
    renameOutput true
    nameFormat '${appName}-google-play-${versionName}'
  }
}
```

Kotlin:
```kotlin
advancedVersioning {
  outputOptions {
    renameOutput(true)
    nameFormat("\${appName}-google-play-\${versionName}")
  }
}
```

If your app name is MyApp with 4.6.1 version name the output apk file name will be: 
`MyApp-google-play-4.6.1.apk`

## License

```
Copyright 2024 Reza Moallemi.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements. See the NOTICE file distributed with this work for
additional information regarding copyright ownership. The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
