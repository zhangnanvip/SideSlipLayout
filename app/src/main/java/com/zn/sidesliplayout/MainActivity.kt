package com.zn.sidesliplayout

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layout = TextView(this)
        layout.text = "hahahahahahh"
        layout.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        layout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        ssl_test.inflaterLayout(layout)

        val menu1 = TextView(this)
        val menu2 = TextView(this)
        val menu3 = TextView(this)
        menu1.text = "11111111"
        menu2.text = "22222222"
        menu3.text = "33333333"
        menu1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light))
        menu2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        menu3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright))
        val menus = arrayListOf<View>(menu1, menu2, menu3)
        ssl_test.inflaterMenus(menus)

        ssl_test.onLayoutClickListener = object: SideSlipLayout.OnLayoutClickListener {
            override fun onClick() {
                Toast.makeText(this@MainActivity,"layout",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
