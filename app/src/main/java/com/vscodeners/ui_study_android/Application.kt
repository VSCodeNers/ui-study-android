package com.vscodeners.ui_study_android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class Application  : Application() {

    private var KAKAO_NATIVE_APP_KEY = BuildConfig.KAKAO_NATIVE_APP_KEY

    override fun onCreate() {
        super.onCreate()
        // Kakao SDK 초기화
         KakaoSdk.init(this, KAKAO_NATIVE_APP_KEY)
    }
}