# Gradle Advanced Build Version Plugin

A plugin to generate the Android version code and version name automatically.

## Contents
1. [Installation](#installation)
2. [How to use](#how-to-use)
3. [Version Name Configuration](#version-name-configuration)
4. [Version Code Configuration](#version-code-configuration)
5. [File output options](#file-output-options)

## Installation

Add the advanced-build-version plugin to your build script and use the property `advancedVersioning.versionName` and
`advancedVersioning.versionCode` where you need:

```groovy
buildscript {
  repositories {
      jcenter()
  }

  dependencies {
      classpath 'org.moallemi.gradle.advanced-build-version:gradle-plugin:1.5.1'
  }
}

apply plugin: 'org.moallemi.advanced-build-version'
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

You can customize version name in your `build.gradle` file as follow:

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

there is no need to specify all params because they will be handled automatically. for example

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

```groovy
advancedVersioning {
    codeOptions {
        versionCodeType org.moallemi.gradle.internal.VersionCodeType.DATE
    }
}
```

`versionCodeType` can be one of following params:
 
 * `org.moallemi.gradle.internal.VersionCodeType.DATE` formatted number e.g.: 1501101614
 * `org.moallemi.gradle.internal.VersionCodeType.JALALI_DATE` will output 931017
 * `org.moallemi.gradle.internal.VersionCodeType.AUTO_INCREMENT_DATE` will output 101101614
 * `org.moallemi.gradle.internal.VersionCodeType.AUTO_INCREMENT_ONE_STEP` will output e.g: 24. this
 property stores AI_VERSION_CODE in `version.properties` file in build.gradle directory, you may
 also change `dependsOnTasks` property to specify that on witch tasks should increase version code
 (default is every task that contains 'release' in its name)

```groovy
advancedVersioning {
  codeOptions {
      versionCodeType org.moallemi.gradle.internal.VersionCodeType.AUTO_INCREMENT_ONE_STEP
      dependsOnTasks 'release' // defaultValue
  }
}
```

Setting multiple tasks for `dependsOnTasks` property:
```groovy
advancedVersioning {
  codeOptions {
      versionCodeType org.moallemi.gradle.internal.VersionCodeType.AUTO_INCREMENT_ONE_STEP
      dependsOnTasks 'debug', 'release', 'assemble'
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

If your app name is MyApp with 2.7 version name and you are in debug mode, the output apk file name 
will be: `MyApp-2.7-debug.apk`

You can customize the output name by using this params:

* `${appName}`: name of main module
* `${projectName}`: name of root project
* `${flavorName}`: flavor name
* `${buildType}`: build type
* `${versionName}`: version name
* `${versionCode}`: version code

```groovy
advancedVersioning {
  outputOptions {
      renameOutput true
      nameFormat '${appName}-${buildType}-${versionName}'
  }
}
```

And you can also use custom string in `nameFormat` like:

```groovy
advancedVersioning {
  outputOptions {
      renameOutput true
      nameFormat '${appName}-google-play-${versionName}'
  }
}
```

If your app name is MyApp with 4.6.1 version name the output apk file name will be: 
`MyApp-google-play-4.6.1.apk`
