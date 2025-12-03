package com.laiiiii.photorevive.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.laiiiii.photorevive.ui.login.LoginActivity

class MineFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        composeView.setContent {
            MineScreen(
                onLoginClick = {
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                },
                onSubscribeClick = {},
                onCreateClick = {}
            )
        }
        return composeView
    }
}
