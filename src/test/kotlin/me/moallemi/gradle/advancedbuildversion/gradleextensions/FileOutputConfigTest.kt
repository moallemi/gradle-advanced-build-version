/*
 * Copyright 2020 Reza Moallemi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.moallemi.gradle.advancedbuildversion.gradleextensions

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import junit.framework.Assert.assertEquals
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.junit.After
import org.junit.Test

class FileOutputConfigTest {

    private val project: Project = mockk {
        every { name } returns APP_NAME
        every { rootProject.name } returns PROJECT_NAME
    }

    private val variants: DomainObjectSet<ApplicationVariant> = mockk()

    private val variant: ApplicationVariant = mockk {
        every { flavorName } returns FLAVOR_NAME
        every { buildType.name } returns BUILD_TYPE
        every { versionName } returns VERSION_NAME
        every { versionCode } returns VERSION_CODE
    }

    private val fileOutputConfig = FileOutputConfig(project)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `must not rename apk by default`() {
        val variants: DomainObjectSet<ApplicationVariant> = mockk()

        fileOutputConfig.renameOutputApkIfPossible(variants)

        verify(exactly = 0) {
            variants.all(any<Action<in ApplicationVariant>>())
        }
    }

    @Test
    fun `must not rename apk if renameOutput = false`() {
        val variants: DomainObjectSet<ApplicationVariant> = mockk()

        fileOutputConfig.renameOutput(false)
        fileOutputConfig.renameOutputApkIfPossible(variants)

        verify(exactly = 0) {
            variants.all(any<Action<in ApplicationVariant>>())
        }
    }

    @Test
    fun `must rename apk if renameOutput = true and FLAVOR_NAME is not null`() {
        val variantsSlot = slot<Action<in ApplicationVariant>>()
        every { variants.all(capture(variantsSlot)) } answers { variantsSlot.captured.execute(variant) }

        val baseVariantOutputSlot = slot<String>()
        val baseVariantOutput: BaseVariantOutputImpl = mockk {
            every { outputFileName = capture(baseVariantOutputSlot) } just runs
        }

        val outputsSlot = slot<Action<BaseVariantOutput>>()
        every { variant.outputs.all(capture(outputsSlot)) } answers { outputsSlot.captured.execute(baseVariantOutput) }

        fileOutputConfig.renameOutput(true)
        fileOutputConfig.renameOutputApkIfPossible(variants)

        verify {
            variants.all(any<Action<in ApplicationVariant>>())
            variant.outputs.all(any<Action<BaseVariantOutput>>())
        }

        val apkFileName = "$APP_NAME-$FLAVOR_NAME-$BUILD_TYPE-$VERSION_NAME.apk"
        assertEquals(apkFileName, baseVariantOutputSlot.captured)
    }

    @Test
    fun `must rename apk if renameOutput = true and FLAVOR_NAME is null`() {
        every { variant.flavorName } returns null

        val variantsSlot = slot<Action<in ApplicationVariant>>()
        every { variants.all(capture(variantsSlot)) } answers { variantsSlot.captured.execute(variant) }

        val baseVariantOutputSlot = slot<String>()
        val baseVariantOutput: BaseVariantOutputImpl = mockk {
            every { outputFileName = capture(baseVariantOutputSlot) } just runs
        }

        val outputsSlot = slot<Action<BaseVariantOutput>>()
        every { variant.outputs.all(capture(outputsSlot)) } answers { outputsSlot.captured.execute(baseVariantOutput) }

        fileOutputConfig.renameOutput(true)
        fileOutputConfig.renameOutputApkIfPossible(variants)

        verify {
            variants.all(any<Action<in ApplicationVariant>>())
            variant.outputs.all(any<Action<BaseVariantOutput>>())
        }

        val apkFileName = "$APP_NAME-$BUILD_TYPE-$VERSION_NAME.apk"
        assertEquals(apkFileName, baseVariantOutputSlot.captured)
    }

    @Test
    fun `must rename apk if renameOutput = true and nameFormat MyApp-google-play-VERSION-NAME`() {
        every { variant.flavorName } returns null

        val variantsSlot = slot<Action<in ApplicationVariant>>()
        every { variants.all(capture(variantsSlot)) } answers { variantsSlot.captured.execute(variant) }

        val baseVariantOutputSlot = slot<String>()
        val baseVariantOutput: BaseVariantOutputImpl = mockk {
            every { outputFileName = capture(baseVariantOutputSlot) } just runs
        }

        val outputsSlot = slot<Action<BaseVariantOutput>>()
        every { variant.outputs.all(capture(outputsSlot)) } answers { outputsSlot.captured.execute(baseVariantOutput) }

        fileOutputConfig.renameOutput(true)
        fileOutputConfig.nameFormat("MyApp-google-play-\$versionName")
        fileOutputConfig.renameOutputApkIfPossible(variants)

        verify {
            variants.all(any<Action<in ApplicationVariant>>())
            variant.outputs.all(any<Action<BaseVariantOutput>>())
        }

        val apkFileName = "MyApp-google-play-$VERSION_NAME.apk"
        assertEquals(apkFileName, baseVariantOutputSlot.captured)
    }

    companion object {
        private const val APP_NAME = "APP_NAME"
        private const val PROJECT_NAME = "PROJECT_NAME"
        private const val FLAVOR_NAME = "FLAVOR_NAME"
        private const val BUILD_TYPE = "BUILD_TYPE"
        private const val VERSION_NAME = "1.2.3.4"
        private const val VERSION_CODE = 1234
    }
}
