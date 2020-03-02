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
//    AES_init_ctx(&mCtx, NULL);
}

jstring char_2_jstring(JNIEnv *envPtr, const char *src) {
    JNIEnv env = *envPtr;

    jsize len = strlen(src);
    jclass clsstring = env->FindClass(envPtr, "java/lang/String");
    jstring strencode = env->NewStringUTF(envPtr, "UTF-8");
    jmethodID mid = env->GetMethodID(envPtr, clsstring, "<init>",
                                     "([BLjava/lang/String;)V");
    jbyteArray barr = env->NewByteArray(envPtr, len);
    env->SetByteArrayRegion(envPtr, barr, 0, len, (jbyte *) src);

    return (jstring) env->NewObject(envPtr, clsstring, mid, barr, strencode);
}


//char *getKey() {
//    int n = 0;
//    char s[23];//"NMTIzNDU2Nzg5MGFiY2RlZg";
//
//    s[n++] = 'N';
//    s[n++] = 'M';
//    s[n++] = 'T';
//    s[n++] = 'I';
//    s[n++] = 'z';
//    s[n++] = 'N';
//    s[n++] = 'D';
//    s[n++] = 'U';
//    s[n++] = '2';
//    s[n++] = 'N';
//    s[n++] = 'z';
//    s[n++] = 'g';
//    s[n++] = '5';
//    s[n++] = 'M';
//    s[n++] = 'G';
//    s[n++] = 'F';
//    s[n++] = 'i';
//    s[n++] = 'Y';
//    s[n++] = '2';
//    s[n++] = 'R';
//    s[n++] = 'l';
//    s[n++] = 'Z';
//    s[n++] = 'g';
//    char *encode_str = s + 1;
//    return b64_decode(encode_str, strlen(encode_str));
//}

JNIEXPORT jbyteArray JNICALL Java_com_shorty_encrypt_ShortyEncrypt_aesEncrypt
        (JNIEnv *env, jobject jobj, jbyteArray msg, jstring key_str) {
    //先进行apk被 二次打包的校验
//    if (check_signature(env, instance, context) != 1 || check_is_emulator(env) != 1) {
//        const char *str = UNSIGNATURE;
////        return (*env)->NewString(env, str, strlen(str));
//        return charToJstring(env,str);
//    }

    int len = (*env)->GetArrayLength(env, msg);
    char *in = (char *)malloc(len);

    (*env)->GetByteArrayRegion(env, msg, 0, len, in);
    char *result_char = NULL;
    if(key_str != NULL){
        const char *key = (*env)->GetStringUTFChars(env, key_str, JNI_FALSE);
        struct AES_ctx ctx;
        AES_init_ctx(&ctx, key);
        (*env)->ReleaseStringUTFChars(env, key_str, key);

        result_char = AES_ECB_encrypt(&ctx, in, len);
    } else {
        result_char = AES_ECB_encrypt(&mCtx, in, len);
    }

//    return (*env)->NewStringUTF(env, baseResult);
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
    char *in = (char *)malloc(len);

    (*env)->GetByteArrayRegion(env, msg, 0, len, in);

    char *result_char = NULL;
    if(key_str != NULL){
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