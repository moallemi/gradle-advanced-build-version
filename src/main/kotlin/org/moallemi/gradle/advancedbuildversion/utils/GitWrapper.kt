package org.moallemi.gradle.advancedbuildversion.utils

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Project

class GitWrapper(private val project: Project) {

    private val repository: Repository by lazy {
        FileRepositoryBuilder()
            .readEnvironment()
            .findGitDir(project.projectDir)
            .build()
    }

    private val git: Git by lazy {
        Git.wrap(repository)
    }

    fun getCommitsNumberInBranch() =
        git.log().call().count()
}
