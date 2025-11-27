package com.laiiiii.photorevive.ui.slideshow

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout

import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.laiiiii.photorevive.R
import com.laiiiii.photorevive.databinding.ViewEditBannerBinding
import com.laiiiii.photorevive.ui.slideshow.adapter.EditBannerAdapter
import com.laiiiii.photorevive.ui.slideshow.indicator.EditBannerIndicator

class EditBannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewEditBannerBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var adapter: EditBannerAdapter
    private lateinit var indicator: EditBannerIndicator

    private var isViewPagerIdle = true
    private val handler = Handler(Looper.getMainLooper())
    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            if (isViewPagerIdle) {
                val currentItem = viewPager.currentItem
                viewPager.currentItem = (currentItem + 1) % BANNER_COUNT
            }
            handler.postDelayed(this, AUTO_SLIDE_INTERVAL)
        }
    }

    companion object {
        const val BANNER_COUNT = 6
        const val AUTO_SLIDE_INTERVAL = 3000L
    }

    init {
        binding = ViewEditBannerBinding.inflate(LayoutInflater.from(context), this)
        viewPager = binding.viewPagerBanner
        indicatorContainer = binding.indicatorContainer
    }

    // 外部调用此方法设置 Fragment
    fun setupWithFragment(fragment: Fragment) {
        adapter = EditBannerAdapter(fragment)
        viewPager.adapter = adapter

        indicator = EditBannerIndicator(context, indicatorContainer, BANNER_COUNT)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                indicator.updateIndicators(position)
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                isViewPagerIdle = (state == ViewPager2.SCROLL_STATE_IDLE)
            }
        })
    }

    fun startAutoSlide() {
        handler.postDelayed(autoSlideRunnable, AUTO_SLIDE_INTERVAL)
    }

    fun stopAutoSlide() {
        handler.removeCallbacks(autoSlideRunnable)
    }

    // 可选：暴露 lifecycleOwner 用于其他用途
    var lifecycleOwner: androidx.lifecycle.LifecycleOwner? = null
}