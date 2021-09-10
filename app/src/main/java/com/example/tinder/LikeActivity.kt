package com.example.tinder

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.stream.DoubleStream.builder
import java.util.stream.IntStream.builder
import java.util.stream.LongStream.builder
import java.util.stream.Stream.builder

class LikeActivity : AppCompatActivity(){
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        userDB = Firebase.database.reference.child("Users")

        val currentUserDB =  userDB.child(getCurrentUserId())
        //DB에서 값을 가져오는 방법 -> 리스너를 달아주는 방법
         currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener{//싱글 이벤트만 가져옴
            override fun onDataChange(snapshot: DataSnapshot) {
                //이름이 변경되었을 때 콜백 넘어옴(snapshot은 유저 정보)
                if(snapshot.child("name").value == null){
                    showNameInputPopup()
                    return
                }
                //유저정보를 갱신해라

            }

            override fun onCancelled(error: DatabaseError) {
                //콜백 넘어옴
            }

        })
    }

    private fun showNameInputPopup(){
        val editText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
            .setView(editText)  //뷰 추가 가능 //editText도 뷰의 종류다
            .setPositiveButton("저장"){_,_ ->
                if(editText.text.isEmpty()){
                    showNameInputPopup()
                }else {
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false) // 뒤로가기나 바깥쪽을 터치해서 팝업을 끌 수도 있기에 취소불가로 설정
            .show()
    }

    private fun saveUserName(name : String){    //save하는 방법
        val userId = getCurrentUserId()
        //reference에서 Users라는 child를 선택
        val currentUserDB = userDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user["userId"] = userId
        user["name"] = name
        currentUserDB.updateChildren(user)      //제일 상위에 DB 안에Users라는 List가 생기고 그안에
        // userId라는 항목으로 오브젝트가 생기고 그안에 user가 저장 유저는 userId를 가지고잇음
        //Users라는 키도 따로 빼둠.


    }

    private fun getCurrentUserId() : String{
       if(auth.currentUser == null){
           Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
           finish()
       }
        return auth.currentUser?.uid.orEmpty()

    }
}