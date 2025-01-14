package com.shujushuo.tracking.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shujushuo.tracking.sdk.TrackingSdk

class MainActivity : AppCompatActivity() {

    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 确保布局文件名正确

        navView = findViewById(R.id.bottom_navigation)
        if (!::navView.isInitialized) {
            Log.e("MainActivity", "BottomNavigationView not found")
        } else {
            Log.i("MainActivity", "BottomNavigationView found")
        }

        // 设置默认选中的 Fragment
        if (savedInstanceState == null) {
            navView.selectedItemId = R.id.navigation_track
            loadFragment(TrackFragment())
        }

        // 设置标签选择监听器
        navView.setOnItemSelectedListener { menuItem ->
            var fragment: Fragment? = null
            when (menuItem.itemId) {
                R.id.navigation_track -> fragment = TrackFragment()
                R.id.navigation_settings -> fragment = SettingsFragment()
                // 如果有更多标签，继续添加
            }

            if (fragment != null) {
                loadFragment(fragment)
                true
            } else {
                false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        // 替换当前 Fragment，并销毁之前的实例
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        // 处理导航向上操作
        return super.onSupportNavigateUp()
    }
}
