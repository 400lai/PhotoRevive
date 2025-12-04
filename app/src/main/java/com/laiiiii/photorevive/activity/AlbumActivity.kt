package com.laiiiii.photorevive.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.laiiiii.photorevive.databinding.ActivityAlbumBinding
import com.laiiiii.photorevive.ui.recyclerview.AlbumViewModel
import com.laiiiii.photorevive.ui.recyclerview.adapter.MediaItemAdapter
import com.laiiiii.photorevive.utils.permission.PermissionUtil

class AlbumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumBinding
    private lateinit var adapter: MediaItemAdapter
    private lateinit var viewModel: AlbumViewModel
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[AlbumViewModel::class.java]

        // 初始化权限请求
        permissionLauncher = PermissionUtil.requestStoragePermission(this) { isGranted ->
            if (isGranted) {
                viewModel.loadMedia()
            } else {
                Snackbar.make(binding.root, "需要相册权限才能访问图片", Snackbar.LENGTH_LONG).show()
            }
        }

        setupToolbar()
        setupRecyclerView()

        // 观察数据更新
        viewModel.mediaItems.observe(this) { items ->
            adapter.updateItems(items)
        }

        // 权限检查与加载
        if (PermissionUtil.hasStoragePermission(this)) {
            viewModel.loadMedia()
        } else {
            permissionLauncher.launch(PermissionUtil.getRequiredPermission(this))
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = MediaItemAdapter(emptyList()) { mediaItem ->
            // 点击图片项时启动 EditorActivity 并传递图片 URI
            val intent = Intent(this, EditorActivity::class.java).apply {
                putExtra("IMAGE_URI", mediaItem.uri)
            }
            startActivity(intent)
        }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@AlbumActivity, 3)
            adapter = this@AlbumActivity.adapter
        }
    }
}
