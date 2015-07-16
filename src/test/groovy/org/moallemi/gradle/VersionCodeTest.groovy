package org.moallemi.gradle

import org.fest.assertions.Condition
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.moallemi.gradle.internal.VersionCodeType
import org.testng.Reporter
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.fest.assertions.Assertions.assertThat

public class VersionCodeTest {
    private Project project;
    private static final PLUGIN_ID = 'org.moallemi.advanced-build-version';

    @BeforeMethod
    public void setUp() throws Exception {
        project = ProjectBuilder.builder().build()

        project.task("clean")
        project.task("build")

        project.allprojects {
            project.apply plugin: PLUGIN_ID
        }
    }

    @Test
    public void testDate() throws Exception {
        project.advancedVersioning {
            codeOptions {
                versionCodeType VersionCodeType.DATE
            }
        }
        int versionCode = project.advancedVersioning.versionCode
        Reporter.log("versionCode :: DATE = " + versionCode, true);
        assertThat(versionCode).is(new SmallerThanCondition(Integer.MAX_VALUE))
    }

    @Test
    public void testJalaiDate() throws Exception {
        project.advancedVersioning {
            codeOptions {
                versionCodeType VersionCodeType.JALALI_DATE
            }
        }
        int versionCode = project.advancedVersioning.versionCode
        Reporter.log("versionCode :: JALALI_DATE = " + versionCode, true);
        assertThat(versionCode).is(new SmallerThanCondition(Integer.MAX_VALUE))
    }

    @Test
    public void testAutoIncrementDate() throws Exception {
        project.advancedVersioning {
            codeOptions {
                versionCodeType VersionCodeType.AUTO_INCREMENT_DATE
            }
        }
        int versionCode = project.advancedVersioning.versionCode
        Reporter.log("versionCode :: AUTO_INCREMENT_DATE = " + versionCode, true);
        assertThat(versionCode).is(new SmallerThanCondition(Integer.MAX_VALUE))
    }

    @Test
    public void testAutoIncrementOneStep() {
        int currentVersionCode = project.advancedVersioning.versionCode
        project.advancedVersioning {
            codeOptions {
                versionCodeType VersionCodeType.AUTO_INCREMENT_ONE_STEP
            }
        }

        assertThat(currentVersionCode).is(project.advancedVersioning.versionCode + 1)
    }

    @Test
    public void testAutoCreatingVersionPropertiesFile() {
        def versionFile = new File("version.properties")
        versionFile.delete()

        project.advancedVersioning {
            codeOptions {
                versionCodeType VersionCodeType.AUTO_INCREMENT_DATE
            }
        }

        assertThat(versionFile.exists()).is(true)
        assertThat(project.advancedVersioning.versionCode).is(1)
    }

    class SmallerThanCondition extends Condition<Integer> {

        private int first;

        public SmallerThanCondition(Integer first) {
            super()
            this.first = first
        }

        @Override
        boolean matches(Integer value) {
            return value < first
        }
    }

}