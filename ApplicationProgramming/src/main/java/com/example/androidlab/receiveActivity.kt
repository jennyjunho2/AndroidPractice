package com.example.androidlab

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlab.databinding.ActivityReceivepictureBinding
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

var received_bitmap : Bitmap? = null
var successorfail: String? = null

class receiveActivity: AppCompatActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityReceivepictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBackToMain.setOnClickListener { backtomain() }

        try{
            var receive = receivePicture()
            receive.connect()
            val receivedBitmap = received_bitmap
            binding.imageViewResult.setImageBitmap(receivedBitmap)
        } catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun backtomain(){
        val returnMainActivity = Intent(this@receiveActivity, IntroActivity::class.java)
        returnMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(returnMainActivity)
    }
}

class receivePicture {
    lateinit var bytearray: ByteArray
    var img_byte: ByteArray? = null
    var socket: Socket? = null
    val ip = "221.143.52.30"
    val port = 9999 //py server
    private var dos: DataOutputStream? = null
    private var dis: DataInputStream? = null
    private val UTF8_CHARSET = Charset.forName("UTF-8")


    fun connect() {
        var mHandler = Handler()
        Log.w("connect", "연결 하는중")
        val checkUpdate: Thread = object : Thread() {

            fun InputStreamToByteArray(data_len: Int, `in`: DataInputStream): ByteArray {
                val loop = (data_len / 1024)
                val resbytes = ByteArray(data_len)
                var offset = 0
                try {
                    for (i in 0 until loop) {
                        `in`.readFully(resbytes, offset, 1024)
                        offset += 1024
                    }
                    `in`.readFully(resbytes, offset, data_len - loop * 1024)
                    println("resbytes len:" + Integer.toString(resbytes.size))
                    println("image get!!!!")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return resbytes
            }

            override fun run(){
                val data_len: Int
                try {
                    socket = Socket(ip, port)
                    Log.w("서버 접속됨", "서버 접속됨")
                } catch (e1: IOException) {
                    Log.w("서버접속실패", "서버접속실패")
                    e1.printStackTrace()
                }
                try {
                    val dos = DataOutputStream(socket!!.getOutputStream())
                    val dis = DataInputStream(socket!!.getInputStream())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.w("버퍼", "버퍼생성 잘못됨")
                }
                try {
                    Log.w("1번째 문자열 발신", "Android_recv")
                    dos!!.writeUTF("Android_recv")
                    dos!!.flush()
                    Log.w("1번째 문자열 수신", "success/fail")
                    TimeUnit.SECONDS.sleep(10)
                    successorfail = dis!!.readUTF()
                    Log.w("1번째 문자열 수신 완료", "success/fail")
                    println(successorfail)
                    Log.w("2번째 문자열 수신", "이미지 length")
                    data_len = dis!!.readInt()
                    println(data_len)
                    Log.w("3번째 문자열 수신", "이미지 bytearray")
                    img_byte = InputStreamToByteArray(data_len, dis!!)
                    received_bitmap = BitmapFactory.decodeByteArray(img_byte, 0, img_byte!!.size)
                    if (received_bitmap == null){
                        dos!!.writeUTF("Failed")
                        Log.w("failed", "이미지 수신 실패")
                        dos!!.flush()
                        socket!!.close()
                    } else{
                        dos!!.writeUTF("Success")
                        Log.w("success", "이미지 수신 성공")
                        dos!!.flush()
                        socket!!.close()
                    }
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


    companion object {
        var mark: String? = null
        var shape: String? = null
    }

    fun readUTF8(`in`: DataInputStream?): String {
        val length = `in`!!.readInt()
        val encoded = ByteArray(length)
        `in`.readFully(encoded, 0, length)
        return String(encoded, UTF8_CHARSET)
    }
}