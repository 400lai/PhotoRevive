package com.laiiiii.photorevive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.laiiiii.photorevive.ui.NavigationBottomBar
import com.laiiiii.photorevive.ui.home.EditFragment
import com.laiiiii.photorevive.ui.home.InspirationFragment
import com.laiiiii.photorevive.ui.home.MineFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: ComposeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.compose_view)

        // 默认加载 EditFragment
        if (savedInstanceState == null) {
            loadFragment(EditFragment())
        }

        setupComposeBottomNavigation()
    }

    private fun setupComposeBottomNavigation() {
        bottomNav.setContent {
            val currentSelectedItem = remember { mutableStateOf(R.id.nav_edit) }

            NavigationBottomBar(
                selectedItem = currentSelectedItem.value,
                onItemSelected = { id ->
                    if (currentSelectedItem.value != id) {
                        currentSelectedItem.value = id  // 触发 Compose 重组
                        val selectedFragment: Fragment = when (id) {
                            R.id.nav_edit -> EditFragment()
                            R.id.nav_inspiration -> InspirationFragment()
                            R.id.nav_mine -> MineFragment()
                            else -> EditFragment()
                        }
                        loadFragment(selectedFragment)
                    }
                }
            )
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
