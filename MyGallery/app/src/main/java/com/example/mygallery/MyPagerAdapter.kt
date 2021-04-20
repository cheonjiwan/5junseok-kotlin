package com.example.mygallery

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MyPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    // 뷰페이저가 표시할 프래그먼트 목록
    private val items = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        // position 위치의 프래그먼트
        return items[position]
    }

    override fun getCount(): Int {
        // 아이템 개수
        return items.size
    }

    // 아이템 갱신
    fun updateFragments(items : List<Fragment>){
        this.items.addAll(items)
    }
}