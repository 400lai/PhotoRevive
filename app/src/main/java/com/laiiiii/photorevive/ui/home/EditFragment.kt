// file: EditFragment.kt
package com.laiiiii.photorevive.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.laiiiii.photorevive.R
import com.laiiiii.photorevive.activity.AlbumActivity
import com.laiiiii.photorevive.databinding.FragmentEditBinding
import com.laiiiii.photorevive.ui.slideshow.EditBannerView

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化轮播组件
        val bannerView = binding.editBannerView
        bannerView.setupWithFragment(this)
        bannerView.startAutoSlide()

        // 绑定卡片：启动 AlbumActivity
        binding.cardImportPhoto.setOnClickListener {
            val intent = Intent(requireContext(), AlbumActivity::class.java)
            startActivity(intent)
        }

        binding.cardCamera.setOnClickListener {
            showSnackbar("点击了「相机」")
        }

        setupQuickEntryClicks(view)
    }

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

        cards.forEach { (id, msg) ->
            view.findViewById<View>(id).setOnClickListener {
                showSnackbar("你点击了：$msg")
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        binding.editBannerView.startAutoSlide()
    }

    override fun onPause() {
        super.onPause()
        binding.editBannerView.stopAutoSlide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
