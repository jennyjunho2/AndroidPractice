package com.example.ch8_event

import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.example.ch8_event.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // 뒤로 가기 버튼을 누른 시각을 저장하는 속성
    var initTime = 0L

    // 멈춘 시각을 저장하는 속성
    var pauseTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener{
            binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
            binding.chronometer.start()
            // 버튼 표시 여부 조정
            binding.stopButton.isEnabled = true
            binding.resetButton.isEnabled = true
            binding.startButton.isEnabled = false
        }
    }
}