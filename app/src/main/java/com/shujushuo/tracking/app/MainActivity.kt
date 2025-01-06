package com.shujushuo.tracking.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.AppBarConfiguration.Builder
import com.shujushuo.tracking.sdk.SdkConfig
import com.shujushuo.tracking.sdk.TrackingSdk

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        TrackingSdk.setLoggingEnabled(true)
        //正常接入，应该使用这个方法。
        //TrackingSdk.initialize(
        //    this.application,
        //    SdkConfig("http://10.1.64.179:8090", "APPID", "DEFAULT")
        //)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 确保布局文件名正确

        val navHostFragment =
            checkNotNull(supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)
        val navController = navHostFragment.navController

        val navView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (navView == null) {
            Log.e("MainActivity", "BottomNavigationView not found")
        } else {
            Log.i("MainActivity", "BottomNavigationView found")
        }
        checkNotNull(navView)
        setupWithNavController(navView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment)
        return navigateUp(navController, Builder(navController.graph).build())
                || super.onSupportNavigateUp()
    }
}