package com.fly.cashhill.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.InputFilter.LengthFilter
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.View
import com.fly.cashhill.R
import com.fly.cashhill.bean.event.HttpEvent
import com.fly.cashhill.databinding.ActivityLoginBinding
import com.fly.cashhill.utils.*

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    override fun initView() {
        HttpEvent.getNewVersion()
        HttpEvent.getProtocolUrl()
        ActivityManager.addCloseActivity(this)
        checkButton()
        binding.include2.toolbarTitle.text = "Sign in"
        binding.include2.toolbarBack.setOnClickListener { finish() }
        val ptvTxt: String = binding.tvp.text.toString()
        val spannableString = SpannableString(ptvTxt)
        CommonUtil.tColorTextClick(spannableString, 30, 47, Color.parseColor("#0BBC79"),
            View.OnClickListener {
                if (SPUtils.getString(Cons.KEY_PROTOCAL_1) == null)return@OnClickListener
                BaseWebActivity.openWebView(this,SPUtils.getString(Cons.KEY_PROTOCAL_1),false)
            })
        CommonUtil.tColorTextClick(spannableString,
            49,
            ptvTxt.length,
            Color.parseColor("#0BBC79"),
            View.OnClickListener {
                if (SPUtils.getString(Cons.KEY_PROTOCAL_6) == null)return@OnClickListener
                BaseWebActivity.openWebView(this,SPUtils.getString(Cons.KEY_PROTOCAL_6),false)
            })
        binding.tvp.text = spannableString
        binding.tvp.movementMethod = LinkMovementMethod.getInstance()
        binding.cb.setOnCheckedChangeListener { _, _ ->
            checkButton()
        }
        binding.etNumber.filters = arrayOf<InputFilter>(LengthFilter(10))
        binding.etNumber.setOnFocusChangeListener { view, b ->
            if (b){
                binding.view1.setBackgroundColor(Color.parseColor("#0BBC79"))
            }else{
                binding.view1.setBackgroundColor(Color.parseColor("#979797"))
            }
        }
        binding.etNumber.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.etNumber.text.toString().startsWith("0")) {
                    binding.etNumber.setText("")
                    return
                }
                if (binding.etNumber.text.toString().contains(" ")) {
                    val txt: String = binding.etNumber.text.toString().replace(" ".toRegex(), "")
                    binding.etNumber.setText(txt)
                    binding.etNumber.setSelection(txt.length)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                checkButton()
            }

        });
        binding.bt.setOnClickListener {
            Cons.sendPhone = binding.etNumber.text.toString()
            startActivity(Intent(this,SendCodeActivity::class.java))
        }
    }

    private fun checkButton(){
        val text = binding.etNumber.text.toString()
        if(TextUtils.isEmpty(text)){
            binding.etNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
        }else{
            binding.etNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
        }

        if (TextUtils.isEmpty(text) || binding.etNumber.text.length != 10){
            binding.bt.setBackgroundResource(R.drawable.shape_button_fail)
            binding.bt.isClickable = false
            binding.bt.isEnabled = false
            return
        }
        if (!binding.cb.isChecked){
            binding.bt.setBackgroundResource(R.drawable.shape_button_fail)
            binding.bt.isClickable = false
            binding.bt.isEnabled = false
            return
        }

        binding.bt.setBackgroundResource(R.drawable.shape_button_ok)
        binding.bt.isClickable = true
        binding.bt.isEnabled = true
    }
}