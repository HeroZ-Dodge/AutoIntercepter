package com.dodge.intercepter

/**
 *  Created by linzheng on 2019/3/26.
 */

class IntercepterInfo {

    var annotation: String = ""
    var intercepterName: String = ""
    var intercepterMethod: String = ""

    override fun toString(): String {
        return "IntercepterInfo(annotation='$annotation', intercepterName='$intercepterName', intercepterMethod='$intercepterMethod')"
    }


}