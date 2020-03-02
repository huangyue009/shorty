//
// Created by yue huang on 2020-02-28.
//
#include <jni.h>
#include "aes.h"
#include "md5.h"
#include "checksignature.h"
#include "check_emulator.h"

struct AES_ctx mCtx;

//当动态库被加载时这个函数被系统调用
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    return JNI_VERSION_1_6;
}
//当动态库被卸载时这个函数被系统调用
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {

}

JNIEXPORT jbyteArray JNICALL Java_com_shorty_encrypt_ShortyEncrypt_aesEncrypt
        (JNIEnv *env, jobject jobj, jbyteArray msg, jstring key_str) {
    //先进行apk被 二次打包的校验
//    if (check_signature(env, instance, context) != 1 || check_is_emulator(env) != 1) {
//        const char *str = UNSIGNATURE;
////        return (*env)->NewString(env, str, strlen(str));
//        return charToJstring(env,str);
//    }

    int len = (*env)->GetArrayLength(env, msg);
    char *in = (char *) malloc(len);

    (*env)->GetByteArrayRegion(env, msg, 0, len, in);
    char *result_char = NULL;
    if (key_str != NULL) {
        const char *key = (*env)->GetStringUTFChars(env, key_str, JNI_FALSE);
        struct AES_ctx ctx;
        AES_init_ctx(&ctx, key);
        (*env)->ReleaseStringUTFChars(env, key_str, key);

        result_char = AES_ECB_encrypt(&ctx, in, len);
    } else {
        result_char = AES_ECB_encrypt(&mCtx, in, len);
    }

    int result_len = strlen(result_char);
    jbyteArray result = (*env)->NewByteArray(env, result_len);
    (*env)->SetByteArrayRegion(env, result, 0, result_len, result_char);

    free(in);
    free(result_char);
    in = NULL;
    result_char = NULL;
    return result;
}

/*
 * Class:     com_shorty_logger_AESExecute
 * Method:    decrypt
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jbyteArray JNICALL Java_com_shorty_encrypt_ShortyEncrypt_aesDecrypt
        (JNIEnv *env, jobject jobj, jbyteArray msg, jstring key_str) {
    //先进行apk被 二次打包的校验
//    if (check_signature(env, instance, context) != 1|| check_is_emulator(env) != 1) {
//        const char *str = UNSIGNATURE;
////        return (*env)->NewString(env, str, strlen(str));
//        return charToJstring(env,str);
//    }

    int len = (*env)->GetArrayLength(env, msg);
    char *in = (char *) malloc(len);

    (*env)->GetByteArrayRegion(env, msg, 0, len, in);

    char *result_char = NULL;
    if (key_str != NULL) {
        const char *key = (*env)->GetStringUTFChars(env, key_str, JNI_FALSE);
        struct AES_ctx ctx;
        AES_init_ctx(&ctx, key);
        (*env)->ReleaseStringUTFChars(env, key_str, key);

        result_char = AES_ECB_decrypt(&ctx, in, len);
    } else {
        result_char = AES_ECB_decrypt(&mCtx, in, len);
    }

    int result_len = strlen(result_char);
    jbyteArray result = (*env)->NewByteArray(env, result_len);
    (*env)->SetByteArrayRegion(env, result, 0, result_len, result_char);

    free(in);
    free(result_char);
    in = NULL;
    result_char = NULL;
    return result;
}

JNIEXPORT void JNICALL
Java_com_shorty_encrypt_ShortyEncrypt_setDefaultKey(JNIEnv *env, jobject thiz, jstring key_str) {
    const char *key = (*env)->GetStringUTFChars(env, key_str, JNI_FALSE);
    AES_init_ctx(&mCtx, key);
    (*env)->ReleaseStringUTFChars(env, key_str, key);
}