package com.fly.cashhill.bean

import java.io.Serializable

class GroupEntity : Serializable{
    var groupId = 0
    var groupName: String? = null
    var memberName: String? = null
    var memberPhone: String? = null
    override fun toString(): String {
        return "GroupEntity(groupId=$groupId, groupName=$groupName, memberName=$memberName, memberPhone=$memberPhone)"
    }
}