package com.laiiiii.photorevive.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.laiiiii.photorevive.R
import com.laiiiii.photorevive.activity.AlbumActivity
import com.laiiiii.photorevive.databinding.FragmentEditBinding
import com.laiiiii.photorevive.utils.camera.CameraUtil
import com.laiiiii.photorevive.utils.permission.PermissionUtil

/**
 * 编辑页面 Fragment
 * 提供图片导入、拍照及各种图像编辑功能入口
 */
class EditFragment : Fragment() {

    // ViewBinding 引用，使用可空类型避免内存泄漏
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    // 相机工具类实例
    private lateinit var cameraUtil: CameraUtil
    // 权限请求启动器
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    /**
     * 相机启动回调
     * 处理拍照结果，将照片保存到公共相册
     */
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 拍照成功，将私有目录图片保存到公共相册
            val photoFile = cameraUtil.getCurrentPhotoFile()
            cameraUtil.saveImageToPublicGallery(photoFile) {
                showSnackbar("拍照成功")
            }
        }
    }

    /**
     * 创建 Fragment 的视图
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * 视图创建完成后初始化 UI 和功能
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化相机工具类
        cameraUtil = CameraUtil(this)

        // 初始化权限请求 - 请求相机权限
        permissionLauncher = PermissionUtil.requestCameraPermission(this) { isGranted ->
            if (isGranted) {
                // 权限已授予，触发相机
                cameraUtil.preparePhotoFile()
                cameraUtil.launchCamera(cameraLauncher)
            } else {
                showSnackbar("需要相机权限才能拍照")
            }
        }

        // 设置轮播图
        binding.editBannerView.setupWithFragment(this)
        binding.editBannerView.startAutoSlide()

        // 设置导入照片按钮点击事件 - 跳转到相册页面
        binding.cardImportPhoto.setOnClickListener {
            startActivity(Intent(requireContext(), AlbumActivity::class.java))
        }

        // 设置拍照按钮点击事件
        binding.cardCamera.setOnClickListener {
            handleCameraClick()
        }

        // 设置快捷功能入口点击事件
        setupQuickEntryClicks(view)
    }

    // 处理拍照按钮点击事件，检查权限并启动相机
    private fun handleCameraClick() {
        if (PermissionUtil.hasCameraPermission(requireContext())) {
            cameraUtil.preparePhotoFile()
            cameraUtil.launchCamera(cameraLauncher)
        } else {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    // 设置快捷功能入口的点击事件
    private fun setupQuickEntryClicks(view: View) {
        val cards = listOf(
            R.id.card_live to "修实况Live",
            R.id.card_ai_face to "AI修人像",
            R.id.card_puzzle to "拼图",
            R.id.card_batch_edit to "批量修图",
            R.id.card_hd to "画质超清",
            R.id.card_magic_remove to "魔法消除",
            R.id.card_smart_cutout to "智能抠图",
            R.id.card_ai_edit to "AI修图",
            R.id.card_face_slender to "瘦脸瘦身",
            R.id.card_auto_body to "自动美体",
            R.id.card_all_tools to "所有工具"
        )

        // 为每个卡片设置点击事件，显示对应的功能名称
        cards.forEach { (id, msg) ->
            view.findViewById<View>(id).setOnClickListener {
                showSnackbar("你点击了：$msg")
            }
        }
    }

    // 显示底部提示消息
    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    // Fragment 恢复时重新开始轮播图自动播放
    override fun onResume() {
        super.onResume()
        binding.editBannerView.startAutoSlide()
    }

    override fun onPause() {
        super.onPause()
        binding.editBannerView.stopAutoSlide()
    }

    // 销毁视图时清理资源，避免内存泄漏
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
