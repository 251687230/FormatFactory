//
// Created by admin on 2021/1/21.
//

#include <android/log.h>
#ifndef LOG_TAG
#define  LOG_TAG    "FFMPEG"
#endif
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)



void log_callback_null(void *ptr, int level, const char *fmt, va_list vl);

