package com.example.translator

import android.os.Bundle
import android.os.SystemClock
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.security.MessageDigest
import android.annotation.SuppressLint as SuppressLint1


class MainActivity : AppCompatActivity() {
    var requestBtn: Button? = null
    var showText: TextView? = null
    var deleteBtn: ImageButton?=null
    val okhttpListener = object : EventListener() {
        override fun dnsStart(call: Call, domainName: String) {
            super.dnsStart(call, domainName)
            //showText?.text = showText?.text.toString() + "\nDns Search:" + domainName
        }

        override fun responseBodyStart(call: Call) {
            super.responseBodyStart(call)
            showText?.text = "\n连接成功"
        }
    }
    val client: OkHttpClient = OkHttpClient
        .Builder()
        //.addInterceptor(TimeConsumeInterceptor())
        .eventListener(okhttpListener).build()


    val gson = GsonBuilder().create()

    private var lastTriggerTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestBtn = findViewById(R.id.send_request)
        showText = findViewById(R.id.show_text)
        deleteBtn=findViewById(R.id.deletetext)
        deleteBtn?.setOnClickListener {

                delete()

        }
        requestBtn?.setOnClickListener {
            if(System.currentTimeMillis()-lastTriggerTime>1000)
            click()
            lastTriggerTime=System.currentTimeMillis()
        }
    }

    fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        client.newCall(request).enqueue(callback)
    }
    fun delete()
    {
        val editText=findViewById<EditText>(R.id.box)
        editText.setText("")

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
        val lang= findViewById<Switch>(R.id.changelang)
        val editText = findViewById<EditText>(R.id.box)
        var target = " "+editText.text
        var language="zh"
        if(lang.isChecked())
            language="en"
        else language="zh"

        var sign="20211121001005133"+target+"1GZwwNV1Uk0ds4zBNPGGC"
        sign=md5(sign)
        val url = "https://fanyi-api.baidu.com/api/trans/vip/translate?q=$target&from=auto&to=$language&appid=20211121001005133&salt=1&sign=$sign"
        if(target.firstOrNull{it=='&'}!=null)
            showText?.text =" 存在非法字符 "
        else{
            request(url, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                showText?.text = "翻译失败"
            }

            @SuppressLint1("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                val jsonBean = gson.fromJson(
                    bodyString,
                    JsonRootBean::class.java

                )
            val temp=jsonBean.trans_result.elementAt(0).dst.toString()

                if(!temp.isEmpty())
                {
                        showText?.text =
                            "翻译结果: \n ${temp} "
                }
            }
        })
    }
}}
