package com.fly.mexicoapp.activity


import com.fly.mexicoapp.databinding.ActivityStartBinding

class StartActivity : BaseActivity<ActivityStartBinding>(ActivityStartBinding::inflate) {

    override fun initView() {
        BaseWebActivity.openWebView(this,"file:///android_asset/h5.html",false)
    }
}