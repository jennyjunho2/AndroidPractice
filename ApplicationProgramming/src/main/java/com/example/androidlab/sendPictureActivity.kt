package com.example.androidlab

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlab.databinding.ActivitySendpictureBinding
import java.io.*
import java.io.DataInputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit


class sendPictureActivity : AppCompatActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySendpictureBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_sendpicture)
        val nextIntent = Intent(this, receiveActivity::class.java)

        var bitmap2 : Bitmap? = null
        var bitmap = getIntent().getStringExtra("Bitmap")
        try {
            val open: FileInputStream = this.openFileInput(bitmap)
            bitmap2 = BitmapFactory.decodeStream(open)
            open.close()
        } catch(e: java.lang.Exception) {
            e.printStackTrace()
        }
        var send = sendPicture()
        send.connect(bitmap2!!)
        if (send.message == 83){
            runOnUiThread {
                Toast.makeText(this@sendPictureActivity, "사진 전송이 성공적으로 완료되었습니다!", Toast.LENGTH_LONG).show()
            }
            startActivity(nextIntent)
        } else{
            runOnUiThread {
                Toast.makeText(this@sendPictureActivity, "사진 전송에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
            val returnMainActivity = Intent(this@sendPictureActivity, IntroActivity::class.java)
            returnMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(returnMainActivity)
        }
    }
    }

class sendPicture {
    private var mHandler: Handler? = null
    private var dos: DataOutputStream? = null
    private var dis: DataInputStream? = null
    var socket: Socket? = null
    //val ip = "221.143.52.30"
    val ip = "172.30.1.25"
    val port = 9999 //py server
    var message: Int? = null

        fun connect(rotatedBitmap: Bitmap) {
            mHandler = Handler()
            Log.w("connect", "연결 하는중")
            val checkUpdate: Thread = object : Thread() {
                override fun run() {
                    val byteArray = ByteArrayOutputStream()
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
                    val bytes = byteArray.toByteArray()
                    val bytesSize = bytes.size.toString()
                    // 서버 접속
                    try {
                        socket = Socket(ip, port)
                        Log.w("서버 접속됨", "서버 접속됨")
                    } catch (e1: IOException) {
                        Log.w("서버접속실패", "서버접속실패")
                        e1.printStackTrace()
                    }
                    try {
                        dos = DataOutputStream(socket!!.getOutputStream())
                        dis = DataInputStream(socket!!.getInputStream())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.w("버퍼", "버퍼생성 잘못됨")
                    }
                    try {
                        dos!!.writeUTF("Android")
                        dos!!.flush()
                        Log.w("1번째 문자열", "Android")
                        TimeUnit.SECONDS.sleep(1)
                        dos!!.writeUTF(bytesSize)
                        dos!!.flush()
                        Log.w("2번째 문자열", "bytearray 길이")
                        TimeUnit.SECONDS.sleep(1)
                        dos!!.write(bytes)
                        dos!!.flush()
                        Log.w("3번째 문자열", "bytearray(이미지)")
                        TimeUnit.SECONDS.sleep(1)
                        message = dis!!.read()
                        Log.w("1번째 수신", "응답메시지")
                        println(message)
                        socket!!.close()
                    } catch (e: Exception) {
                        Log.w("error", "error occur")
                    }
                }
            }
            checkUpdate.start()
            try {
                checkUpdate.join()
            } catch (e: InterruptedException) {
            }
            println("Thread terminated")
        }
    }


