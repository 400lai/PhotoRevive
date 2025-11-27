package com.laiiiii.photorevive.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.laiiiii.photorevive.databinding.ActivityAlbumBinding
import com.laiiiii.photorevive.ui.recyclerview.AlbumViewModel
import com.laiiiii.photorevive.ui.recyclerview.adapter.MediaItemAdapter

class AlbumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumBinding
    private lateinit var adapter: MediaItemAdapter
    private lateinit var viewModel: AlbumViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadMedia()
        } else {
            Snackbar.make(binding.root, "需要相册权限才能访问图片", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[AlbumViewModel::class.java]

        setupToolbar()
        setupRecyclerView()

        // 观察数据更新
        viewModel.mediaItems.observe(this) { items ->
            adapter.updateItems(items)
        }

        // 权限检查与加载
        if (hasStoragePermission()) {
            viewModel.loadMedia()
        } else {
            requestPermissionLauncher.launch(getRequiredPermission())
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = MediaItemAdapter(emptyList())
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@AlbumActivity, 3)
            this.adapter = this@AlbumActivity.adapter
        }
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, getRequiredPermission()) == PackageManager.PERMISSION_GRANTED
    }

    private fun getRequiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
}