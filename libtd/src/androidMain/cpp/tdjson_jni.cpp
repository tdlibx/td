#include <jni.h>
#include "td_json_client.h"

// JNI bridge: maps Java external methods in org.drinkless.tdlib.JsonClient
// to the TDLib JSON C API (td_json_client_*) exported by libtdjson.so.

extern "C" {

JNIEXPORT jlong JNICALL
Java_org_drinkless_tdlib_JsonClient_create(JNIEnv *env, jobject obj) {
    return (jlong) td_json_client_create();
}

JNIEXPORT void JNICALL
Java_org_drinkless_tdlib_JsonClient_send(JNIEnv *env, jobject obj, jlong clientId, jstring request) {
    const char *req = env->GetStringUTFChars(request, nullptr);
    td_json_client_send((void *) clientId, req);
    env->ReleaseStringUTFChars(request, req);
}

JNIEXPORT jstring JNICALL
Java_org_drinkless_tdlib_JsonClient_receive(JNIEnv *env, jobject obj, jlong clientId, jdouble timeout) {
    const char *result = td_json_client_receive((void *) clientId, timeout);
    if (result == nullptr) return nullptr;
    return env->NewStringUTF(result);
}

JNIEXPORT jstring JNICALL
Java_org_drinkless_tdlib_JsonClient_execute(JNIEnv *env, jobject obj, jlong clientId, jstring request) {
    const char *req = env->GetStringUTFChars(request, nullptr);
    const char *result = td_json_client_execute((void *) clientId, req);
    env->ReleaseStringUTFChars(request, req);
    if (result == nullptr) return nullptr;
    return env->NewStringUTF(result);
}

JNIEXPORT void JNICALL
Java_org_drinkless_tdlib_JsonClient_destroy(JNIEnv *env, jobject obj, jlong clientId) {
    td_json_client_destroy((void *) clientId);
}

} // extern "C"
