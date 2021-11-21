package com.roj.formatfactory

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AudioFormatSelectFragment: Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val data = ArrayList<String>()
        data.add("转换为MP3")
        data.add("转换为WMA")
        data.add("转换为M4A")
        data.add("转换为FLAC")
        data.add("转换为AAC")
        data.add("转换为APE")
        data.add("转换为WAV")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_format_select,container,false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView?.layoutManager = GridLayoutManager(requireContext(),2)
        val adapter = FunctionAdapter(requireContext(),data)
        recyclerView?.adapter = adapter

        val verticalDivider =  DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        verticalDivider.setDrawable(resources.getDrawable(R.drawable.vertical_divider,null))

        val horizontalDivider = DividerItemDecoration(requireContext(), RecyclerView.HORIZONTAL)
        horizontalDivider.setDrawable(resources.getDrawable(R.drawable.horizontal_divider,null))
        recyclerView?.addItemDecoration(horizontalDivider)
        recyclerView?.addItemDecoration(verticalDivider)

        adapter.adapterListener = object : AdapterListener{
            override fun onItemClick(position: Int) {
                var type = 0
                if(position == 0){
                    //转换为MP4
                    type = AppConstant.EVENT_TO_MP3
                }
                val intent =  Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "audio/*" //选择音频
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(intent, type)
            }
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            val intent = Intent(requireContext(),StartParseActivity::class.java)
            intent.data = data?.data
            intent.putExtra("type", AppConstant.EVENT_TO_MP3)
            startActivity(intent)
        }
    }
}