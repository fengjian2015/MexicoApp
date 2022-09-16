package com.fly.cashhill.activity


import android.content.Intent
import android.text.TextUtils
import com.fly.cashhill.databinding.ActivityStartBinding
import com.fly.cashhill.utils.DeviceInfoUtil
import com.fly.cashhill.utils.UserInfoManger
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

class StartActivity : BaseActivity<ActivityStartBinding>(ActivityStartBinding::inflate) {

    override fun initView() {
        initP()
        binding.include2.toolbarTitle.text = "Permission"
        binding.include2.toolbarBack.setOnClickListener { finish() }
        binding.agree.setOnClickListener {
            DeviceInfoUtil.openLocService()
            DeviceInfoUtil.openWifi()
            XXPermissions.with(this@StartActivity) // 申请多个权限
                .permission(Permission.READ_SMS)
                .permission(Permission.READ_CONTACTS)
                .permission(Permission.GET_ACCOUNTS)
                .permission(*Permission.Group.STORAGE)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .permission(Permission.READ_PHONE_STATE)
                .permission(Permission.CAMERA)
                .permission(Permission.READ_CALL_LOG)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        if (all && DeviceInfoUtil.isLocServiceEnable() && DeviceInfoUtil.isOpenWifi()) {
                            if (UserInfoManger.getUserInfo() != null) {
                                UserInfoManger.getHomeUrl()?.let { it1 ->
                                    BaseWebActivity.openWebView( this@StartActivity, it1,true)
                                }
                            } else {
                                startActivity(Intent(this@StartActivity, LoginActivity::class.java))
                            }
                            this@StartActivity.finish()
                        }
                    }

                    override fun onDenied(permissions: List<String>, never: Boolean) {
                        if (never) {
                            XXPermissions.startPermissionActivity(this@StartActivity, permissions)
                        } else {
                        }
                    }
                })
        }
    }

    private fun initP(){
        if (XXPermissions.isGranted(
                this,
                Permission.ACCESS_FINE_LOCATION,
                Permission.ACCESS_COARSE_LOCATION,
                Permission.CAMERA,
                Permission.READ_PHONE_STATE,
                Permission.READ_SMS,
                Permission.READ_CONTACTS,
                Permission.GET_ACCOUNTS,
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.READ_CALL_LOG
            )
        ){
            if (!DeviceInfoUtil.isLocServiceEnable() || !DeviceInfoUtil.isOpenWifi()) {
                return
            }
            if (TextUtils.isEmpty(UserInfoManger.getUserInfoJson())){
                startActivity(Intent(this,LoginActivity::class.java))
            }else{
                UserInfoManger.getHomeUrl()?.let { it1 ->
                    BaseWebActivity.openWebView( this@StartActivity, it1,true)
                }
            }
            finish()
        }else{
            return
        }
    }
}