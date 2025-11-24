package com.laiiiii.photorevive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import com.laiiiii.photorevive.databinding.ItemBannerBinding

// BannerFragment（负责“每一张 Banner 长什么样”）
class BannerFragment(private val position: Int) : Fragment() {

    // 用于 ViewBinding 绑定布局
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

    // 当创建视图时，会被调用，加载当前的 Banner 布局文件
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 使用 ViewBinding 绑定布局文件 ItemBannerBinding,加载 item_banner.xml
        _binding = ItemBannerBinding.inflate(inflater, container, false)
        // 返回绑定后的视图根节点
        return binding.root
    }


    // onViewCreated 会在视图创建完成后被调用
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 根据 position 获取对应的颜色（确保 position < COLORS.size）
        val color = COLORS.getOrNull(position) ?: COLORS[0] // 如果超出范围，使用第一个颜色（回到轮播图首页）

        // 设置背景颜色
        binding.bannerBackground.setBackgroundColor(Color.parseColor(color))

        // 设置显示的文本（轮播图编号）
        binding.textView.text = "轮播图 ${position + 1}"
    }

    // 当视图销毁时，解除绑定，防止内存泄漏
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}