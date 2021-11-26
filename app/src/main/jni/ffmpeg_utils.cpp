//
// Created by admin on 2021/11/17.
//

#include <jni.h>
#include <cstring>


extern "C"{
#include <cstdio>
#include"mylog.h"
#include"libavformat/avformat.h"
#include"libavutil/channel_layout.h"
#include <libavutil/timestamp.h>
#include "ffmpeg_thread.h"
JNIEXPORT jobject JNICALL
Java_com_roj_formatfactory_FFmpegUtils_getMediaInfo(JNIEnv* env,jobject thiz,jstring jpath){
    char *path = (char *) env->GetStringUTFChars(jpath, 0);
    AVFormatContext* context= avformat_alloc_context();
    int rst = avformat_open_input(&context,path,0, nullptr);
    if(rst != 0){
        LOGE("open file fail %d" , errno);
    }
    if(context == nullptr){
        LOGE("not open file");
        jclass exception = env->FindClass("java/lang/Exception");
        env->ThrowNew(exception,"not open file");
        return nullptr;
    }
    avformat_find_stream_info(context, nullptr);
    av_log(NULL, AV_LOG_INFO, "getinfo %" PRId64" kb/s", context->bit_rate/1000);
    jclass mediaInfoCls = env->FindClass("com/roj/formatfactory/beans/MediaInfo");
    jmethodID init = env->GetMethodID(mediaInfoCls, "<init>", "()V");
    jobject mediaInfo = env->NewObject(mediaInfoCls, init);


    AVRational tb = AV_TIME_BASE_Q;
    char* c_durationStr =   av_ts2timestr(context->duration, &tb);
    jfieldID  durationStr = env->GetFieldID(mediaInfoCls,"durationStr", "Ljava/lang/String;");
    env->SetObjectField(mediaInfo,durationStr,env->NewStringUTF(c_durationStr));

    uint64_t c_duration = context->duration + 5000;
    jfieldID  duration = env->GetFieldID(mediaInfoCls,"duration", "J");
    env->SetLongField(mediaInfo,duration,(long)(c_duration / AV_TIME_BASE));

    for(int i =0;i < context->nb_streams;i++){
        AVStream* avStream = context->streams[i];
        if(avStream->codecpar->codec_type == AVMEDIA_TYPE_AUDIO){
            AVCodecID c_codecId = avStream->codecpar->codec_id;

            jfieldID audioFormat = env->GetFieldID(mediaInfoCls,"audioFormat", "Ljava/lang/String;");
            env->SetObjectField(mediaInfo,audioFormat,env->NewStringUTF(avcodec_get_name(c_codecId)));

            int c_channels = avStream->codecpar->channels;
            jfieldID channels = env->GetFieldID(mediaInfoCls,"channels", "I");
            env->SetIntField(mediaInfo,channels,c_channels);

            int c_sampleRate = avStream->codecpar->sample_rate;
            jfieldID simpleRate = env->GetFieldID(mediaInfoCls,"sampleRate", "I");
            env->SetIntField(mediaInfo,simpleRate,c_sampleRate);

            uint64_t c_bitRate = avStream->codecpar->bit_rate;
            jfieldID  bitRate = env->GetFieldID(mediaInfoCls,"audioBitRate", "I");
            env->SetIntField(mediaInfo,bitRate,(int)(c_bitRate / 1000));


            uint64_t c_channel_layout = avStream->codecpar->channel_layout;
            char* channel_layout_desc = nullptr;
            for(int i=0;i < c_channels; i++) {
                char* temp = new char[1024];
                av_get_channel_layout_string(temp, 1024, i, c_channel_layout);
                if(channel_layout_desc == nullptr){
                    channel_layout_desc = new char[1024];
                    strcpy(channel_layout_desc,temp);
                }else {
                    strcat(channel_layout_desc, temp);
                }
                delete[] temp;
            }
            jfieldID channel_layout = env->GetFieldID(mediaInfoCls,"channelLayout", "Ljava/lang/String;");
            env->SetObjectField(mediaInfo,channel_layout,
                             env->NewStringUTF(channel_layout_desc));
            delete[] channel_layout_desc;



            uint64_t c_startTime = context->start_time;
            jfieldID  startTime = env->GetFieldID(mediaInfoCls,"startTime", "I");
            env->SetIntField(mediaInfo,startTime,(int)(c_startTime / AV_TIME_BASE));
        } else if(avStream->codecpar->codec_type == AVMEDIA_TYPE_VIDEO){
            AVCodecID c_codecId = avStream->codecpar->codec_id;

            jfieldID videoFormat = env->GetFieldID(mediaInfoCls,"videoFormat", "Ljava/lang/String;");
            env->SetObjectField(mediaInfo,videoFormat,env->NewStringUTF(avcodec_get_name(c_codecId)));

            uint64_t c_bitRate = avStream->codecpar->bit_rate;
            jfieldID  bitRate = env->GetFieldID(mediaInfoCls,"videoBitRate", "I");
            env->SetIntField(mediaInfo,bitRate,(int)(c_bitRate / 1000));

            char c_dar[40] ;
            AVRational sar = avStream->sample_aspect_ratio;
            sprintf(c_dar,"%d:%d",sar.num,sar.den);
            jfieldID dar = env->GetFieldID(mediaInfoCls,"dar", "Ljava/lang/String;");
            env->SetObjectField(mediaInfo,dar,env->NewStringUTF(c_dar));

            AVRational afr =  avStream->avg_frame_rate;
            jfieldID frameRate = env->GetFieldID(mediaInfoCls,"frameRate", "F");
            env->SetFloatField(mediaInfo,frameRate,(float)(afr.num) / afr.den);

            int c_width = avStream->codecpar->width;
            jfieldID  width = env->GetFieldID(mediaInfoCls,"width", "I");
            env->SetIntField(mediaInfo,width,c_width);

            int c_height = avStream->codecpar->height;
            jfieldID  height = env->GetFieldID(mediaInfoCls,"height", "I");
            env->SetIntField(mediaInfo,height,c_height);
        }
    }

    return mediaInfo;
}

jint JNI_OnLoad(JavaVM* vm,void* reserved){
    JNIEnv* env = nullptr;
    if(vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK){
        return -1;
    }
    av_log_set_callback(log_callback_null);
    return JNI_VERSION_1_6;
}
JNIEXPORT jint JNICALL
Java_com_roj_formatfactory_FFmpegUtils_parseToMp3(JNIEnv *env, jobject thiz, jstring src_path,
                                                  jstring dest_path, jobject option) {
   const  char*  src =  env->GetStringUTFChars(src_path,0);
   const char* dest =  env->GetStringUTFChars(dest_path,0);

   jclass optionCls = env->GetObjectClass(option);
   jfieldID  sampleRate = env->GetFieldID(optionCls,"sampleRate", "I");
   int c_sampleRate = env->GetIntField(option,sampleRate);

    jfieldID  bitRate = env->GetFieldID(optionCls,"bitRate", "I");
    int c_bitRate = env->GetIntField(option,bitRate);
    char bitRateStr[20];
    sprintf(bitRateStr,"%dk",c_bitRate);

    const  char* cmd[] = {"ffmpeg","-i",src,"-acodec","libmp3lame","-b:a",bitRateStr,dest};
    int result = ffmpeg_thread_run_command(8,cmd);
    env->ReleaseStringUTFChars(src_path,src);
    env->ReleaseStringUTFChars(dest_path,dest);
    return result;
}

}
