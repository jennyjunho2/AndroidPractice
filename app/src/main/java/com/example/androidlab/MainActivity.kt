package com.example.androidlab

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlab.databinding.ActivityMainBinding
import java.util.*
import kotlin.math.roundToInt
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private var time: Int = 3
    var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val nextIntent = Intent(this, SceneActivity::class.java)

        var keywords = getResources().getStringArray(R.array.keywords);
        binding.keyword.setText(keywords[Random().nextInt(keywords.size)])

        timer(period = 1000, initialDelay = 1000){
            time--
            if (time == 0){
                startActivity(nextIntent)
                time = 3
                cancel()
            }
            runOnUiThread{
                binding.timer1.text = time.toString()
            }
        }
    }
}