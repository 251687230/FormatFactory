package com.roj.formatfactory.beans

import com.roj.formatfactory.config.NoArg

@NoArg
data class MediaInfo(
    var name : String?,
    val audioFormat : String,
    //kb
    var fileSize : Int,
    val channels : Int,
    val sampleRate : Int,
    val channelLayout : String?,
    //kb/s
    val bitRate : Int,
    //s
    val duration : Long
){
    override fun toString(): String {
        return "name = $name,audioFormat = $audioFormat," +
                "fileSize = $fileSize , channel = $channels," +
                "sampleRate = $sampleRate, channel_layout = $channelLayout" +
                ",bitRate = $bitRate,duration = $duration"
    }
}