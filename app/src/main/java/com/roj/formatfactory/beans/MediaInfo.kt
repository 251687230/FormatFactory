package com.roj.formatfactory.beans

import com.roj.formatfactory.config.NoArg

@NoArg
data class MediaInfo(
    var name : String?,
    val audioFormat : String?,
    val videoFormat : String,
    //kb
    var fileSize : Int,
    val channels : Int,
    val sampleRate : Int,
    val channelLayout : String?,
    //kb/s
    val audioBitRate : Int,
    val videoBitRate : Int,
    val dar : String,
    //s
    val duration : Long,
    val durationStr : String,
    val startTime : Int,
    val frameRate : Float,
    val width : Int,
    val height : Int
){
    override fun toString(): String {
        return "name = $name,audioFormat = $audioFormat," +
                "fileSize = $fileSize , channel = $channels," +
                "sampleRate = $sampleRate, channel_layout = $channelLayout" +
                ",audioBitRate = $audioBitRate,duration = $duration,startTime = $startTime" +
                ",durationStr = $durationStr,dar = $dar,frameRate = $frameRate"
    }
}