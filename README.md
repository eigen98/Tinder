# Tinder

이 챕터를 통해 배우는 것
Firebase Authenetication 사용하기   

    ->email 로그인과정
        ->createUserWithEmailAndPassword()함수 사용을했고 
        addOncompleteListner를 통해서 결과값을 받아옴
        (원래는 retrofit을 통하여 네트워크처리 데이터 처리를 직접해줘야했지만 firebase에서 알아서 다 해줌.)
    ->facebook로그인과정

Firebase Realtime Database 사용하기(Jason, 즉key-value형식으로 저장)
    ->DatabaseReference에서 사용.
    ->reference.child.child.child형태
    ->addListnerForSingleValueEvent 함수는 즉시성으로 발효되고 한 번만 불러옴 -> 유저정보를 불러오기위해 한 번 불러옴
        -> addChildEventListener함수를 후에 사용함으로써 전체 유저 테이블에 대한 change를 다 보고 이벤트를 받음
    -> 

yuyakaido/CardStackView 사용하기

틴더
Firebase Authentication 을 통해 이메일 로그인과 페이스북 로그인을 할 수 있음.

Firebase Realtime Database 를 이용하여 기록을 저장하고, 불러올 수 있음.

Github에서 Opensource Library 를 찾아 사용할 수 있음.


![KakaoTalk_20210912_182304706](https://user-images.githubusercontent.com/68258365/132982270-324561e9-342f-4c3e-ab27-f5881849049c.jpg)
![KakaoTalk_20210912_182304706_01](https://user-images.githubusercontent.com/68258365/132982272-395933ef-7d1d-4326-be93-962fd25f10e2.jpg)






목차
1.인트로 (완성앱 & 구현 기능 소개)
2.Firebase 환경설정하기
    ->Firebase gradle 추가
    ->Authentication 추가, Realtime DataBase 만들기 해줌
    ->Json 설정 파일에 realTimeDatabase권한을 추가해줘야함.
        ->firebase 프로젝트 설정에가서 서비스json파일을 다시 다운로드
        ->기존 json에 붙여넣기해줌.

3.이메일 로그인 구현하기(Authentication)
    ->Firebase콘솔에서 Authentication에서email 로그인 활성화
    -> 프로젝트에 Auth추가 implementation 'com.google.firebase:firebase-auth-ktx'
        ->Firebase.호출 가능해짐.
    ->로그인액티비티 추가
        LoginActivity에서 emailpassword를 받아와서 firebase에 전달하여 추가

        //이메일과 패스워드 파라미터로 auth에 있는 signin기능 사용 가능
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->  //로그인이 성공이라면
                            if(task.isSuccessful){
                                finish()    //로그인 액티비티 종료
                            }else{
                                Toast.makeText(this, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                            }

                        }
        //signUp기능 사용
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) {task ->
                            if(task.isSuccessful){
                                Toast.makeText(this, "회원가입에 성공했습니다. 로그인 버튼을 눌러 로그인해주세요.",Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.",Toast.LENGTH_SHORT).show()
                            }
                        }

        ->이메일 비밀번호 칸이 비어있을 때 예외처리 필요(리스너 이용)

4.Facebook 환경설정하기
    ->연결설정
        https://developers.facebook.com/docs/facebook-login/android/?locale=ko_KR  페이스북 개발자페이지
    ->로그인과 같은 환경구축 클릭-> 표시할 앱 이름 설정, 페이스북 로그인 설정클릭
        ->설정 -> 기본설정에서 앱ID를 복사하여 firebase 페이스북 설정에 붙여넣기, 앱 시크릿코드 복사하여 붙여넣기 후 저장
    ->페이스북 for Developer 에서 firebase와 연결()
    ->gradle에 implementation 'com.facebook.android:facebook-login:8.2.0'
        buildscript {
                mavenCentral() 추가
            }
        allprojects {
                mavenCentral()
            }
        }

    -> 빠른시작으로 돌아와서 안드로이드 프로젝트에 대해서 facebook에 알리기

5.Facebook 로그인 구현하기
https://developers.facebook.com/docs/facebook-login/android
    ->내 앱에서 앱 선택 또는 새앱 만들기

    ->리소스 및 메니페스트 수정
        ->value에 추가 <string name="facebook_app_id">1431286343909792</string> <string name="fb_login_protocol_scheme">fb1431286343909792</string>

    -> application 요소 뒤에 추가 <uses-permission android:name="android.permission.INTERNET"/>
    -> 다음 meta-data 요소, Facebook에 대한 활동, Chrome 맞춤 탭에 대한 활동 및 인텐트 필터를 application 요소 내에 추가합니다.
        <meta-data android:name="com.facebook.sdk.ApplicationId" android:value="@string/facebook_app_id"/>
        <activity android:name="com.facebook.FacebookActivity" android:configChanges= "keyboard|keyboardHidden|screenLayout|screenSize|orientation" android:label="@string/app_name" />
         <activity android:name="com.facebook.CustomTabActivity" android:exported="true">
          <intent-filter> <action android:name="android.intent.action.VIEW" />
          <category android:name="android.intent.category.DEFAULT" />
          <category android:name="android.intent.category.BROWSABLE" />
          <data android:scheme="@string/fb_login_protocol_scheme" />

          </intent-filter> </activity>

    ->릴리즈키는 배포시 고려

    -> 로그인 액티비티로 돌아와서
        페이스북 로그인 버튼은 따로사용용 com.faceook.login.widget.LoginButton

    ->콜백매니저 추가
        (페이스북 버튼 누르면 페이스북 열린 후 완료 후 다시 액티비티로 넘어오는 액티비티 콜백으로 넘어옴. -> OnActivityResult 오버라이드)
        private lateinit var callbackManager: CallbackManager
         callbackManager = CallbackManager.Factory.create() //콜백매니저 초기화

         override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                 super.onActivityResult(requestCode, resultCode, data)

                 callbackManager.onActivityResult(requestCode, resultCode, data)
             }

      private fun initFacebookLoginButton() {
              val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)
              //permission설정
              facebookLoginButton.setPermissions("email", "public_profile") //받아올 정보. 이메일과 유저프로필을 받아온다.
              //콜백을 등록해준다.
              facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{

                  override fun onSuccess(result: LoginResult?) {
                     //로그인이 성공적 -> 로그인 Access토큰을 가져와서 firebase에 넘기는 과정
                  }

                  override fun onCancel() {
                      //로그인하다가 취소
                  }

                  override fun onError(error: FacebookException?) {

                      Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()

                  }

              })//콜백 추가
          }

       -> 로그인이 성공적이라면 LoginResult에서 Access토큰을 가져온다음에 로그인이 완료되었을 때  firebase에 알려주어야함.(access토큰을 우리서버에 저장하는 것이 아님)
       로그인 Access토큰을 가져와서 firebase에 넘기는 과정



6.Firebase Realtime Database 연동하기
    ->이름을 추가하는 과정 실습
    ->likeActivity 생성

    ->Build.gradle에 realTimeDatabase 추가
       implementation 'com.google.firebase:firebase-database-ktx'


7.Swipe Animation 라이브러리 사용해보기

    ->시간 세이브를 위해서 라이브러리 끌어옴 깃허브에서 swipe android stack 검색
    yuyakaido/CardStackView
    (조회수 or 최근 개발 날짜 확인으로 신뢰도 증가)
    installation 부분에서 다운로드 방법 확인
    ->build.gradle 추가
                    dependencies {

                        implementation "com.yuyakaido.android:card-stack-view:${LatestVersion}"
                    }
    -> 모델 클래스 추가
    -> LikeActivity에 CardStackView (추가 깃허브에서 참고)  Setup부분에서 확인 후 sample파일에서 참고
        https://github.com/yuyakaido/CardStackView
        val cardStackView = findViewById<CardStackView>(R.id.card_stack_view)
        cardStackView.layoutManager = CardStackLayoutManager()
        cardStackView.adapter = CardStackAdapter()

    -> activity_like에 CardStackView 추가


8.Like DB 연동하기
    ->
9.Match 된 유저목록 보여주기
10.어떤 것을 추가로 개발할 수 있을까?
마무리
