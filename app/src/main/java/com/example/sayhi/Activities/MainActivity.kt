package com.example.sayhi.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2
import com.example.sayhi.R
import com.example.sayhi.Adapter.ScreenSlideAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var viewPager: ViewPager2
    private lateinit var tabs: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        viewPager = findViewById(R.id.viewPager)
        tabs = findViewById(R.id.tabs)

        viewPager.adapter = ScreenSlideAdapter(this)
        TabLayoutMediator(
            tabs,
            viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, pos: Int ->
                when (pos) {
                    0 -> tab.text = "CHATS"
                    1 -> tab.text = "PEOPLES"
                }
            }).attach()
    }
}
