package com.example.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        super.onStart()

        if(auth.currentUser == null){//로그인정보 저장소 currentUser//현재 로그인이 되어있지 않다면
            startActivity(Intent(this,LoginActivity::class.java))
        }else{//로그인이 되어있다면 (라우터형식??)
            startActivity(Intent(this,LikeActivity::class.java))
            finish()//라이크액티비티가 열리면 메인은 꺼줌
        }
    }
}