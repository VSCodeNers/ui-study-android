package com.vscodeners.ui_study_android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application  : Application() {

    private var KAKAO_NATIVE_APP_KEY = BuildConfig.KAKAO_NATIVE_APP_KEY
    private var NAVER_CLIENT_ID = BuildConfig.NAVER_CLIENT_ID
    private var NAVER_CLIENT_SECRET = BuildConfig.NAVER_CLIENT_SECRET
    private var NAVER_CLIENT_NAME = BuildConfig.NAVER_CLIENT_NAME

    override fun onCreate() {
        super.onCreate()
        // Kakao SDK 초기화
        KakaoSdk.init(this, KAKAO_NATIVE_APP_KEY)

        // Naver SDK 초기화
        NaverIdLoginSDK.initialize(this, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET, NAVER_CLIENT_NAME)
    }
}