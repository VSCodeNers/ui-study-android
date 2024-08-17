package com.vscodeners.ui_study_android.feature

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.vscodeners.ui_study_android.feature.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var kakaoCallback: (OAuthToken?, Throwable?) -> Unit

    // nullable한 FirebaseAuth 객체 선언
    var auth: FirebaseAuth? = null
    lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //auth 객체 초기화
        auth = FirebaseAuth.getInstance()

        setKakaoCallback()

        // status bar 보여주기
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(true)
            requireActivity().window.insetsController?.show(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE)
        } else {
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        // 카카오 로그인
        binding.btnKakaoLogin.setOnClickListener {
            handleKakaoLogin()
        }

        // 네이버 로그인
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가

            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(requireActivity(),"errorCode:$errorCode, errorDesc:$errorDescription",
                    Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(requireContext(), oauthLoginCallback)
        binding.btnNaverLogin.setOAuthLogin(oauthLoginCallback)

        // Google Login
        var signInRequest = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //기본 로그인 방식 사용
            .requestIdToken(BuildConfig.FIREBASE_GOOGLE_CLIENT_ID)
            //requestIdToken :필수사항이다. 사용자의 식별값(token)을 사용하겠다.
            //(App이 구글에게 요청)
            .requestEmail()
            // 사용자의 이메일을 사용하겠다.(App이 구글에게 요청)
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), signInRequest)

        binding.btnGoogleLogin.setOnClickListener {
            googleLogin()
        }
    }
    // 카카오 로그인 callback 세팅
    private fun setKakaoCallback() {
        kakaoCallback = { token, error ->
            if (error != null) { // 에러가 있는 경우
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        showToast("접근이 거부 됨(동의 취소)")
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        showToast("유효하지 않은 앱")
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        showToast("인증 수단이 유효하지 않아 인증할 수 없는 상태")
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        showToast("요청 파라미터 오류")
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        showToast("유효하지 않은 scope ID")
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        showToast("설정이 올바르지 않음(android key hash)")
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        showToast("서버 내부 에러")
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        showToast("앱이 요청 권한이 없음")
                    }
                    else -> { // Unknown
                        showToast("기타 에러")
                    }
                }
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            }
            else if (token != null) { // 토큰을 받아온 경우
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken} / ${token.refreshToken} / ${token.idToken}")
            }
        }
    }

    // 카카오 로그인
    private fun handleKakaoLogin() {
        val context = requireContext()
        lifecycleScope.launch {
            try {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                    // 카카오톡 앱이 설치되어 있고, 연결된 계정이 있는 경우 카카오톡 앱으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoTalk(context, callback = kakaoCallback)
                    Log.d(TAG, "카카오톡으로 로그인")
                } else {
                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
                    Log.d(TAG, "카카오 계정으로 로그인")
                }
            } catch (error: Throwable) {
                // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    Log.d(TAG, "사용자의 의도적인 로그인 취소")
                } else {
                    Log.e(TAG, "인증 에러 발생", error)
                }
            }
        }
    }

    // Google Login
    private fun googleLogin() {
        //1. 구글로 로그인을 한다.
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,1004)
    }

    override fun onActivityResult(requestCode:Int, resultCode: Int, data: Intent?){
        //Activity.Result_OK : 정상완료
        //Activity.Result_CANCEL : 중간에 취소 되었음(실패)
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1004){
            if(resultCode == Activity.RESULT_OK){
                //결과 Intent(data 매개변수) 에서 구글로그인 결과 꺼내오기
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)!!

                //정상적으로 로그인되었다면
                if(result.isSuccess){
                    //우리의 Firebase 서버에 사용자 이메일정보보내기
                    val account = result.signInAccount
                    firebaseAuthWithGoogle(account)
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        //구글로부터 로그인된 사용자의 정보(Credentail)을 얻어온다.
        val credential = GoogleAuthProvider.getCredential(account?.idToken!!, null)
        //그 정보를 사용하여 Firebase의 auth를 실행한다.
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {  //통신 완료가 된 후 무슨일을 할지
                    task ->
                if (task.isSuccessful) {
                    // 로그인 처리를 해주면 됨!
                    //goMainActivity(task.result?.user)
                    showToast("구글 로그인 완료")
                } else {
                    // 오류가 난 경우!
                    task.exception?.message?.let { showToast(it) }
                }
                //progressBar.visibility = View.GONE
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}