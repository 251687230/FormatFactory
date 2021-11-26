package com.roj.formatfactory.beans

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AudioParseOption(
    val sampleRate : Int = 44100,
    val bitRate : Int = 320,
    val channel : Int = 1
) : Parcelable