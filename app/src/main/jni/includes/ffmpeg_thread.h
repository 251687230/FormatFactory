//
// Created by admin on 2021/4/13.
//

#ifndef VOICESERVICE_FFMPEG_THREAD_H
#define VOICESERVICE_FFMPEG_THREAD_H
void *run_thread(void *arg);
void ffmpeg_thread_exit(int ret);
int ffmpeg_thread_run_command(int argc, char **argv);
#endif //VOICESERVICE_FFMPEG_THREAD_H
