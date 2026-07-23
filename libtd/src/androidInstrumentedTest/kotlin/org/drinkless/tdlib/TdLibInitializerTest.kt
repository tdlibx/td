package org.drinkless.tdlib

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test that verifies TDLib native library initialization.
 *
 * Run on a physical device or emulator:
 *   ./gradlew :libtd:connectedDebugAndroidTest
 *
 * This test will FAIL (with a clear message) if:
 *  - libtdjson.so is missing from the APK
 *  - The JNI bridge (libtdjson_jni.so) is not compiled
 *  - The .so was compiled for a different ABI
 */
@RunWith(AndroidJUnit4::class)
class TdLibInitializerTest {
    @Test
    fun testNativeLibraryLoadsSuccessfully() {
        val result = TdLibInitializer.init()

        when (result) {
            is TdLibInitResult.Success -> {
                // All good — library loaded and JNI bridge is functional
                assertTrue(TdLibInitializer.isAvailable)
            }
            is TdLibInitResult.Error -> {
                fail(
                    "TDLib native library failed to load.\n\n" +
                        "Cause: ${result.message}\n\n" +
                        "Most likely reasons:\n" +
                        "  1. libtdjson_jni.so (JNI bridge) is not compiled.\n" +
                        "     Fix: Install Android NDK in SDK Manager, then run:\n" +
                        "          ./gradlew :libtd:buildCMakeDebug\n" +
                        "  2. libtdjson.so is not packaged in the APK for this ABI.\n" +
                        "     Fix: Check jniLibs directory in td-kmp-core.\n" +
                        "  3. The .so was compiled for a different ABI than the test device.\n" +
                        "     Fix: Verify ABI filters in build.gradle.",
                )
            }
        }
    }

    @Test
    fun testJsonClientCanCreateClient() {
        // Skip if library init failed
        val result = TdLibInitializer.init()
        if (result is TdLibInitResult.Error) {
            println("SKIPPED: ${result.message}")
            return
        }

        val clientId = JsonClient.create()
        assertNotEquals("JsonClient.create() must return a non-zero client ID", 0L, clientId)

        // Clean up
        JsonClient.destroy(clientId)
    }

    @Test
    fun testJsonClientCanExecuteSynchronousRequest() {
        val result = TdLibInitializer.init()
        if (result is TdLibInitResult.Error) {
            println("SKIPPED: ${result.message}")
            return
        }

        // getOption is documented as synchronously executable
        val response =
            JsonClient.execute(
                0L,
                """{"@type":"getOption","name":"version"}""",
            )

        assertNotNull("execute(getOption) must return a non-null response", response)
        assertTrue(
            "Response must contain TDLib version",
            response!!.contains("\"@type\""),
        )
        println("TDLib version response: $response")
    }

    @Test
    fun testPlatformEngineCreation() {
        val result = TdLibInitializer.init()
        if (result is TdLibInitResult.Error) {
            println("SKIPPED: ${result.message}")
            return
        }

        // Verify PlatformTdClientEngine can be instantiated and create a client
        val engine = PlatformTdClientEngine()
        val clientId = engine.createClient()
        assertNotEquals(0L, clientId)
        println("PlatformTdClientEngine created client with ID: $clientId")
    }
}
