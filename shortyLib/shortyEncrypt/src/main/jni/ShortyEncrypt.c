//
// Created by yue huang on 2020-02-28.
//
#include <jni.h>
#include "aes.h"
#include "md5.h"

struct AES_ctx ctx;

//当动态库被加载时这个函数被系统调用
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    return JNI_VERSION_1_6;
}
//当动态库被卸载时这个函数被系统调用
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    AES_init_ctx(&ctx, NULL);
}


JNIEXPORT jstring JNICALL Java_com_shorty_encrypt_ShortyEncrypt_aesEncrypt
        (JNIEnv *env, jobject jobj, jstring msg) {
    //先进行apk被 二次打包的校验
//    if (check_signature(env, instance, context) != 1 || check_is_emulator(env) != 1) {
//        char *str = UNSIGNATURE;
//        return (*env)->NewString(env, str, strlen(str));
//        return charToJstring(env,str);
//    }

    const char *in = (*env)->GetStringUTFChars(env, msg, JNI_FALSE);
    char* result_chars = AES_ECB_encrypt(&ctx, in);
    int len = strlen(result_chars);
    (*env)->ReleaseStringUTFChars(env, msg, in);
    jstring result_str = (*env) ->NewStringUTF(env, result_chars);

    free(result_chars);

    return result_str;
}

/*
 * Class:     com_shorty_logger_AESExecute
 * Method:    decrypt
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_shorty_encrypt_ShortyEncrypt_aesDecrypt
        (JNIEnv *env, jobject jobj, jstring msg) {
    const char *in = (*env)->GetStringUTFChars(env, msg, JNI_FALSE);

    char* result_chars = AES_ECB_decrypt(&ctx, in);
    (*env)->ReleaseStringUTFChars(env, msg, in);
    jstring result_str = (*env) ->NewStringUTF(env, result_chars);

    free(result_chars);

    return result_str;
}

JNIEXPORT void JNICALL
Java_com_shorty_encrypt_ShortyEncrypt_setKey(JNIEnv *env, jobject thiz, jstring key_str) {
    const char * key = (*env)->GetStringUTFChars(env, key_str, JNI_FALSE);
    AES_init_ctx(&ctx,  key);
    (*env)->ReleaseStringUTFChars(env, key_str, key);
}