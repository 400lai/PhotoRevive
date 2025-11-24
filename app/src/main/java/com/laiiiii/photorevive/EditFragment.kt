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
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
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
    // 声明一个小圆点指示器容器
    private lateinit var indicatorContainer: LinearLayout
    // ViewPager 的 Adapter，用来提供每一页（BannerFragment）
    private lateinit var bannerAdapter: BannerAdapter

    // 用于记录 ViewPager2 是否处于 Idle/空闲 状态，避免和手动滑动冲突
    private var isViewPagerIdle = true

    // 自动轮播需要的 Handler（Android 里的计时器）
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var btnImportPhoto: MaterialButton
    private lateinit var btnCamera: MaterialButton

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupQuickEntryClicks(view: View) {
        val liveCard = view.findViewById<View>(R.id.card_live)
        val aiFaceCard = view.findViewById<View>(R.id.card_ai_face)
        val puzzleCard = view.findViewById<View>(R.id.card_puzzle)

        liveCard.setOnClickListener {
            showSnackbar("点击了：修实况Live")
        }

        aiFaceCard.setOnClickListener {
            showSnackbar("你点击了：AI修人像")
        }

        puzzleCard.setOnClickListener {
            showSnackbar("你点击了：拼图")
        }

        // 区域4 的八个按钮
        val batchEdit = view.findViewById<View>(R.id.card_batch_edit)
        val hd = view.findViewById<View>(R.id.card_hd)
        val magicRemove = view.findViewById<View>(R.id.card_magic_remove)
        val smartCutout = view.findViewById<View>(R.id.card_smart_cutout)
        val aiEdit = view.findViewById<View>(R.id.card_ai_edit)
        val faceSlender = view.findViewById<View>(R.id.card_face_slender)
        val autoBody = view.findViewById<View>(R.id.card_auto_body)
        val allTools = view.findViewById<View>(R.id.card_all_tools)

        batchEdit.setOnClickListener { showSnackbar("你点击了：批量修图") }
        hd.setOnClickListener { showSnackbar("你点击了：画质超清") }
        magicRemove.setOnClickListener { showSnackbar("你点击了：魔法消除") }
        smartCutout.setOnClickListener { showSnackbar("你点击了：智能抠图") }
        aiEdit.setOnClickListener { showSnackbar("你点击了：AI修图") }
        faceSlender.setOnClickListener { showSnackbar("你点击了：瘦脸瘦身") }
        autoBody.setOnClickListener { showSnackbar( "你点击了：自动美体") }
        allTools.setOnClickListener { showSnackbar("你点击了：所有工具") }

    }

    // 自动轮播的任务（每 3 秒执行一次）
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
        // 使用 ViewBinding 加载 fragment_edit.xml
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = binding.viewPagerBanner
        indicatorContainer = binding.indicatorContainer

        // 初始化 Adapter，告诉 ViewPager2 总共有多少 Banner
        bannerAdapter = BannerAdapter(this)
        viewPager.adapter = bannerAdapter

        // 创建底部的小圆点指示器
        createIndicators()

        // 监听页面滑动变化（用于更新小圆点 & 判断是否空闲）
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            // 当滑到某一页时，更新小圆点状态
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
                super.onPageSelected(position)
            }

            // 监听滑动状态（是否处于空闲）
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                // 更新空闲状态：SCROLL_STATE_IDLE = 0
                isViewPagerIdle = (state == ViewPager2.SCROLL_STATE_IDLE)
            }
        })

        // 启动自动轮播
        startAutoSlide()

        // 区域二_新增：绑定按钮
        btnImportPhoto = binding.btnImportPhoto
        btnCamera = binding.btnCamera

        // 设置点击事件（可暂用 Toast 测试）
        btnImportPhoto.setOnClickListener {
            // TODO: 打开相册选择图片
            showSnackbar("点击了「导入照片」")
        }

        btnCamera.setOnClickListener {
            // TODO: 打开相机
            showSnackbar("点击了「相机」")
        }

        // 区域3_新增： 按钮点击监听
        setupQuickEntryClicks(view)
    }

    // 创建小圆点指示器：圆点数量 = 轮播图数量
    private fun createIndicators() {
        indicatorContainer.removeAllViews()
        for (i in 0 until BANNER_COUNT) {
            val dot = View(context)

            val size = 8.dpToPx()     // 圆点大小
            val margin = 4.dpToPx()   // 圆点之间的间距

            // 设置圆点的布局属性
            val params = LinearLayout.LayoutParams(size, size).apply {
                setMargins(margin, 0, margin, 0)
            }
            dot.layoutParams = params
            // 默认使用未选中的圆点背景
            dot.setBackgroundResource(R.drawable.shape_indicator_dot)
            indicatorContainer.addView(dot)
        }
        updateIndicators(0)     // 第一个圆点设为选中状态
    }

    // 更新小圆点显示：选中某个圆点（当前页亮起）
    private fun updateIndicators(selectedIndex: Int) {
        for (i in 0 until indicatorContainer.childCount) {
            val isSelected = i == selectedIndex

            indicatorContainer.getChildAt(i).setBackgroundResource(
                if (isSelected)
                    R.drawable.shape_indicator_dot_selected // 选中的圆点
                else
                    R.drawable.shape_indicator_dot          // 普通圆点
            )
        }
    }

    // 启动自动轮播（3 秒切下一页）
    private fun startAutoSlide() {
        handler.postDelayed(autoSlideRunnable, 3000)
    }

    // 停止自动轮播（退出页面时调用）
    private fun stopAutoSlide() {
        handler.removeCallbacks(autoSlideRunnable)
    }

    override fun onResume() {
        super.onResume()
        startAutoSlide()    // 页面重新可见时重启轮播
    }

    override fun onPause() {
        super.onPause()
        stopAutoSlide()    // 页面不可见时暂停轮播（省电 & 防止后台乱切页）
    }

    // dp 转 px 扩展函数（Android 必备工具函数）
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    // 当视图销毁时，解除绑定，防止内存泄漏
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}