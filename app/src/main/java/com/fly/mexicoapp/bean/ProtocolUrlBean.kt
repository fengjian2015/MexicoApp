package com.fly.mexicoapp.bean

import java.io.Serializable

class ProtocolUrlBean :Serializable{
    var protocalName :String =""//协议名称
    //协议类型 1.隐私协议（登录页） 2.用户服务协议（登录页） 3.Contacts license agreement
    // 4.information collection and use rules  5.privacy policy 6. term and conditions 7.空合同模板
    var protocalType :Int =0
    var url :String =""//协议地址

}