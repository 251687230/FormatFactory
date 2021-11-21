package com.roj.formatfactory.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.roj.formatfactory.AudioFormatSelectFragment
import com.roj.formatfactory.VideoFormatSelectFragment

class FunctionFragmentAdapter(val titles : Array<String>,fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager) {
    val audioFormatSelectFragment = AudioFormatSelectFragment()
    val videoFormatSelectFragment = VideoFormatSelectFragment()
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
       if(position == 0){
           return videoFormatSelectFragment
       }else{
           return audioFormatSelectFragment
       }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}