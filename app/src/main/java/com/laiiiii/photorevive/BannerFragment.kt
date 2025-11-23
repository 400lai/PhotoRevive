package com.laiiiii.photorevive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import com.laiiiii.photorevive.databinding.ItemBannerBinding

class BannerFragment(private val position: Int) : Fragment() {

    private var _binding: ItemBannerBinding? = null
    private val binding get() = _binding!!

    // 设置背景颜色（模拟不同图片 / 与轮播数量一致）
    companion object {
        private val COLORS = listOf(
            "#FF4081", // 粉红
            "#4CAF50", // 绿
            "#2196F3", // 蓝
            "#FF9800", // 橙
            "#9C27B0", // 紫
            "#00BCD4"  // 青
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemBannerBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 使用 position 直接取颜色（确保 position < COLORS.size）
        val color = COLORS.getOrNull(position) ?: COLORS[0] // 安全兜底
        binding.bannerBackground.setBackgroundColor(Color.parseColor(color))

        binding.textView.text = "轮播图 ${position + 1}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}