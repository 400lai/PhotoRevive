package com.laiiiii.photorevive

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return BannerFragment(position)
    }
}