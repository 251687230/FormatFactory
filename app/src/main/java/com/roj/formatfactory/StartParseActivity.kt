package com.roj.formatfactory

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream


class StartParseActivity : AppCompatActivity() {
    var fileNameTv : TextView? = null
    var type = 0
    var typeIv : ImageView? = null
    var fd : FileDescriptor? = null
    var getInfoBtn : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_parse)

        fileNameTv = findViewById(R.id.tv_title)
        getInfoBtn = findViewById(R.id.bt_get_info)
        typeIv = findViewById(R.id.iv_type)

        type = intent.getIntExtra("type",0)

        val uri = intent.data
        if(uri != null) {
            val returnCursor = contentResolver.query(uri, null, null, null, null)
            val displayIndex = returnCursor?.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME) ?:0
            returnCursor?.moveToFirst()
            val fileName = returnCursor?.getString(displayIndex)

            val fileDescriptor = contentResolver.openFileDescriptor(uri,"r")
            returnCursor?.close()
            Log.i(AppConstant.LOG_TAG,"$uri fileName = $fileName,fileDescriptor = ${fileDescriptor?.fd}")

            fileNameTv?.text = fileName

            if(fileDescriptor?.fd != null){
                fd = fileDescriptor.fileDescriptor
                val rst = FFmpegUtils.open("file_fd:" + fileDescriptor.fd)


            }

        }


        if(type == 1){
            typeIv?.setImageResource(R.drawable.ic_audio)
        }else{
            typeIv?.setImageResource(R.drawable.ic_video)
        }

        getInfoBtn?.setOnClickListener {
                val mediaInfo = FFmpegUtils.getMediaInfo()

                val fis =  FileInputStream(fd)
                val size = fis.available()
                mediaInfo.fileSize = size
                Log.i(AppConstant.LOG_TAG, mediaInfo.toString())
        }
    }

    override fun onDestroy() {
        FFmpegUtils.close()
        super.onDestroy()
    }
}