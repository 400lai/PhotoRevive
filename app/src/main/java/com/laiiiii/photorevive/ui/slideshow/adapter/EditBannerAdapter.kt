package com.laiiiii.photorevive.ui.slideshow.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.laiiiii.photorevive.ui.slideshow.fragment.EditBannerFragment
import com.laiiiii.photorevive.ui.slideshow.model.EditBannerItem

// 假设你有 banner 数据源
private val bannerItems = listOf(
    EditBannerItem("#FF4081", "修实况Live"),
    EditBannerItem("#E91E63", "AI修人像"),
    EditBannerItem("#9C27B0", "拼图"),
    EditBannerItem("#3F51B5", "批量修图"),
    EditBannerItem("#00BCD4", "画质超清"),
    EditBannerItem("#4CAF50", "魔法消除")
)

class EditBannerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = bannerItems.size

    override fun createFragment(position: Int): Fragment {
        return EditBannerFragment.newInstance(bannerItems[position])
    }
}