package com.fly.cashhill.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.View
import com.fly.cashhill.R
import com.fly.cashhill.bean.event.HttpEvent
import com.fly.cashhill.databinding.ActivitySendCodeBinding
import com.fly.cashhill.network.bean.BaseResponseBean
import com.fly.cashhill.network.bean.HttpErrorBean
import com.fly.cashhill.network.bean.HttpResponse
import com.fly.cashhill.utils.Cons
import com.fly.cashhill.utils.ToastUtils

class SendCodeActivity : BaseActivity<ActivitySendCodeBinding>(ActivitySendCodeBinding::inflate)  {

    var sendTime = 0L
    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (binding.tvTime != null) {
                val currentTime: Long = System.currentTimeMillis() / 1000
                val l: Long =  sendTime- currentTime
                binding.tvTime.text = l.toString()
                if (l <= 0) {
                    timeUi(false)
                    return
                }
                sendEmptyMessage(1)
            }
        }
    }

    override fun initView() {
        binding.include2.toolbarTitle.text = "Sign in"
        binding.include2.toolbarBack.setOnClickListener { finish() }
        binding.etCode.filters = arrayOf<InputFilter>(LengthFilter(4))
        if (Cons.sendPhone.length == 10) {
            var phoneNumber = Cons.sendPhone.substring(7)
            binding.tv3.text = phoneNumber
        }
        binding.tvResend.setOnClickListener {
            httpSendCode()
        }
        binding.etCode.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                p0?.let {
                    val length: Int = it.length
                    //字符串内容存入数组
                    //字符串内容存入数组
                    var c = CharArray(4)
                    c = it.toString().toCharArray()
                    if (c.size >= 1) {
                        binding.tvCode1.text = c[0].toString() + ""
                    } else {
                        binding.tvCode1.text = ""
                    }
                    if (c.size >= 2) {
                        binding.tvCode2.text = c[1].toString() + ""
                    } else {
                        binding.tvCode2.text = ""
                    }
                    if (c.size >= 3) {
                        binding.tvCode3.text = c[2].toString() + ""
                    } else {
                        binding.tvCode3.text = ""
                    }
                    if (c.size >= 4) {
                        binding.tvCode4.text = c[3].toString() + ""
                    } else {
                        binding.tvCode4.text = ""
                    }
                    binding.tvCode1.setTextColor(Color.parseColor("#0BBC79"))
                    binding.tvCode2.setTextColor(Color.parseColor("#0BBC79"))
                    binding.tvCode3.setTextColor(Color.parseColor("#0BBC79"))
                    binding.tvCode4.setTextColor(Color.parseColor("#0BBC79"))

                    if (length == 0) {
                        binding.tvCode1.setBackgroundResource(R.drawable.shape_code_true)
                        binding.tvCode2.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode3.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode4.setBackgroundResource(R.drawable.shape_code_false)
                    } else if (length == 1) {
                        binding.tvCode1.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode2.setBackgroundResource(R.drawable.shape_code_true)
                        binding.tvCode3.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode4.setBackgroundResource(R.drawable.shape_code_false)
                    } else if (length == 2) {
                        binding.tvCode1.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode2.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode3.setBackgroundResource(R.drawable.shape_code_true)
                        binding.tvCode4.setBackgroundResource(R.drawable.shape_code_false)
                    } else if (length == 3) {
                        binding.tvCode1.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode2.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode3.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode4.setBackgroundResource(R.drawable.shape_code_true)
                    } else if (length == 4) {
                        binding.tvCode1.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode2.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode3.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode4.setBackgroundResource(R.drawable.shape_code_false)
                        binding.tvCode1.setTextColor(Color.parseColor("#F6625B"))
                        binding.tvCode2.setTextColor(Color.parseColor("#F6625B"))
                        binding.tvCode3.setTextColor(Color.parseColor("#F6625B"))
                        binding.tvCode4.setTextColor(Color.parseColor("#F6625B"))
                        httpLogin()
                    }
                }
            }
        })
        httpSendCode()
    }

    private fun httpLogin(){
        HttpEvent.loginByPhoneVerifyCode(Cons.sendPhone,binding.etCode.text.toString(),this)
    }

    private fun httpSendCode(){
        HttpEvent.sendVerifyCode(Cons.sendPhone,object : HttpResponse<BaseResponseBean>() {
            override fun businessSuccess(data: BaseResponseBean) {
                if (data.code == 200){
                    sendTime = System.currentTimeMillis() /1000 + 60
                    timeUi(true)
                    handler.sendEmptyMessage(1)
                }else{
                    ToastUtils.showShort(data.message)
                    timeUi(false)
                }
            }

            override fun businessFail(statusCode: Int, httpErrorBean: HttpErrorBean) {
                ToastUtils.showShort(httpErrorBean.message)
                timeUi(false)
            }
        })
    }

    private fun timeUi(isTimeing: Boolean){
        if (isTimeing){
            binding.tvResend.visibility = View.GONE
            binding.llTime.visibility = View.VISIBLE
        }else{
            binding.tvResend.visibility = View.VISIBLE
            binding.llTime.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.post {
            handler.removeMessages(1)
        }
    }
}