package com.anindra.messages

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AndroidCompileSdkTest {

    @Test
    fun appAndMmsCompileAgainstApi37() {
        val root = generateSequence(File(System.getProperty("user.dir"))) { it.parentFile }
            .first { File(it, "settings.gradle.kts").isFile }

        listOf("app/build.gradle.kts", "mms/build.gradle.kts").forEach { path ->
            val gradleFile = File(root, path)
            assertTrue("$path must compile against API 37", Regex("""compileSdk\s*=\s*37\b""")
                .containsMatchIn(gradleFile.readText()))
        }
    }
}
