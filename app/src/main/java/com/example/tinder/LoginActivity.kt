package com.example.tinder

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class LoginActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)


        initLoginButton()
        initSignUpButton()

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
                        finish()    //로그인 액티비티 종료
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
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable

        }
        passwordEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable

        }


    }

    private fun getInputEmail() : String{
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }
    private fun getInputPassword() : String{
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

}