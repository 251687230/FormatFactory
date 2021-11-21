//
// Created by admin on 2021/11/17.
//

#include <jni.h>
#include <cstring>
extern "C"{
#include"mylog.h"
#include"libavformat/avformat.h"
AVFormatContext *context = nullptr;

void av_dump_format1(AVFormatContext *ic, int index,
                    const char *url, int is_output)
{
    int i;

    av_log(NULL, AV_LOG_INFO, "%s #%d, %s, %s '%s':\n",
           is_output ? "Output" : "Input",
           index,
           is_output ? ic->oformat->name : ic->iformat->name,
           is_output ? "to" : "from", url);

    if (!is_output) {
        av_log(NULL, AV_LOG_INFO, "  Duration: ");
        if (ic->duration != AV_NOPTS_VALUE) {
            int64_t hours, mins, secs, us;
            int64_t duration = ic->duration + (ic->duration <= INT64_MAX - 5000 ? 5000 : 0);
            secs  = duration / AV_TIME_BASE;
            us    = duration % AV_TIME_BASE;
            mins  = secs / 60;
            secs %= 60;
            hours = mins / 60;
            mins %= 60;
            av_log(NULL, AV_LOG_INFO, "%02" PRId64":%02" PRId64":%02" PRId64".%02" PRId64"", hours, mins, secs,
                   (100 * us) / AV_TIME_BASE);
        } else {
            av_log(NULL, AV_LOG_INFO, "N/A");
        }
        if (ic->start_time != AV_NOPTS_VALUE) {
            int secs, us;
            av_log(NULL, AV_LOG_INFO, ", start: ");
            secs = llabs(ic->start_time / AV_TIME_BASE);
            us   = llabs(ic->start_time % AV_TIME_BASE);
            av_log(NULL, AV_LOG_INFO, "%s%d.%06d",
                   ic->start_time >= 0 ? "" : "-",
                   secs,
                   (int) av_rescale(us, 1000000, AV_TIME_BASE));
        }
        av_log(NULL, AV_LOG_INFO, ", bitrate: ");
        if (ic->bit_rate)
            av_log(NULL, AV_LOG_INFO, "%" PRId64" kb/s", ic->bit_rate / 1000);
        else
            av_log(NULL, AV_LOG_INFO, "N/A");
        av_log(NULL, AV_LOG_INFO, "\n");
    }

}


JNIEXPORT jint JNICALL
Java_com_roj_formatfactory_FFmpegUtils_open(JNIEnv* env,jobject thiz, jstring jpath) {
    char *path = (char *) env->GetStringUTFChars(jpath, 0);
    context= avformat_alloc_context();
    int rst = avformat_open_input(&context,path,0, nullptr);
    if(rst != 0){
        LOGE("open file fail %d" , errno);
    }

    avformat_find_stream_info(context, nullptr);
    av_log(NULL, AV_LOG_INFO, "before dump %" PRId64" kb/s", context->bit_rate / 1000);
    av_dump_format(context,0,path,0);
    if(context->bit_rate) {
        av_log(NULL, AV_LOG_INFO, "dump %" PRId64" kb/s", context->bit_rate / 1000);
    }

    env->ReleaseStringUTFChars(jpath,path);
    return rst;
}

JNIEXPORT void JNICALL
Java_com_roj_formatfactory_FFmpegUtils_close(JNIEnv* env,jobject thiz){
    avformat_close_input(&context);
    avformat_free_context(context);
    context = nullptr;
}

JNIEXPORT jobject JNICALL
Java_com_roj_formatfactory_FFmpegUtils_getMediaInfo(JNIEnv* env,jobject thiz){
    if(context == nullptr){
        LOGE("not open file");
        jclass exception = env->FindClass("java/lang/Exception");
        env->ThrowNew(exception,"not open file");
        return nullptr;
    }
    av_log(NULL, AV_LOG_INFO, "getinfo %" PRId64" kb/s", context->bit_rate/1000);
    jclass mediaInfoCls = env->FindClass("com/roj/formatfactory/beans/MediaInfo");
    jmethodID init = env->GetMethodID(mediaInfoCls, "<init>", "()V");
    jobject mediaInfo = env->NewObject(mediaInfoCls, init);


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

            uint64_t c_bitRate = context->bit_rate;
            jfieldID  bitRate = env->GetFieldID(mediaInfoCls,"bitRate", "I");
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
            uint64_t c_duration = context->duration;
            jfieldID  duration = env->GetFieldID(mediaInfoCls,"duration", "J");
            env->SetLongField(mediaInfo,duration,(long)(c_duration / AV_TIME_BASE));
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


}