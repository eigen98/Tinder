package com.example.tinder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {


    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth        //
        callbackManager = CallbackManager.Factory.create()  //콜백매니저 초기화


        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()

    }



    private fun initLoginButton() {
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = getInputEmail()
            val password =  getInputPassword()

            //이메일과 패스워드 파라미터로 auth에 있는 signin기능 사용 가능
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->  //task가 완료가 되었는지 리스너 장착
                    if(task.isSuccessful){//로그인이 성공이라면
                        handleSuccessLogin()    //로그인 액티비티 종료
                    }else{
                        Toast.makeText(this, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            //나머지는 firebase가 알아서 해줌.

        }
    }
    //회원가입
    private fun initSignUpButton() {
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = getInputEmail()
            val password =  getInputPassword()

            //signUp기능 사용
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "회원가입에 성공했습니다. 로그인 버튼을 눌러 로그인해주세요.",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.",Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
    //비었을 때 예외처리
    private fun initEmailAndPasswordEditText(){
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        emailEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()  //비어있지않을 때만 true값 반환
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable

        }
        passwordEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable

        }
    }

    private fun initFacebookLoginButton() {
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)
        //permission설정
        facebookLoginButton.setPermissions("email", "public_profile") //받아올 정보. 이메일과 유저프로필을 받아온다.
        //콜백을 등록해준다.
        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{

            override fun onSuccess(result: LoginResult) {
               //로그인이 성공적 ->로그인 Access토큰을 가져와서 firebase에 넘기는 과정
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)   //토큰생성
                auth.signInWithCredential(credential)   //토큰 넘기기
                    .addOnCompleteListener(this@LoginActivity) {task -> //컨텍스트LoginActivity주의
                        if(task.isSuccessful){
                            handleSuccessLogin()
                        }else{
                            Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.",Toast.LENGTH_SHORT).show()
                        }

                    }
            }

            override fun onCancel() {
                //로그인하다가 취소
            }

            override fun onError(error: FacebookException?) {

                Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()

            }

        })//콜백 추가
    }

    private fun getInputEmail() : String{
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }
    private fun getInputPassword() : String{
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

//    ->콜백매니저 추가
//        (페이스북 버튼 누르면 페이스북 열린 후 완료 후 다시 액티비티로 넘어오는 액티비티 콜백으로 넘어옴. -> OnActivityResult 오버라이드)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSuccessLogin(){
        if(auth.currentUser == null){   //currentUser는 null이 가능함
            Toast.makeText(this, "로그인에 실패했습니다.",Toast.LENGTH_SHORT).show()
            return
        }
    //성공했다면 유저id가져오기
        val userId = auth.currentUser?.uid.orEmpty()    //userid가져오기 //null일경우를 대비해서 orEmpty
        //reference에서 Users라는 child를 선택
        val currentUserDB = Firebase.database.reference.child("Users").child(userId)
        val user = mutableMapOf<String, Any>()
        user["userId"] = userId
        currentUserDB.updateChildren(user)      //제일 상위에 DB 안에Users라는 List가 생기고 그안에
    // userId라는 항목으로 오브젝트가 생기고 그안에 user가 저장 유저는 userId를 가지고잇음
    //Users라는 키도 따로 빼둠.
        finish()

    }

}