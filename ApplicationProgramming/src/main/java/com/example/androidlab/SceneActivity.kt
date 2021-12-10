package com.example.androidlab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.androidlab.databinding.ActivityCameraBinding
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class SceneActivity : AppCompatActivity() {

    // activity related
    private var time: Int = 15
    var mCurrentPhotoPath: String? = null
    companion object {
        var ImageCaptureCode = 1
        lateinit var bitmap: Bitmap
    }
    var bitmap2: Bitmap? = null

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다음 intent 인스턴트
        val nextIntent = Intent(this@SceneActivity, sendPictureActivity::class.java)
        // 화면 켜짐 유지
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        timer(period = 1000, initialDelay = 1000){
            time--
            if (time == 0){
                cancel()
                time = 15
                runOnUiThread {
                    Toast.makeText(this@SceneActivity, "사진을 전송중입니다...", Toast.LENGTH_LONG).show()
                }
                try{
                    var filename = "bitmap.png"
                    val stream :FileOutputStream = this@SceneActivity.openFileOutput(filename, Context.MODE_PRIVATE)
                    bitmap2?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.close()
                    bitmap2?.recycle()
                    nextIntent.putExtra("Bitmap", filename)
                    startActivity(nextIntent)
                } catch (e:java.lang.Exception){
                    e.printStackTrace()
                }
            }
            runOnUiThread{
                binding.timersecond.text = time.toString()
            }
        }

        binding.buttonCamera.setOnClickListener { takePicture() }
    }

    private fun takePicture(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val time = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(Date())
        var file: File? = null
        try {
            file = File.createTempFile(
                time,
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
            mCurrentPhotoPath = file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val uri = FileProvider.getUriForFile(this@SceneActivity, "Package.app.applicationprogramming.fileprovider", file!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, ImageCaptureCode)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val imageView = findViewById<ImageView>(R.id.imageView)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImageCaptureCode) {
            if (resultCode == RESULT_OK) {
                val bitmapFile = File(mCurrentPhotoPath)
                var bitmap: Bitmap? = null
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(bitmapFile))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val orientation = getOrientationOfImage(mCurrentPhotoPath)
                try {
                    bitmap = getRotatedBitmap(bitmap, orientation)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                imageView!!.setImageBitmap(bitmap)
                bitmap2 = bitmap
            } else if (resultCode == RESULT_CANCELED) {
                runOnUiThread {
                    Toast.makeText(this@SceneActivity, "Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getOrientationOfImage(filepath: String?): Int {
        val exif: ExifInterface
        exif = try {
            ExifInterface(filepath!!)
        } catch (e: IOException) {
            e.printStackTrace()
            return -1
        }
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        if (orientation != 0) {
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
            }
        }
        return 0
    }

    @Throws(Exception::class)
    fun getRotatedBitmap(bitmap: Bitmap?, degree: Int): Bitmap? {
        if (bitmap == null) return null
        if (degree == 0) return bitmap
        val m = Matrix()
        m.setRotate(degree.toFloat(), bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
        return Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,m,true)
    }
}