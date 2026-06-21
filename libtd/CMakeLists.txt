cmake_minimum_required(VERSION 3.22.1)
project(tdjson_jni)

# CMakeLists.txt lives in td-core-src/libtd/
# Paths below are relative to this file's location.

# TDLib headers from the sibling td-kmp-core module
set(TDJSON_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../td-kmp-core/native_libs/include")

# Prebuilt libtdjson.so for the current ABI
set(TDJSON_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../td-kmp-core/src/androidMain/jniLibs/${ANDROID_ABI}")

# Import libtdjson.so as a prebuilt shared library
add_library(tdjson SHARED IMPORTED)
set_target_properties(tdjson PROPERTIES
    IMPORTED_LOCATION "${TDJSON_LIB_DIR}/libtdjson.so"
)

# Build the JNI bridge shared library
add_library(tdjson_jni SHARED
    src/androidMain/cpp/tdjson_jni.cpp
)

target_include_directories(tdjson_jni PRIVATE
    ${TDJSON_INCLUDE_DIR}
)

target_link_libraries(tdjson_jni
    tdjson
    log
)
