package com.example.androidlab

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Camera
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androidlab.databinding.ActivityCameraBinding
import com.example.androidlab.databinding.ActivityIntroBinding
import java.util.*

class SceneActivity : AppCompatActivity() {

    private var time: Int = 3
    var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

//    private fun permissionCheck(){
//        var cameraPermission: Int = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) // 허용일 경우 1
//        var writeExternalStoragePermission: Int = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//
//        if(cameraPermission == PackageManager.PERMISSION_GRANTED &&
//                writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED){ // 두 접근 권한이 모두 허용
//            setupCamera()
//        } else{
//            ActivityCompat.requestPermissions(this, arrayPermission, requestPermission)
//        }
//    }
//
}
