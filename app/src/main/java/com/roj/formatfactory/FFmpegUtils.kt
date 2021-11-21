package com.roj.formatfactory

import com.roj.formatfactory.beans.MediaInfo

object FFmpegUtils {
    external fun open(path : String) : Int

    external fun getMediaInfo() : MediaInfo

    external fun close()

    init {
        System.loadLibrary("native_lib")
    }
}