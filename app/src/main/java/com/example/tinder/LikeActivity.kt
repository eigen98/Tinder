package com.example.tinder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tinder.DBKey.Companion.DIS_LIKE
import com.example.tinder.DBKey.Companion.LIKE
import com.example.tinder.DBKey.Companion.LIKED_BY
import com.example.tinder.DBKey.Companion.NAME
import com.example.tinder.DBKey.Companion.USERS
import com.example.tinder.DBKey.Companion.USER_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction


class LikeActivity : AppCompatActivity(), CardStackListener{
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB : DatabaseReference

    private val adapter = CardItemAdapter()
    private val cardItems = mutableListOf<CardItem>()
    private val manager by lazy {
        CardStackLayoutManager(this,this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        userDB = Firebase.database.reference.child(USERS) //좀더 상위 항목

        val currentUserDB =  userDB.child(getCurrentUserId())

        //DB에서 값을 가져오는 방법 -> 리스너를 달아주는 방법
         currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener{//싱글 이벤트만 가져옴 //최초한번
            override fun onDataChange(snapshot: DataSnapshot) {
                //이름이 변경되었을 때 콜백 넘어옴(snapshot은 유저 정보)
                if(snapshot.child(NAME).value == null){
                    showNameInputPopup()
                    return
                }
                //null이 아니라면 유저정보를 갱신해라
                getUnSelectedUsers()

            }

            override fun onCancelled(error: DatabaseError) {
                //콜백 넘어옴
            }

        })

        initCardStackView()
        initSignOutButton()
        initMatchedListButton()

    }
    private fun initCardStackView(){
        val stackView = findViewById<CardStackView>(R.id.cardStackView)
        stackView.layoutManager = manager
        stackView.adapter = adapter

    }

    private fun initSignOutButton(){
        val signOutButton = findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()// 메인엑티비티에서 onStart 이후 likeActivity를 꺼도 다시 열리는 버그 수정 필요

        }
    }
    private fun initMatchedListButton(){
        val matchedListButton = findViewById<Button>(R.id.matchListButton)
        matchedListButton.setOnClickListener {
            startActivity(Intent(this,MatchedUserActivity::class.java))
        }

    }
    private fun getUnSelectedUsers(){
        userDB.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.child(USER_ID).value != getCurrentUserId()
                    && snapshot.child(LIKED_BY).child(LIKE).hasChild(getCurrentUserId()).not()//like를 추가하는데 그 안에 내가 있는가? &&
                    && snapshot.child(LIKED_BY).child(DIS_LIKE).hasChild(getCurrentUserId()).not()){

                    val userId = snapshot.child(USER_ID).value.toString()
                    var name = "undecided" //초기값
                    if(snapshot.child(NAME).value != null){
                        name = snapshot.child(NAME).value.toString()
                    }

                    cardItems.add(CardItem(userId, name))
                    adapter.submitList(cardItems)
                    adapter.notifyDataSetChanged() //리사이클러뷰 갱신
                }


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {   //이름이 바뀌었을경우

                cardItems.find { it.userId == snapshot.key }?.let {
                    it.name = snapshot.child(NAME).value.toString()
                }
                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged() //리사이클러뷰 최신화
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showNameInputPopup(){
        val editText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
            .setView(editText)  //뷰 추가 가능 //editText도 뷰의 종류다
            .setPositiveButton("저장"){_,_ ->
                if(editText.text.isEmpty()){    //입력안 했을 시 예외처리
                    showNameInputPopup()    //다시 띄우기
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
        user[USER_ID] = userId
        user[NAME] = name
        currentUserDB.updateChildren(user)   //유저 정보 업데이트
    //제일 상위에 DB 안에Users라는 List가 생기고 그안에
        // userId라는 항목으로 오브젝트가 생기고 그안에 user가 저장 유저는 userId를 가지고잇음
        //Users라는 키도 따로 빼둠.

        getUnSelectedUsers()//유저 정보 갱신


    }

    private fun getCurrentUserId() : String{
       if(auth.currentUser == null){
           Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
           finish()
       }
        return auth.currentUser?.uid.orEmpty()

    }

    private fun like(){
        val card = cardItems[manager.topPosition-1]
        cardItems.removeFirst()

        userDB.child(card.userId)
            .child(LIKED_BY)
            .child(LIKE)
            .child(getCurrentUserId())
            .setValue(true)

        saveMatchIfOtherUserLikedMe(card.userId)
        // 매칭이 된 시점을 봐야한다.
        Toast.makeText(this,"${card.name}님을 Like 하셨습니다.", Toast.LENGTH_SHORT).show()
    }
    private fun disLike(){
        val card = cardItems[manager.topPosition-1]
        cardItems.removeFirst()

        userDB.child(card.userId)
            .child(LIKED_BY)
            .child(DIS_LIKE)
            .child(getCurrentUserId())
            .setValue(true)


        Toast.makeText(this,"${card.name}님을 disLike 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun saveMatchIfOtherUserLikedMe(otherUserId : String){
        val otherUserDB = userDB.child(getCurrentUserId()).child(LIKED_BY).child(LIKE).child(otherUserId)
        //true라면 매칭, 없으면 X
        otherUserDB.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value ==true){
                    userDB.child(getCurrentUserId())
                        .child(LIKED_BY)
                        .child("match")
                        .child(otherUserId)
                        .setValue(true)

                    userDB.child(otherUserId)
                        .child("likedBy")
                        .child("match")
                        .child(getCurrentUserId())
                        .setValue(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    override fun onCardSwiped(direction: Direction?) {//이부분만 사용
        when(direction){
            Direction.Right-> like()
            Direction.Left -> disLike()
            else ->{

            }
        }

    }

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View?, position: Int) {}

    override fun onCardDisappeared(view: View?, position: Int) {

    }
}