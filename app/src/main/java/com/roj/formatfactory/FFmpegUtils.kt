package com.roj.formatfactory

import com.roj.formatfactory.beans.AudioParseOption
import com.roj.formatfactory.beans.MediaInfo

object FFmpegUtils {

    external fun getMediaInfo(path : String) : MediaInfo

    external fun parseToMp3(srcPath : String,destPath : String,
    option : AudioParseOption) : Int


    init {
        System.loadLibrary("native_lib")
    }
}