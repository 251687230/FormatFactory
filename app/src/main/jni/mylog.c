//
// Created by admin on 2021/4/13.
//
#include <libavutil/log.h>
#include "mylog.h"
#include <string.h>
void log_callback_null(void *ptr, int level, const char *fmt, va_list vl)
{
    static int print_prefix = 1;
    static int count;
    static char prev[1024];
    char line[1024];
    static int is_atty;

    av_log_format_line(ptr, level, fmt, vl, line, sizeof(line), &print_prefix);

    strcpy(prev, line);
    //sanitize((uint8_t *)line);

    if (level <= AV_LOG_WARNING)
    {
        LOGE("%s", line);
    }
    else
    {
        LOGI("%s", line);
    }
}
