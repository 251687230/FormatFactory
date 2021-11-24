//
// Created by admin on 2021/4/13.
//
// run_ffmpeg_command 参数个数
extern "C"{
#include <mylog.h>
#include "libtools/ffmpeg.h"
#include "ffmpeg_thread.h"
}
int ffmpeg_argc;
// 缓存 run_ffmpeg_command 返回结果
void* ffmpeg_exec_ret;
// run_ffmpegm_command 参数
char **ffmpeg_argv;

// 子线程函数
void *run_thread(void *arg){
    run(ffmpeg_argc, ffmpeg_argv);
}

// 在子线程跑指令
int ffmpeg_thread_run_command(int argc, char **argv){
    // 初始化全局变量
    ffmpeg_argc = argc;
    ffmpeg_argv = argv;
    int a = -1;
    ffmpeg_exec_ret = &a;

    pthread_t thread_id;
    // 创建线程
    int thread_ret = pthread_create(&thread_id, nullptr, run_thread, nullptr);
    if (thread_ret){
        LOGE("can not create thread");
        return -1;
    }

    // join 子线程，模拟同步
    thread_ret = pthread_join(thread_id, &ffmpeg_exec_ret);
    if (thread_ret){
        LOGE("thread join error");
        return -1;
    }

    // 返回结果
    LOGI("exec result %d",(uintptr_t)ffmpeg_exec_ret);
    return (uintptr_t)ffmpeg_exec_ret;
}

void ffmpeg_thread_exit(int ret){
    // 缓存执行结果
    pthread_exit((void*)ret);
}
