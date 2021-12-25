package com.example.sayhi.Activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2
import com.example.sayhi.R
import com.example.sayhi.Adapter.ScreenSlideAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var viewPager: ViewPager2
    private lateinit var tabs: TabLayout
    private lateinit var btnLogout: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        viewPager = findViewById(R.id.viewPager)
        tabs = findViewById(R.id.tabs)
        btnLogout = findViewById(R.id.btnLogout)

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

        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("Are you sure you want to Log out?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton(
                    "No"
                ) { dialogInterface, i -> }
                .show()
        }

    }
}
