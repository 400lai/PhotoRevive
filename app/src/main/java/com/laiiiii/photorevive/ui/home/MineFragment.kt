package com.laiiiii.photorevive.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.laiiiii.photorevive.activity.AlbumActivity
import com.laiiiii.photorevive.activity.HelperActivity
import com.laiiiii.photorevive.data.local.AppPreferences
import com.laiiiii.photorevive.data.local.UserDatabase
import com.laiiiii.photorevive.data.repository.UserRepository
import com.laiiiii.photorevive.ui.login.LoginActivity

class MineFragment : Fragment() {
    private lateinit var viewModel: MineViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        // 初始化 ViewModel
        val db = UserDatabase.getDatabase(requireContext())
        val prefs = AppPreferences(requireContext())
        val repo = UserRepository(db.userDao(), prefs)
        viewModel = ViewModelProvider(this, MineViewModelFactory(repo, prefs)).get(MineViewModel::class.java)

        composeView.setContent {
            MaterialTheme {
                MineScreen(
                    viewModel = viewModel,
                    onLoginClick = {
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                    },
                    onSubscribeClick = {},
                    onCreateClick = {
                        val intent = Intent(context, AlbumActivity::class.java)
                        startActivity(intent)
                    },
                    onEditProfileClick = {},
                    onHelpClick = {
                        val intent = Intent(context, HelperActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }

        return composeView
    }
}
