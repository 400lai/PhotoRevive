package com.laiiiii.photorevive

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

// 轮播图适配器：BannerAdapter（负责“生成每一页”）
// ViewPager 要滑动 6 张图 → Adapter 就创建 6 个 BannerFragment。

class BannerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 6  // 总共有 6 页

    override fun createFragment(position: Int): Fragment {
        return BannerFragment(position)  // 每一页都创建一个 BannerFragment
    }
}
