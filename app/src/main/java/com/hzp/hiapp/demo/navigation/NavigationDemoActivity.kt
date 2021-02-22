package com.hzp.hiapp.demo.navigation

import android.net.Uri
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.hzp.hiapp.R

class NavigationDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_demo)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //寻找出路由控制器对象，它是我们路由跳转的唯一入口
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        //将navController和BottomNavigationView绑定,形成联动效果
        navView.setupWithNavController(navController)


//        //路由跳转
//        navController.navigate(R.id.navigation_notifications)
//        //传递参数跳转
//        navController.navigate(R.id.navigation_notifications, Bundle.EMPTY)
//        //deepLink跳转
//        navController.navigate(Uri.parse("www.baidu.com"))
//
//        //回退
//        navController.navigateUp()
//        //notis->dashboard
//        navController.popBackStack(R.id.navigation_dashboard,false)
//        //notis->dashboard->home
//        navController.popBackStack(R.id.navigation_dashboard,true)
    }
}