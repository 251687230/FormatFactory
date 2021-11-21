package com.roj.formatfactory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VideoFormatSelectFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val data = ArrayList<String>()
        data.add("转换为MP4")
        data.add("转换为MKV")
        data.add("转换为WebM")
        data.add("转换为GIF")
        data.add("转换为AVI")
        data.add("转换为FLV")
        data.add("转换为MOV")

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
                if(position == 0){
                    //转换为MP4

                }
            }
        }
        return view
    }
}