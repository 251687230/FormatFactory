//
// Created by admin on 2021/4/12.
//
#include<jni.h>
#include <cstring>
#include "mylog.h"
#include <cstdio>
extern "C"{
#include "ffmpeg_thread.h"
jint exec_cmd(JNIEnv* env,jobject instance,jobjectArray commands){

    int argc = env->GetArrayLength(commands);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) env->GetObjectArrayElement(commands, i);
        argv[i] = (char *) env->GetStringUTFChars(js, 0);
    }
    int result =  ffmpeg_thread_run_command(argc, argv);
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) env->GetObjectArrayElement(commands, i);
        env->ReleaseStringUTFChars(js,argv[i]);
    }
    return result;
}
}

static JNINativeMethod gmethod[] = {
        {"execCmd","([Ljava/lang/String;)I",(void*)exec_cmd}
};


jint JNI_OnLoad(JavaVM* vm,void* reserved){
    JNIEnv* env = nullptr;
    if(vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK){
        return -1;
    }
    jclass  jclazz = env->FindClass("com/future_education/voiceservice/utils/MediaUtils");
    if(env->RegisterNatives(jclazz,gmethod,sizeof(gmethod)/sizeof(gmethod[0])) < 0)
    {
        return -1;
    }
    return JNI_VERSION_1_6;
}

void JNI_OnUnload(JavaVM *vm, void *reserved){
    JNIEnv* env = nullptr;
    if(vm->GetEnv((void**)&env, JNI_VERSION_1_6) != -1 ){
        return;
    }
    jclass  jclazz = env->FindClass("com/future_education/voiceservice/utils/MediaUtils");
    env->UnregisterNatives(jclazz);
}