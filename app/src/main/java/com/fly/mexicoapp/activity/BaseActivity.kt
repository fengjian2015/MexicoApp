package com.fly.mexicoapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.fly.mexicoapp.R
import com.fly.mexicoapp.utils.SoftKeyboardUtils
import com.gyf.immersionbar.ImmersionBar


abstract class BaseActivity<VB : ViewBinding>(
    private val inflate: (LayoutInflater) -> VB
) : AppCompatActivity() {

    lateinit var binding: VB
    var immersionBar: ImmersionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        initBar()
        initView()
    }

    abstract fun initView()

    private fun initBar() {
        immersionBar = ImmersionBar
            .with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.c_bg)
            .statusBarDarkFont(true)
            .keyboardEnable(true)
        immersionBar?.let {
            it.init()
        }
    }

    override fun finish() {
        super.finish()
        SoftKeyboardUtils.hideSoftKeyboard(this)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(v!!.windowToken, 0)
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }

    /**
     * 点击 EditText 以外的区域 ，自动收起软键盘
     *
     * @param v
     * @param event
     * @return
     */
    open fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }
}
