package com.laiiiii.photorevive.ui.slideshow.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.laiiiii.photorevive.databinding.ItemBannerBinding
import com.laiiiii.photorevive.ui.slideshow.model.EditBannerItem

class EditBannerFragment : Fragment() {

    private var _binding: ItemBannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var item: EditBannerItem

    companion object {
        const val ARG_ITEM = "item"

        fun newInstance(item: EditBannerItem): EditBannerFragment {
            val fragment = EditBannerFragment()
            val args = Bundle().apply {
                putSerializable(ARG_ITEM, item)
            }
            fragment.arguments = args
            return fragment
        }
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

        item = arguments?.getSerializable(ARG_ITEM) as EditBannerItem
        binding.bannerBackground.setBackgroundColor(Color.parseColor(item.color))
        binding.textView.text = item.title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}