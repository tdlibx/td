# TDLib JNI Bridge — Setup Guide

## The Problem

`libtdjson.so` in this project is the **TDLib JSON C library**. It exposes a pure C API:
```
td_json_client_create()
td_json_client_send()
td_json_client_receive()
td_json_client_execute()
td_json_client_destroy()
```

The Kotlin `JsonClient.kt` file declares `external fun` methods which require JNI symbols (`Java_org_drinkless_tdlib_JsonClient_create`, etc.). These symbols **do not exist** in `libtdjson.so` — a separate JNI bridge `.so` is needed.

## Prerequisites

Install the Android NDK via Android Studio:
**Tools → SDK Manager → SDK Tools → NDK (Side by side)**

Or via command line:
```bash
~/Library/Android/sdk/cmdline-tools/latest/bin/sdkmanager "ndk;27.0.12077973"
```

## Building the JNI Bridge

Once NDK is installed:
```bash
./gradlew :libtd:buildCMakeDebug
```

This compiles `src/androidMain/cpp/tdjson_jni.cpp` into `libtdjson_jni.so`, which maps:
- `Java_org_drinkless_tdlib_JsonClient_create` → `td_json_client_create()`
- `Java_org_drinkless_tdlib_JsonClient_send` → `td_json_client_send()`
- `Java_org_drinkless_tdlib_JsonClient_receive` → `td_json_client_receive()`
- `Java_org_drinkless_tdlib_JsonClient_execute` → `td_json_client_execute()`
- `Java_org_drinkless_tdlib_JsonClient_destroy` → `td_json_client_destroy()`

## Verifying the Fix

Run the integration test on a connected device/emulator:
```bash
./gradlew :libtd:connectedDebugAndroidTest
```

Expected output (success):
```
TdLibInitializerTest > testNativeLibraryLoadsSuccessfully PASSED
TdLibInitializerTest > testJsonClientCanCreateClient PASSED
TdLibInitializerTest > testJsonClientCanExecuteSynchronousRequest PASSED
TdLibInitializerTest > testPlatformEngineCreation PASSED
```

Expected output (JNI bridge missing):
```
TdLibInitializerTest > testNativeLibraryLoadsSuccessfully FAILED
  TDLib native library failed to load.
  Cause: No implementation found for long org.drinkless.tdlib.JsonClient.create()
  Fix: Install Android NDK, then run ./gradlew :libtd:buildCMakeDebug
```

## Files Involved

| File | Purpose |
|------|---------|
| `src/androidMain/cpp/tdjson_jni.cpp` | JNI bridge C++ source |
| `CMakeLists.txt` | CMake build config for the JNI bridge |
| `src/androidMain/kotlin/org/drinkless/tdlib/JsonClient.kt` | Kotlin `external` declarations |
| `src/androidMain/kotlin/org/drinkless/tdlib/TdLibInitializer.kt` | Safe init helper |
| `src/androidInstrumentedTest/.../TdLibInitializerTest.kt` | Integration tests |
