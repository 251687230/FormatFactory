package com.roj.formatfactory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    var recyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = ArrayList<String>()
        data.add("格式转换")
        data.add("快速剪辑")

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView?.layoutManager = GridLayoutManager(this,2)
        val adapter = FunctionAdapter(this,data)
        recyclerView?.adapter = adapter
        val horizontalDivider = DividerItemDecoration(this,RecyclerView.HORIZONTAL)
        horizontalDivider.setDrawable(resources.getDrawable(R.drawable.horizontal_divider,null))
        recyclerView?.addItemDecoration(horizontalDivider)

        adapter.adapterListener = object : AdapterListener{
            override fun onItemClick(position: Int) {
                if(position == 0){
                    val intent = Intent(this@MainActivity,FormatSelectActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}