package com.fly.mexicoapp.weight

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ToastUtils
import com.fly.mexicoapp.R
import com.fly.mexicoapp.bean.UpdateBean
import com.fly.mexicoapp.network.download.DownloadCallback
import com.fly.mexicoapp.utils.UploadUtil
import java.io.File

class UpdateDialog constructor(updateBean :UpdateBean): DialogFragment() {
    private var updateBean :UpdateBean
    private lateinit var tvContent:TextView
    private lateinit var progressbar:ProgressBar
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
        progressbar =rootView.findViewById(R.id.progressbar)
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
            buttonUpload.visibility = View.GONE
            buttonWait.visibility = View.GONE
            progressbar.visibility =View.VISIBLE
            //升级
            UploadUtil.startDownload(updateBean.url,updateBean.code,object : DownloadCallback{
                override fun onSuccess(file: File) {
                    initButton(resources.getString(R.string.update_update_instalar))
                }

                override fun onProgress(progress: Long, length: Long) {
                    val curProgress: Float = progress / length.toFloat()
                    val pro = (curProgress * 100).toInt()
                    progressbar.progress =pro
                }

                override fun onFail(errorInfo: String?) {
                    ToastUtils.showShort(errorInfo)
                }

            })
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
            progressbar.visibility =View.GONE
        }else{
            buttonUpload.visibility = View.VISIBLE
            buttonWait.visibility = View.VISIBLE
            progressbar.visibility =View.GONE
        }
    }
}