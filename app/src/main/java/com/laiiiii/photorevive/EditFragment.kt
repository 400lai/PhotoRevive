package com.laiiiii.photorevive


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.laiiiii.photorevive.databinding.FragmentEditBinding

class EditFragment : Fragment() {
    // 在此处定义轮播图数量的常量
    companion object {
        private const val BANNER_COUNT = 6
    }

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var bannerAdapter: BannerAdapter

    // 用于记录 ViewPager2 是否处于 Idle/空闲 状态
    private var isViewPagerIdle = true

    private val handler = Handler(Looper.getMainLooper())
    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            // 仅在 ViewPager 空闲时才翻页，防止跳帧
            if (isViewPagerIdle) {
                val currentItem = viewPager.currentItem
                viewPager.currentItem = (currentItem + 1) % BANNER_COUNT
            }
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = binding.viewPagerBanner
        indicatorContainer = binding.indicatorContainer

        // 传入 this
        bannerAdapter = BannerAdapter(this)
        viewPager.adapter = bannerAdapter

        // 创建指示器
        createIndicators()

        // 监听页面切换
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                // 更新空闲状态：SCROLL_STATE_IDLE = 0
                isViewPagerIdle = (state == ViewPager2.SCROLL_STATE_IDLE)
            }
        })

        // 启动自动轮播
        startAutoSlide()
    }

    private fun createIndicators() {
        indicatorContainer.removeAllViews()
        for (i in 0 until BANNER_COUNT) {
            val dot = View(context)
            val size = 8.dpToPx()
            val margin = 4.dpToPx()
            val params = LinearLayout.LayoutParams(size, size).apply {
                setMargins(margin, 0, margin, 0)
            }
            dot.layoutParams = params
            dot.setBackgroundResource(R.drawable.shape_indicator_dot)
            indicatorContainer.addView(dot)
        }
        updateIndicators(0)
    }

    private fun updateIndicators(selectedIndex: Int) {
        for (i in 0 until indicatorContainer.childCount) {
            val isSelected = i == selectedIndex
            indicatorContainer.getChildAt(i).setBackgroundResource(
                if (isSelected) R.drawable.shape_indicator_dot_selected
                else R.drawable.shape_indicator_dot
            )
        }
    }

    private fun startAutoSlide() {
        handler.postDelayed(autoSlideRunnable, 3000)
    }

    private fun stopAutoSlide() {
        handler.removeCallbacks(autoSlideRunnable)
    }

    override fun onResume() {
        super.onResume()
        startAutoSlide()
    }

    override fun onPause() {
        super.onPause()
        stopAutoSlide()
    }

    // dp 转 px 扩展函数
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}