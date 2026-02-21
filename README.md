# Gradle Advanced Build Version Plugin

A Gradle plugin that automatically generates Android version code and version name based on git commits count, date, and [Semantic Versioning](https://semver.org/).

[![GitHub Workflow Status](https://github.com/moallemi/gradle-advanced-build-version/workflows/CI/badge.svg)](https://github.com/moallemi/gradle-advanced-build-version/actions?query=workflow%3ACI)
[![Coverage](https://codecov.io/gh/moallemi/gradle-advanced-build-version/branch/master/graph/badge.svg)](https://codecov.io/gh/moallemi/gradle-advanced-build-version)

## Contents
1. [Installation](#installation)
2. [How to use](#how-to-use)
3. [Version Name Configuration](#version-name-configuration)
4. [Version Code Configuration](#version-code-configuration)
5. [File output options](#file-output-options)

## Installation

Add the advanced-build-version plugin to your build script and use the property `advancedVersioning.versionName` and
`advancedVersioning.versionCode` where you need:

| Gradle Advanced Build Version | Minimum AGP Version |
|:-----------------------------:|:-------------------:|
|             4.0.0             |        9.0.0        |
|             3.5.0             |        8.1.0        |
|             3.0.0             |        8.1.0        |
|             2.0.2             |        8.0.0        |
|             2.0.0             |        7.0.0        |
|             1.7.3             |        3.0.0        |

Using the plugins DSL:
```kotlin
plugins {
    id("me.moallemi.advanced-build-version") version "4.0.0"
}
```

For other installation options, please visit [plugin page](https://plugins.gradle.org/plugin/me.moallemi.advanced-build-version) in Gradle Plugins Portal.

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

`versionCodeType` can be one of the following values:

 * `GIT_COMMIT_COUNT` — total number of commits in the current branch
 * `AUTO_INCREMENT_STEP` — file-based auto-increment (e.g. 26)

### GIT_COMMIT_COUNT

If you are using CIs like Jenkins, CircleCI or GitHub Actions, and you want to use `GIT_COMMIT_COUNT`, you should clone your repository with full history or unshallow an already existing one.

For GitHub Actions use `fetch-depth: 0`:

```yaml
steps:
- name: Checkout
  uses: actions/checkout@v4
  with:
    fetch-depth: 0
```

### AUTO_INCREMENT_STEP

Stores `AI_VERSION_CODE` in a `version.properties` file in the build.gradle directory. You can change the `dependsOnTasks` property to specify which tasks should increase the version code (default is every task that contains 'release' in its name).

You can set a step different from 1:
```groovy
advancedVersioning {
  codeOptions {
    versionCodeType 'AUTO_INCREMENT_STEP'
    versionCodeStep 2 // default is 1
  }
}
```

### lastLegacyCode

If you are migrating from a different versioning system, you can use `lastLegacyCode` to add an offset to the generated version code:

```groovy
advancedVersioning {
  codeOptions {
    versionCodeType 'GIT_COMMIT_COUNT'
    lastLegacyCode 987650
  }
}
```

The final version code will be `lastLegacyCode + generatedCode`. 

## File output options
You can rename the output APK file by enabling the `renameOutput` option:

```groovy
advancedVersioning {
  outputOptions {
    renameOutput true
  }
}
```

If your app name is MyApp with 2.7 version name, and you are in debug mode, the output APK file name
will be: `MyApp-2.7-debug.apk`

You can customize the output name by using these params:

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
Copyright 2026 Reza Moallemi.

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
