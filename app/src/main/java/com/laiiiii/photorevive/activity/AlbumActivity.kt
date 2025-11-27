package com.laiiiii.photorevive.activity

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.laiiiii.photorevive.MediaItem
import com.laiiiii.photorevive.MediaItemAdapter
import com.laiiiii.photorevive.databinding.ActivityAlbumBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumBinding
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var adapter: MediaItemAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadMediaFromDevice()
        } else {
            Snackbar.make(binding.root, "需要相册权限才能访问图片", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()

        val permission = getRequiredPermission()
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadMediaFromDevice()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = MediaItemAdapter(emptyList())
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@AlbumActivity, 3)
            this.adapter = this@AlbumActivity.adapter
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
                queryImagesFromMediaStore(this@AlbumActivity)
            }
            adapter.updateItems(items)
        }
    }

    private fun queryImagesFromMediaStore(context: android.content.Context): List<MediaItem> {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.MIME_TYPE
        )

        val items = mutableListOf<MediaItem>()

        // 按拍摄时间倒序，再按 ID 倒序（避免 DATE_TAKEN=0 的图片乱序）
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

                if (mimeType.startsWith("image/")) {
                    val uri = Uri.withAppendedPath(collection, id.toString()).toString()
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
}