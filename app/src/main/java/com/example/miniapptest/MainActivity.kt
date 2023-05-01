package com.example.miniapptest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.miniapptest.R
import com.example.miniapptest.my_miniapp.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val miniapp_1: CardView
        val miniapp_2: CardView
        val miniapp_3: CardView
        val miniapp_4: CardView
        val miniapp_5: CardView
        val miniapp_6: CardView
        miniapp_1 = findViewById<CardView>(R.id.mini_1)
        miniapp_2 = findViewById<CardView>(R.id.mini_2)
        miniapp_3 = findViewById<CardView>(R.id.mini_3)
        miniapp_4 = findViewById<CardView>(R.id.mini_4)
        miniapp_5 = findViewById<CardView>(R.id.mini_5)
        miniapp_6 = findViewById<CardView>(R.id.mini_6)

        miniapp_1.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    MainMiniApp::class.java
                )
            )
        }
        miniapp_2.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    WebApp::class.java
                )
            )
        }
        miniapp_3.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    MiniAppUrl::class.java
                )
            )
        }
        miniapp_4.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    MiniAppAuth::class.java
                )
            )
        }
        miniapp_5.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    DownloadMiniApp::class.java
                )
            )
        }
        miniapp_6.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    ZipMiniApp::class.java
                )
            )
        }
    }
}