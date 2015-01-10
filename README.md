# Gradle Advanced Build Version Plugin

A plugin to generate the Android version code and version name automatically.

## Quick Start

Add the advanced-build-version plugin to your build script and use the property `advancedVersioning.nameOptions.versionName` and
`advancedVersioning.codeOptions.versionCode` where you need:

```

buildscript {
  repositories {
    maven() {
        url("https://oss.sonatype.org/content/groups/public")
    }
  }

  dependencies {
    classpath 'org.moallemi.gradle.advanced-build-version:gradle-plugin:1.0.0-SNAPSHOT'
  }
}

apply plugin: 'advanced-build-version'

def appVersionName = advancedVersioning.nameOptions.versionName
def appVersionCode = advancedVersioning.codeOptions.versionCode

```

## Version Name Configuration

You can customize version name in your `build.gradle` file as follow:

```
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
```
advancedVersioning {
    nameOptions {
        versionMajor 1
        versionBuild 8
    }
}
```
will output `1.0.0.8` and
```
advancedVersioning {
    nameOptions {
        versionMajor 1
        versionMinor 3
    }
}
```
will output `1.3`

def appVersionName = advancedVersioning.nameOptions.versionName

## Version Code Configuration

To customize version code in your `build.gradle` file write:

```
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
 * `org.moallemi.gradle.internal.VersionCodeType.AUTO_INCREMENT_ONE_STEP` will output e.g: 24. this property stores
 VERSION_CODE in `version.properties` file in your project structure




