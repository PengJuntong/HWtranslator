package com.example.translator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.security.MessageDigest
import android.annotation.SuppressLint as SuppressLint1

class MainActivity : AppCompatActivity() {
    var requestBtn: Button? = null
    var showText: TextView? = null

    val okhttpListener = object : EventListener() {
        override fun dnsStart(call: Call, domainName: String) {
            super.dnsStart(call, domainName)
            //showText?.text = showText?.text.toString() + "\nDns Search:" + domainName
        }

        override fun responseBodyStart(call: Call) {
            super.responseBodyStart(call)
            showText?.text = showText?.text.toString() + "\n连接成功"
        }
    }
    val client: OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(TimeConsumeInterceptor())
        .eventListener(okhttpListener).build()

    val gson = GsonBuilder().create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestBtn = findViewById(R.id.send_request)
        showText = findViewById(R.id.show_text)

        requestBtn?.setOnClickListener {
            showText?.text = ""
            click()
        }
    }

    fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        client.newCall(request).enqueue(callback)
    }
    fun md5(content: String): String {
        val hash = MessageDigest.getInstance("MD5").digest(content.toByteArray())
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            var str = Integer.toHexString(b.toInt())
            if (b < 0x10) {
                str = "0$str"
            }
            hex.append(str.substring(str.length -2))
        }
        return hex.toString()
    }
    fun click() {
        val editText = findViewById<EditText>(R.id.box)
        var target = editText.text
        var sign="20211121001005133"+target+"12343234543GZwwNV1Uk0ds4zBNPGGC"
        sign=md5(sign)
        val url = "https://fanyi-api.baidu.com/api/trans/vip/translate?q=$target&from=auto&to=zh&appid=20211121001005133&salt=12343234543&sign=$sign"
        request(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showText?.text = e.message
            }

            @SuppressLint1("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                val jsonBean = gson.fromJson(
                    bodyString,
                    JsonRootBean::class.java

                )



                showText?.text =
                        "Result: ${jsonBean.trans_result.elementAt(0).dst} \n"
            }
        })
    }
}