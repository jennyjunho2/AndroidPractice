package com.example.androidlab

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlab.SceneActivity
import com.example.androidlab.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private var time: Int = 3
    var timer = Timer()

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 레이아웃 바인딩 & 표시
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다음 액티비티와 연계
        val nextIntent = Intent(this, SceneActivity::class.java)

        // 제시어 랜덤 표시
        var keywords = getResources().getStringArray(R.array.keywords);
        binding.keyword.setText(keywords[Random().nextInt(keywords.size)])

        // 3초 타이머 오브젝트
        timer(period = 1000, initialDelay = 1000){
            time--
            if (time == -1){
                // 0초일 시 다음 액티비티 실행
                startActivity(nextIntent)
                time = 3
                cancel()
            }
            // 쓰레드 지정(안할 시 디스플레이 오류)
            runOnUiThread{
                binding.timer1.text = time.toString()
            }
        }
    }
}