package org.moallemi.gradle.internal

import org.gradle.api.GradleScriptException;


class VersionNameOptions {

    private int mVersionMajor = 0
    private int mVersionMinor = -1
    private int mVersionPatch = -1
    private int mVersionBuild = -1

    void versionMajor(int major) {
        mVersionMajor = major
    }

    void versionMinor(int minor) {
        mVersionMinor = minor
    }

    void versionPatch(int patch) {
        mVersionPatch = patch
    }

    void versionBuild(int build) {
        mVersionBuild = build
    }

    String getVersionName() {
        if (mVersionMajor < 0) {
            throw new GradleScriptException("nameOptions.versionMinor could not be less than 0", new Throwable());
        }
        if (mVersionMinor < -1) {
            throw new GradleScriptException("nameOptions.versionMinor could not be less than 0", new Throwable());
        }
        if (mVersionPatch < -1) {
            throw new GradleScriptException("nameOptions.versionPatch could not be less than 0", new Throwable());
        }
        if (mVersionBuild < -1) {
            throw new GradleScriptException("nameOptions.versionBuild could not be less than 0", new Throwable());
        }

        String build = mVersionBuild != -1 ? "." + mVersionBuild : ""
        String patch = mVersionPatch != -1 ? "." + mVersionPatch : mVersionBuild != -1 ? ".0" : ""
        String minor = mVersionMinor != -1 ? "." + mVersionMinor : ".0"
        String major = String.valueOf(mVersionMajor)


        return major + minor + patch + build
    }
}
