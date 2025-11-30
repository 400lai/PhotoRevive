package com.laiiiii.photorevive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.laiiiii.photorevive.ui.home.EditFragment
import com.laiiiii.photorevive.ui.home.InspirationFragment
import com.laiiiii.photorevive.ui.home.MineFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_navigation)

        // 默认显示“修图”页面
        loadFragment(EditFragment())

        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_edit -> EditFragment()
                R.id.nav_inspiration -> InspirationFragment()
                R.id.nav_mine -> MineFragment()
                else -> EditFragment()
            }
            loadFragment(selectedFragment)
            true1
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}