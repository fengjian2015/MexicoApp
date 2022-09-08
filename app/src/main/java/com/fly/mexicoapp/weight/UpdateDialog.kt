package com.fly.mexicoapp.weight

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fly.mexicoapp.R
import com.fly.mexicoapp.bean.UpdateBean

class UpdateDialog constructor(updateBean :UpdateBean): DialogFragment() {
    private var updateBean :UpdateBean
    private lateinit var tvContent:TextView
    private lateinit var buttonUpload:Button
    private lateinit var buttonWait:Button

    init {
        this.updateBean =updateBean
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.layout_update_dialog, null)
        tvContent =rootView.findViewById(R.id.tv_content)
        buttonUpload =rootView.findViewById(R.id.buttonUpload)
        buttonWait =rootView.findViewById(R.id.buttonWait)
        initData()
        return rootView
    }

    private fun initData(){
        isCancelable = updateBean.must != 1
        tvContent.text = updateBean.tips
        initButton(resources.getString(R.string.update_update_now))
        buttonUpload.setOnClickListener {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW" //Intent.ACTION_VIEW
            val content_url = Uri.parse(updateBean.url)
            intent.data = content_url
            startActivity(intent)
        }
        buttonWait.setOnClickListener {
            //忽略
            dismissAllowingStateLoss()
        }
    }

    fun initButton(nowTxt:String){
        if (updateBean.must == 1){
            buttonUpload.visibility = View.VISIBLE
            buttonWait.visibility = View.GONE
        }else{
            buttonUpload.visibility = View.VISIBLE
            buttonWait.visibility = View.VISIBLE
        }
    }
}