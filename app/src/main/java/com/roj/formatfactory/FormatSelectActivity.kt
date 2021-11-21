package com.roj.formatfactory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.roj.formatfactory.adapter.FunctionFragmentAdapter


class FormatSelectActivity : AppCompatActivity() {
    var viewPager : ViewPager? = null
    var tabLayout : TabLayout? = null
    val titles = arrayOf("视频","音频")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_format_select)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        for(i in titles.indices) {
            val tab = tabLayout?.newTab()
            tab?.text = titles[i]
            tab?.let { tabLayout?.addTab(it) }
        }

        tabLayout?.setupWithViewPager(viewPager)
        viewPager?.adapter = FunctionFragmentAdapter(titles,supportFragmentManager)

    }
}