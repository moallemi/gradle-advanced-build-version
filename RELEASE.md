# How to prepare for a new version

First of all increase the plugin version in `publish.gradle` file. 
Because all integration tests are based on building and publishing the
current code on localMaven.

### Maven Central
~~After finishing development create a `release/x.x.x` branch and wait for Github Actions to finish deploying.
Then you should close the repo in [sonatype](https://oss.sonatype.org/) and publish manually in bintray panel.~~

As of January 2026, the Maven Central repository is no longer supported,
please use the [Gradle Plugin Portal](https://plugins.gradle.org/) instead.

### Gradle Plugin Portal
Run plugin portal > publishPlugin gradle task