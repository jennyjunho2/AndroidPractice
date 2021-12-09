package com.example.androidlab

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlab.databinding.ActivityReceivepictureBinding
import java.io.DataInputStream
import java.io.DataInputStream.readUTF
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

var received_bitmap : Bitmap? = null
var successOrFail: Int? = null

class receiveActivity: AppCompatActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityReceivepictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBackToMain.setOnClickListener { backtomain() }

        try{
            // 사진 수신
            var receive = receivePicture()
            receive.connect()
            val receivedBitmap = received_bitmap
            binding.textViewWaiting.visibility = View.INVISIBLE
            binding.buttonBackToMain.visibility = View.VISIBLE
            binding.imageViewResult.visibility = View.VISIBLE
            binding.textViewResult.visibility = View.VISIBLE
            binding.imageViewResult.setImageBitmap(receivedBitmap)
            if (successOrFail == 48) {
                binding.textViewResult.text = "성공!"
            } else{
                binding.textViewResult.text = "실패!"
            }
        } catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun backtomain(){
        // Back to main
        val returnMainActivity = Intent(this@receiveActivity, IntroActivity::class.java)
        returnMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(returnMainActivity)
    }
}

class receivePicture {
    lateinit var bytearray: ByteArray
    var img_byte: ByteArray? = null
    var socket: Socket? = null
    //val ip = "221.143.52.30"
    val ip = "172.30.1.25"
    val port = 9999 //py server
    private var dos: DataOutputStream? = null
    private var dis: DataInputStream? = null

    // 통신 구현
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
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return resbytes
            }

            override fun run(){
                val data_len: Int
                try {
                    TimeUnit.SECONDS.sleep(3)
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
                    dos!!.writeUTF("Android_recv")
                    dos!!.flush()
                    successOrFail = dis!!.read()
                    data_len = dis!!.readInt()
                    img_byte = InputStreamToByteArray(data_len, dis!!)
                    received_bitmap = BitmapFactory.decodeByteArray(img_byte, 0, img_byte!!.size)
                    if (received_bitmap == null){
                        dos!!.writeUTF("Failed")
                        dos!!.flush()
                        socket!!.close()
                    } else{
                        dos!!.writeUTF("Success")
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
        println("Thread 종료")
    }

}