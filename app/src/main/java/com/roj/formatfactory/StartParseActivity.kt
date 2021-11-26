package com.roj.formatfactory

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.roj.formatfactory.beans.AudioParseOption
import com.roj.formatfactory.beans.MediaInfo
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream


class StartParseActivity : AppCompatActivity() {
    var fileNameTv: TextView? = null
    var type = 0
    var typeIv: ImageView? = null
    var fd: FileDescriptor? = null
    var point: Long = 0
    var fileDescriptor: ParcelFileDescriptor? = null

    var videoInfoLayout: LinearLayout? = null
    var audioInfoLayout: LinearLayout? = null
    var labelVideoTv: TextView? = null
    var labelAudioTv: TextView? = null
    var audioParseOption = AudioParseOption()
    var parseBtn: FloatingActionButton? = null
    var fileName: String? = null
    var hintDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_start_parse)


        fileNameTv = findViewById(R.id.tv_title)
        typeIv = findViewById(R.id.iv_type)
        videoInfoLayout = findViewById(R.id.video_info_ll)
        audioInfoLayout = findViewById(R.id.audio_info_ll)
        labelVideoTv = findViewById(R.id.label_video_info)
        labelAudioTv = findViewById(R.id.label_audio_info)
        parseBtn = findViewById(R.id.btn_action_parse)

        type = intent.getIntExtra("type", 0)

        val uri = intent.data
        if (uri != null) {
            val returnCursor = contentResolver.query(uri, null, null, null, null)
            val displayIndex =
                returnCursor?.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME) ?: 0
            returnCursor?.moveToFirst()
            fileName = returnCursor?.getString(displayIndex)

            fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            returnCursor?.close()
            Log.i(
                AppConstant.LOG_TAG,
                "$uri fileName = $fileName,fileDescriptor = ${fileDescriptor?.fd}"
            )

            fileNameTv?.text = fileName

            if (fileDescriptor?.fd != null) {
                fd = fileDescriptor?.fileDescriptor
                val mediaInfo = FFmpegUtils.getMediaInfo("file_fd:${fileDescriptor?.fd}")

                val fis = FileInputStream(fd)
                val size = fis.available()
                mediaInfo.fileSize = size
                fis.close()

                mediaInfo.name = fileName
                Log.i(AppConstant.LOG_TAG, mediaInfo.toString())

                showMediaInfo(mediaInfo)
            }

        }


        if (type < 100) {
            typeIv?.setImageResource(R.drawable.ic_audio)
        } else {
            typeIv?.setImageResource(R.drawable.ic_video)
        }

        parseBtn?.setOnClickListener {
            if (fileName == null) {
                return@setOnClickListener
            }
            if (type < 100) {
                val dir = File(
                    getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString()
                )
                var suffix = ""
                if (type == AppConstant.EVENT_TO_MP3) {
                    suffix = ".mp3"
                }
                val newFile = File(dir.toString() + "/" + fileName!!.split(".")[0] + suffix)
                if (newFile.exists()) {
                    newFile.delete()
                }
                val result = FFmpegUtils.parseToMp3(
                    "file_fd:${fileDescriptor?.fd}",
                    newFile.absolutePath, audioParseOption
                )
                if (result == 0) {
                    showSuccessHint {
                        Toast.makeText(this, "你点击了确定按钮", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun showSuccessHint(confirm: () -> Unit) {
        if(hintDialog != null && hintDialog?.isShowing == true){
            hintDialog?.dismiss()
        }
        hintDialog = AlertDialog.Builder(this)
            .setMessage("转码成功，是否预览转码后的文件？")
            .setPositiveButton("是"){
                    _, _ ->
                run {
                    confirm()
                }
            }
            .setNegativeButton("否", null).create()
        hintDialog?.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.media_settings, menu)
        if (menu == null) {
            return false
        }
        for (i in 0 until menu.size()) {
            val drawable = menu.getItem(i).icon
            if (drawable != null) {
                drawable.mutate();
                //这里修改为你想修改的颜色
                drawable.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_setting) {
            if (type < 100) {
                val intent = Intent(this, AudioSettingsActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("fileName", fileName)
                startActivityForResult(intent, type)
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode < 100) {
                val option = data?.getParcelableExtra<AudioParseOption>("option")
                if (option != null) {
                    audioParseOption = option
                }
            }
        }
    }

    fun showMediaInfo(mediaInfo: MediaInfo) {
        if (type < 100) {
            labelVideoTv?.visibility = View.GONE
            videoInfoLayout?.visibility = View.GONE

            generateItemView(1, "File Size", "${mediaInfo.fileSize / 1024}KB")
            generateItemView(1, "Duration", "${mediaInfo.durationStr}s")
            generateItemView(1, "Format", mediaInfo.audioFormat ?: "")
            generateItemView(1, "Channels", mediaInfo.channels.toString())
            generateItemView(1, "Channel layout", mediaInfo.channelLayout ?: "")
            generateItemView(
                1,
                "Sample Rate",
                if (mediaInfo.sampleRate > 1000) "${mediaInfo.sampleRate / 1000f}KHZ " else
                    "${mediaInfo.sampleRate}HZ"
            )
            generateItemView(1, "BitRate", "${mediaInfo.audioBitRate} KB/S")
        } else {
            generateItemView(2, "File Size", "${mediaInfo.fileSize / 1024}KB")
            generateItemView(2, "Duration", "${mediaInfo.durationStr}s")
            generateItemView(2, "Width", mediaInfo.width.toString())
            generateItemView(2, "Height", mediaInfo.height.toString())
            generateItemView(2, "Format", mediaInfo.videoFormat)
            generateItemView(2, "Display Aspect Ratio", mediaInfo.dar)
            generateItemView(2, "BitRate", "${mediaInfo.videoBitRate} KB/S")
            generateItemView(2, "Frame Rate", "${mediaInfo.frameRate} FPS")


            if (mediaInfo.audioFormat != null) {
                generateItemView(1, "Format", mediaInfo.audioFormat ?: "")
                generateItemView(1, "Channels", mediaInfo.channels.toString())
                generateItemView(1, "Channel layout", mediaInfo.channelLayout ?: "")
                generateItemView(
                    1,
                    "Sample Rate",
                    if (mediaInfo.sampleRate > 1000) "${mediaInfo.sampleRate / 1000f}KHZ " else
                        "${mediaInfo.sampleRate}HZ"
                )
                generateItemView(1, "BitRate", "${mediaInfo.audioBitRate} KB/S")
            } else {
                audioInfoLayout?.visibility = View.GONE
                labelAudioTv?.visibility = View.GONE
            }
        }
    }

    fun generateItemView(type: Int, key: String, value: String) {
        if (type == 1) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_info, audioInfoLayout, false)
            val keyTv = view.findViewById<TextView>(R.id.item_key)
            keyTv.text = key
            val valueTv = view.findViewById<TextView>(R.id.item_value)
            valueTv.text = value
            audioInfoLayout?.addView(view)
        } else {
            val view = LayoutInflater.from(this).inflate(R.layout.item_info, videoInfoLayout, false)
            val keyTv = view.findViewById<TextView>(R.id.item_key)
            keyTv.text = key
            val valueTv = view.findViewById<TextView>(R.id.item_value)
            valueTv.text = value
            videoInfoLayout?.addView(view)
        }
    }

    override fun onDestroy() {
        fileDescriptor?.close()
        super.onDestroy()
    }
}