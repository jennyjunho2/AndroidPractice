package com.example.androidlab


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androidlab.databinding.ActivityIntroBinding


class IntroActivity : AppCompatActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        // 레이아웃 바인딩 & 표시
        super.onCreate(savedInstanceState)
        val binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 권한 확인
        binding.startButton.setOnClickListener{
            checkPermission()
        }
    }

    // 권환 확인 함수
    fun checkPermission() {
        // 카메라, 저장공간 권한
        val cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        val externalStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val internetPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
        val accessNetworkStatePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE)
        if (cameraPermission == PackageManager.PERMISSION_GRANTED
            && externalStoragePermission == PackageManager.PERMISSION_GRANTED
            && internetPermission == PackageManager.PERMISSION_GRANTED
            && accessNetworkStatePermission == PackageManager.PERMISSION_GRANTED) { // 권한 있는 경우
            startProcess()
        } else {
            requestPermission()
        }
    }
    // 권한 획득 후 실행
    private fun startProcess() {
        val nextIntent = Intent(this, MainActivity::class.java)
        startActivity(nextIntent)
    }
    // 권한 요청 함수
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE), 99)
    }
    // 요청 이후 함수...
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            99 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startProcess()
                } else {
                    Log.d("MainActivity", "종료")
                }
            }
        }
    }

}