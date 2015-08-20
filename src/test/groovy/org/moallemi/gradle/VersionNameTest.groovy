package org.moallemi.gradle

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test

import static org.fest.assertions.Assertions.assertThat;

public class VersionNameTest {
    private Project project;
    private static final PLUGIN_ID = 'org.moallemi.advanced-build-version';

    @BeforeMethod
    public void setUp() throws Exception {
        project = ProjectBuilder.builder().build()

        project.task("clean")
        project.task("build")

        project.buildscript {
            dependencies {
                classpath 'com.android.tools.build:gradle:1.3.0'
            }
        }
        project.allprojects {
            project.apply plugin: PLUGIN_ID
        }
    }

    @Test
    public void testMajorVersion() throws Exception {
        project.advancedVersioning {
            nameOptions {
                versionMajor 1
            }
        }

        project.version = project.advancedVersioning.versionName
        assertThat(project.version.toString()).isEqualToIgnoringCase("1.0")
    }

    @Test
    public void testMinorVersion() throws Exception {
        project.advancedVersioning {
            nameOptions {
                versionMajor 1
                versionMinor 3
            }
        }

        project.version = project.advancedVersioning.versionName
        assertThat(project.version.toString()).isEqualToIgnoringCase("1.3")
    }

    @Test
    public void testPatchVersion() throws Exception {
        project.advancedVersioning {
            nameOptions {
                versionMajor 1
                versionPatch 8
            }
        }

        project.version = project.advancedVersioning.versionName
        assertThat(project.version.toString()).isEqualToIgnoringCase("1.0.8")
    }

    @Test
    public void testBuildVersion() throws Exception {
        project.advancedVersioning {
            nameOptions {
                versionMajor 1
                versionPatch 8
                versionBuild 2
            }
        }

        project.version = project.advancedVersioning.versionName
        assertThat(project.version.toString()).isEqualToIgnoringCase("1.0.8.2")
    }

    @Test
    public void testMajorBuildVersion() throws Exception {
        project.advancedVersioning {
            nameOptions {
                versionMajor 3
                versionBuild 9
            }
        }

        project.version = project.advancedVersioning.versionName
        assertThat(project.version.toString()).isEqualToIgnoringCase("3.0.0.9")
    }

    @Test
    public void testEmptyMajorVersion() throws Exception {
        project.advancedVersioning {
            nameOptions {
                versionMinor 7
                versionBuild 4
            }
        }

        project.version = project.advancedVersioning.versionName
        assertThat(project.version.toString()).isEqualToIgnoringCase("0.7.0.4")
    }
}