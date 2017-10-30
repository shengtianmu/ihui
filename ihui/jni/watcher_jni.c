#include <jni.h>
#include <unistd.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <android/log.h>
#define LOG_TAG "WatcherNative"


#define MEM_ZERO(pDest, destSize) memset(pDest, 0, destSize)

#define LOG_INFO(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOG_DEBUG(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOG_WARN(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOG_ERROR(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
jboolean isCopy = JNI_TRUE;
JNIEXPORT jint JNICALL Java_com_zpstudio_util_Watcher_chmod
(JNIEnv* env, jobject thiz, jstring file , jstring mode) {

	 //return chmod( (*env)->GetStringUTFChars(env, file, &isCopy) , strtol((*env)->GetStringUTFChars(env, mode, &isCopy) , 0 , 0));
	return chmod( (*env)->GetStringUTFChars(env, file, &isCopy) , S_IRWXU | S_IRWXG);
}

JNIEXPORT jint JNICALL Java_com_zpstudio_util_Watcher_exec
  (JNIEnv* env, jobject thiz, jstring command)
{
	int result;
	const char * cCommand = (*env)->GetStringUTFChars(env, command, &isCopy);
	result = system(cCommand);
	LOG_INFO("run %s : %d" , cCommand , result);
	return result;
}
