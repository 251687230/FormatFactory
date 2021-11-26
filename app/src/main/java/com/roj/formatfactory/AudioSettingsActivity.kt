package com.roj.formatfactory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.roj.formatfactory.beans.AudioParseOption
import java.io.File

class AudioSettingsActivity : AppCompatActivity() {
    val sampleRateList = arrayOf(
        48000,44100,32000,24000,16000,8000
    )
    val bitRateList = arrayOf(320,256,224,192,128,96,32)
    val channelList = arrayOf("Mono Channel","Stereo Channel")
    var sampleRateSpinner : Spinner? = null
    var bitRateSpinner : Spinner? = null
    var channelSpinner : Spinner? = null
    var filePathTv : TextView? = null

    var selectSampleRate = 0
    var selectBitRate = 0
    var selectChannel = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_audio_settings)

        filePathTv = findViewById(R.id.tv_file_path)
        val fileName = intent.getStringExtra("fileName")
        val type = intent.getIntExtra("type",AppConstant.EVENT_TO_MP3)
        if(fileName != null){
            val dir = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString())
            var suffix = ""
            if(type == AppConstant.EVENT_TO_MP3){
                suffix = ".mp3"
            }
            val newFile = File(dir.toString() + "/" + fileName.split(".")[0] + suffix)
            filePathTv?.text = newFile.absolutePath
        }

        sampleRateSpinner = findViewById(R.id.spinner_sample_rate)
        sampleRateSpinner?.adapter = ArrayAdapter(
            this,android.R.layout.simple_list_item_1,sampleRateList
        )
        sampleRateSpinner?.setSelection(1)
        sampleRateSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectSampleRate = sampleRateList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        bitRateSpinner = findViewById(R.id.spinner_bit_rate)
        bitRateSpinner?.adapter = ArrayAdapter(
            this,android.R.layout.simple_list_item_1,bitRateList
        )
        bitRateSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectBitRate = bitRateList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        channelSpinner = findViewById(R.id.spinner_channel)
        channelSpinner?.adapter = ArrayAdapter(
            this,android.R.layout.simple_list_item_1,channelList
        )
        channelSpinner?.setSelection(1)
        channelSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectChannel = position + 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_save){
            val option = AudioParseOption(
                selectSampleRate,selectBitRate,selectChannel
            )

            val intent = Intent()
            intent.putExtra("option",option)
            setResult(RESULT_OK,intent)
            finish()
        }
        return true
    }
}