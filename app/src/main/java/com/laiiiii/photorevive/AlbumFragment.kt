package com.laiiiii.photorevive

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.laiiiii.photorevive.databinding.FragmentAlbumBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var adapter: MediaItemAdapter // 成员变量

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadMediaFromDevice()
        } else {
            Snackbar.make(requireView(), "需要相册权限才能访问图片", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun getRequiredPermission(): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private fun loadMediaFromDevice() {
        scope.launch {
            val items = withContext(Dispatchers.IO) {
                queryImagesFromMediaStore(requireContext())
            }
            adapter.updateItems(items)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 正确初始化成员变量 adapter
        adapter = MediaItemAdapter(emptyList())
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            this.adapter = this@AlbumFragment.adapter
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val permission = getRequiredPermission()
        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                loadMediaFromDevice()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun queryImagesFromMediaStore(context: android.content.Context): List<MediaItem> {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.MIME_TYPE
        )

        val items = mutableListOf<MediaItem>()

        // 使用 DATE_TAKEN 排序（更可靠），若为 0 则按 _ID 倒序
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC, ${MediaStore.Images.Media._ID} DESC"

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val mimeType = cursor.getString(mimeColumn)

                // 构造 content:// URI 字符串
                val uri = Uri.withAppendedPath(collection, id.toString()).toString()

                // 可选：过滤非图片类型（如 .gif 算图片，但有些 ROM 会混入视频缩略图）
                if (mimeType.startsWith("image/")) {
                    items.add(MediaItem(id = id, uri = uri, isVideo = false))
                }
            }
        }

        return items
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}